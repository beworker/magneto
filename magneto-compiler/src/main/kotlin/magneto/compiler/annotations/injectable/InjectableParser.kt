package magneto.compiler.annotations.injectable

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.*
import kotlinx.metadata.KmClassifier
import magneto.Injectable
import magneto.compiler.ProcessEnvironment
import magneto.compiler.failCompilation
import magneto.compiler.model.InjectableType
import magneto.compiler.model.ParameterType
import magneto.compiler.utils.forEachAttributeOf
import javax.lang.model.element.TypeElement

@KotlinPoetMetadataPreview
fun ProcessEnvironment.getInjectableTypes(): List<InjectableType> {
    val annotatedElements = round.getElementsAnnotatedWith(Injectable::class.java)
    return annotatedElements.mapNotNull { element ->
        if (element !is TypeElement) element.failCompilation(
            "@Injectable can't be applied to $element: must be a Kotlin class"
        )
        parseInjectableType(element)
    }
}

@KotlinPoetMetadataPreview
private fun ProcessEnvironment.parseInjectableType(element: TypeElement): InjectableType? {
    val typeMetadata = element.getAnnotation(Metadata::class.java)
        ?: element.failCompilation("@Injectable can't be applied to $element: must be a Kotlin class")

    val kmClass = try {
        typeMetadata.toImmutableKmClass()
    } catch (e: UnsupportedOperationException) {
        element.failCompilation("@Injectable can't be applied to $element: must be a Class type")
    }

    when {
        !kmClass.isClass -> element.failCompilation("@Injectable can't be applied to $element: must be a Kotlin class")
        kmClass.isEnum -> element.failCompilation("@Injectable can't be applied to $element: must not be enum")
        // fixme add more checks
    }

    var annotationType: TypeElement? = null
    element.forEachAttributeOf<Injectable> { name, value ->
        when (name) {
            "type" -> {
                annotationType = elements.getTypeElement(value.value.toString())
            }
        }
    }

    val interfaceType = annotationType
        ?: element.failCompilation("@Injectable.type must not be null")

    val interfaceTypeMetadata = interfaceType.getAnnotation(Metadata::class.javaObjectType)
        ?: element.failCompilation("@Injectable can't be applied to $interfaceType: must be a Kotlin class")

    val interfaceTypeKmClass = try {
        interfaceTypeMetadata.toImmutableKmClass()
    } catch (e: UnsupportedOperationException) {
        element.failCompilation("@Injectable can't be applied to $interfaceTypeMetadata: must be a Class type")
    }

    if (interfaceTypeKmClass.isInternal) return null

    if (kmClass.constructors.size > 1) {
        element.failCompilation("@Injectable can't be applied to $element: the class must have single constructor")
    }

    val parameters = mutableListOf<ParameterType>()
    val constructor = kmClass.constructors.first()

    constructor.valueParameters.forEach { parameter ->
        val classifier = parameter.type?.classifier
            ?: element.failCompilation(
                "@Injectable can't be applied to ${parameter.name} of $element: vararg are not supported"
            )

        val className = when (classifier) {
            is KmClassifier.Class -> classifier.name
            is KmClassifier.TypeParameter -> element.failCompilation(
                "@Injectable can't be applied to ${parameter.name} of $element: typed parameters are not supported"
            )
            is KmClassifier.TypeAlias -> element.failCompilation(
                "@Injectable can't be applied to ${parameter.name} of $element: type aliases are not supported"
            )
        }

        parameters += ParameterType(
            name = parameter.name,
            typeName = ClassName.bestGuess(className.replace("/", "."))
        )
    }

    return InjectableType(
        typeName = element.asType().asTypeName(),
        interfaceName = interfaceType.asType().asTypeName(),
        parameters = parameters
    )
}