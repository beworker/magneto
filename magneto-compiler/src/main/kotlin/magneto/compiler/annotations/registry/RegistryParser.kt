package magneto.compiler.annotations.registry

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isInterface
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import magneto.Registry
import magneto.compiler.ProcessEnvironment
import magneto.compiler.failCompilation
import magneto.compiler.model.DependencyType
import magneto.compiler.model.FactoryType
import magneto.compiler.model.RegistryType
import magneto.compiler.utils.forEachAttributeOf
import magneto.internal.Factory
import java.nio.charset.Charset
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import kotlin.Metadata
import magneto.compiler.protobuf.Metadata as FactoryMetadata

@KotlinPoetMetadataPreview
fun ProcessEnvironment.getRegistryType(): RegistryType? {
    val annotatedElements = round.getElementsAnnotatedWith(Registry::class.java)
    return when (annotatedElements.size) {
        0 -> null
        1 -> {
            val element = annotatedElements.first()
            if (element !is TypeElement) element.failCompilation(
                "@Scope can't be applied to $element: must be a Kotlin class"
            )
            parseRegistryType(element)
        }
        else -> {
            val element = annotatedElements.first()
            element.failCompilation(
                "@Registry can only be applied once, found ${annotatedElements.size} annotated elements."
            )
        }
    }
}

@KotlinPoetMetadataPreview
fun ProcessEnvironment.parseRegistryType(element: TypeElement): RegistryType {
    val typeMetadata = element.getAnnotation(Metadata::class.java)
        ?: element.failCompilation("@Registry can't be applied to $element: must be a Kotlin class")

    val kmClass = try {
        typeMetadata.toImmutableKmClass()
    } catch (e: UnsupportedOperationException) {
        element.failCompilation("@Registry can't be applied to $element: must be a Class type")
    }

    if (!kmClass.isInterface) element.failCompilation(
        "@Registry can't be applied to $element: must be a Kotlin interface"
    )

    return RegistryType(
        factories = getEnclosedFactories(),
        scopes = emptySet()
    )
}

private fun ProcessEnvironment.getEnclosedFactories(): Map<String, FactoryType> {
    val factories = mutableMapOf<String, FactoryType>()
    val factoriesPackage = elements.getPackageElement("magneto.generated.factories")
    val factoryHolders = factoriesPackage.enclosedElements ?: emptyList()
    for (holder in factoryHolders) {
        for (child in holder.enclosedElements) {
            if (child.kind == ElementKind.METHOD) {
                child.forEachAttributeOf<Factory> { name, value ->
                    if (name == "metadata") {
                        val bytes = value.value.toString().toByteArray(Charset.forName("UTF-8"))
                        val factory = FactoryMetadata.Factory.parseFrom(bytes)
                        val factoryType = factory.type
                        val factoryTypeName = ClassName.bestGuess(factoryType)
                        val dependencies = factory.dependencyList
                            .map {
                                DependencyType(
                                    name = it.name,
                                    typeName = ClassName.bestGuess(it.type)
                                )
                            }
                        factories[factoryType] = FactoryType(
                            typeName = factoryTypeName,
                            dependencies = dependencies
                        )
                    }
                }
            }
        }
    }
    return factories
}