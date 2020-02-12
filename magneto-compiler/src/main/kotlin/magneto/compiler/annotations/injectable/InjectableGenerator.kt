package magneto.compiler.annotations.injectable

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import magneto.compiler.ProcessEnvironment
import magneto.compiler.annotations.FACTORY_PACKAGE
import magneto.compiler.annotations.toFactoryFunctionName
import magneto.compiler.model.InjectableType
import magneto.compiler.protobuf.Metadata
import magneto.internal.InjectableFactory
import java.nio.charset.Charset

fun ProcessEnvironment.generateInjectables(types: List<InjectableType>) {
    for (type in types) {
        val injectorName = type.typeName.toFactoryFunctionName()
        FileSpec
            .builder(FACTORY_PACKAGE, injectorName)
            .addFunction(
                FunSpec.builder(injectorName)
                    .addAnnotation(
                        AnnotationSpec
                            .builder(InjectableFactory::class)
                            .addMember("metadata = %S", generateInjectableMetadata(type))
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

private fun generateInjectableMetadata(type: InjectableType): String =
    Metadata.Injectable.newBuilder()
        .setType(type.typeName.toString())
        .setInterfaceType(type.interfaceTypeName.toString())
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
