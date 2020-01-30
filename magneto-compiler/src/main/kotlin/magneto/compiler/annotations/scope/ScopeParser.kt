package magneto.compiler.annotations.scope

import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.*
import magneto.Scope
import magneto.compiler.ProcessEnvironment
import magneto.compiler.failCompilation
import magneto.compiler.model.ParameterType
import magneto.compiler.model.ScopeType
import magneto.compiler.utils.toTypeName
import javax.lang.model.element.TypeElement

@KotlinPoetMetadataPreview
fun ProcessEnvironment.getScopeTypes(): List<ScopeType> {
    val annotatedElements = round.getElementsAnnotatedWith(Scope::class.java)
    return annotatedElements.map { element ->
        if (element !is TypeElement) element.failCompilation(
            "@Scope can't be applied to $element: must be a Kotlin class"
        )
        parseScopeType(element)
    }
}

@KotlinPoetMetadataPreview
fun ProcessEnvironment.parseScopeType(element: TypeElement): ScopeType {
    val typeMetadata = element.getAnnotation(Metadata::class.java)
        ?: element.failCompilation("@Scope can't be applied to $element: must be a Kotlin class")

    val kmClass = try {
        typeMetadata.toImmutableKmClass()
    } catch (e: UnsupportedOperationException) {
        element.failCompilation("@Scope can't be applied to $element: must be a Class type")
    }

    when {
        !kmClass.isClass -> element.failCompilation("@Scope can't be applied to $element: must be a Kotlin class")
        kmClass.isEnum -> element.failCompilation("@Scope can't be applied to $element: must not be enum")
        // fixme add more checks
    }

    if (kmClass.constructors.size > 1) {
        element.failCompilation("@Scope can't be applied to $element: the class must have single constructor")
    }

    val parameters = mutableListOf<ParameterType>()
    kmClass.constructors.first().valueParameters.forEach { parameter ->
        val parameterType = parameter.type ?: element.failCompilation(
            "@Scope can't be applied to ${parameter.name} of $element: vararg are not supported"
        )
        parameters += ParameterType(
            name = parameter.name,
            typeName = parameterType.toTypeName(parameter.name, element)
        )
    }

    val overrides = mutableListOf<ParameterType>()
    for (property in kmClass.properties) {
        if (property.isAbstract) {
            overrides += ParameterType(
                name = property.name,
                typeName = property.returnType.toTypeName(property.name, element)
            )
        }
    }

    return ScopeType(
        typeName = element.asType().asTypeName(),
        bounds = parameters,
        declarations = overrides
    )
}
