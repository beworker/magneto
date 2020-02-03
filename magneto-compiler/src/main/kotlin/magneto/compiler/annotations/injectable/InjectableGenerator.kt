package magneto.compiler.annotations.injectable

import com.squareup.kotlinpoet.*
import magneto.compiler.ProcessEnvironment
import magneto.compiler.model.InjectableType
import magneto.compiler.protobuf.Metadata
import magneto.internal.Factory
import java.nio.charset.Charset

fun ProcessEnvironment.generateInjectables(types: List<InjectableType>) {
    for (type in types) {
        val injectorName = type.typeName.toCanonicalName()
        FileSpec
            .builder("magneto.generated.factories", injectorName)
            .addFunction(
                FunSpec.builder(injectorName)
                    .addAnnotation(
                        AnnotationSpec
                            .builder(Factory::class)
                            .addMember("data = %S", generateFactoryData(type))
                            .build()
                    )
                    .apply {
                        for (parameter in type.dependencies) {
                            addParameter(
                                parameter.name,
                                parameter.typeName
                            )
                        }
                    }
                    .returns(type.interfaceTypeName)
                    .apply {
                        if (type.dependencies.isEmpty()) addStatement("return %T()", type.typeName)
                        else addCode(
                            CodeBlock.builder()
                                .add("return %T(", type.typeName)
                                .apply {
                                    val lastIndex = type.dependencies.lastIndex
                                    for ((index, parameter) in type.dependencies.withIndex()) {
                                        val isLast = index == lastIndex
                                        if (isLast) add(parameter.name)
                                        else add("${parameter.name}, ")
                                    }
                                }
                                .add(")")
                                .build()
                        )
                    }
                    .build()
            )
            .build()
            .apply {
                writeTo(filer)
            }
    }
}

private fun TypeName.toCanonicalName(): String =
    when (this) {
        is ClassName -> canonicalName.replace(".", "_")
        Dynamic -> TODO()
        is LambdaTypeName -> TODO()
        is ParameterizedTypeName -> TODO()
        is TypeVariableName -> TODO()
        is WildcardTypeName -> TODO()
    }

private fun ProcessEnvironment.generateFactoryData(type: InjectableType): String =
    Metadata.Factory.newBuilder()
        .setType(type.typeName.toString())
        .apply {
            for (dependency in type.dependencies) {
                addDependency(
                    Metadata.Dependency.newBuilder()
                        .setName(dependency.name)
                        .setType(dependency.typeName.toString())
                        .build()
                )
            }
        }
        .build()
        .toByteArray()
        .toString(Charset.forName("UTF-8"))
