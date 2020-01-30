package magneto.compiler.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.metadata.ImmutableKmType
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import kotlinx.metadata.KmClassifier
import magneto.compiler.failCompilation
import javax.lang.model.element.Element

@KotlinPoetMetadataPreview
internal fun ImmutableKmType.toTypeName(propertyName: String, element: Element): TypeName {
    val className = ClassName.bestGuess(
        classifier
            .getStringClassName(propertyName, element)
            .replace("/", ".")
    )
    return if (arguments.isEmpty()) className
    else className.parameterizedBy(
        arguments.map {
            val type = it.type ?: element.failCompilation(
                "@Scope can't be applied to $propertyName of $element: type argument must be a type: $it"
            )
            type.toTypeName(propertyName, element)
        }
    )
}

private fun KmClassifier.getStringClassName(propertyName: String, element: Element): String = when (this) {
    is KmClassifier.Class -> name
    is KmClassifier.TypeParameter -> element.failCompilation(
        "@Scope can't be applied to $propertyName of $element: typed properties are not supported"
    )
    is KmClassifier.TypeAlias -> element.failCompilation(
        "@Scope can't be applied to $propertyName of $element: type aliases are not supported"
    )
}