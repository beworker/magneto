package magneto.compiler.annotations.registry

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isInterface
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import magneto.Registry
import magneto.compiler.ProcessEnvironment
import magneto.compiler.failCompilation
import magneto.compiler.model.DependencyType
import magneto.compiler.model.InjectableType
import magneto.compiler.model.RegistryType
import magneto.compiler.model.ScopeType
import magneto.compiler.utils.forEachAttributeOf
import magneto.internal.InjectableFactory
import magneto.internal.ScopeExtension
import java.nio.charset.Charset
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import kotlin.Metadata
import magneto.compiler.protobuf.Metadata as ProtobufMetadata

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
        injectables = getEnclosedFactories(),
        scopes = getEnclosedScopes()
    )
}

private fun ProcessEnvironment.getEnclosedFactories(): List<InjectableType> {
    val injectables = mutableListOf<InjectableType>()
    val injectablesPackage = elements.getPackageElement("magneto.generated.factories")
    val injectableHolders = injectablesPackage.enclosedElements ?: emptyList()
    for (holder in injectableHolders) {
        for (child in holder.enclosedElements) {
            if (child.kind == ElementKind.METHOD) {
                child.forEachAttributeOf<InjectableFactory> { name, value ->
                    if (name == "metadata") {
                        val bytes = value.value.toString().toByteArray(Charset.forName("UTF-8"))
                        val injectable = ProtobufMetadata.Injectable.parseFrom(bytes)
                        val injectableType = injectable.type
                        injectables += InjectableType(
                            typeName = ClassName.bestGuess(injectableType),
                            interfaceTypeName = ClassName.bestGuess(injectable.interfaceType),
                            dependencies = injectable.dependencyList.map {
                                DependencyType(
                                    name = it.name,
                                    typeName = ClassName.bestGuess(it.type)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
    return injectables
}

private fun ProcessEnvironment.getEnclosedScopes(): List<ScopeType> {
    val scopes = mutableListOf<ScopeType>()
    val extensionsPackage = elements.getPackageElement("magneto.generated.extensions")
    val extensionHolders = extensionsPackage?.enclosedElements ?: emptyList()
    for (holder in extensionHolders) {
        if (holder.kind == ElementKind.INTERFACE) {
            holder.forEachAttributeOf<ScopeExtension> { name, value ->
                if (name == "metadata") {
                    val bytes = value.value.toString().toByteArray(Charset.forName("UTF-8"))
                    val scope = ProtobufMetadata.Scope.parseFrom(bytes)
                    val scopeTypeName = ClassName.bestGuess(scope.type)
                    scopes += ScopeType(
                        typeName = scopeTypeName,
                        parameters = scope.parameterList.map {
                            DependencyType(
                                name = it.name,
                                typeName = ClassName.bestGuess(it.type)
                            )
                        },
                        properties = scope.propertyList.map {
                            DependencyType(
                                name = it.name,
                                typeName = ClassName.bestGuess(it.type)
                            )
                        }
                    )
                }
            }
        }
    }
    return scopes
}
