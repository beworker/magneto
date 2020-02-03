package magneto.compiler.annotations.registry

import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.isInterface
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import magneto.Registry
import magneto.compiler.ProcessEnvironment
import magneto.compiler.failCompilation
import magneto.compiler.model.RegistryType
import javax.lang.model.element.TypeElement

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

    val factoriesPackage = elements.getPackageElement("magneto.generated.factories")
    val factories = factoriesPackage.enclosedElements ?: emptyList()
    // todo

    return RegistryType(
        factories = emptySet(),
        scopes = emptySet()
    )
}
