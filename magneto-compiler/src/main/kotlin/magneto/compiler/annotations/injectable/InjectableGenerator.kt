package magneto.compiler.annotations.injectable

import com.squareup.kotlinpoet.*
import magneto.compiler.ProcessEnvironment
import magneto.compiler.model.InjectableType
import magneto.internal.Factory

fun ProcessEnvironment.generateInjectables(types: List<InjectableType>) {
    for (type in types) {
        val injectorName = type.typeName.toCanonicalName()
        val file = FileSpec
            .builder("magneto.generated.factories", "injector_$injectorName")
            .addFunction(
                FunSpec.builder("create_$injectorName")
                    .addAnnotation(Factory::class)
                    .also {
                        for (parameter in type.dependencies) {
                            it.addParameter(
                                parameter.name,
                                parameter.typeName
                            )
                        }
                    }
                    .returns(type.interfaceTypeName)
                    .also {
                        if (type.dependencies.isEmpty()) it.addStatement("return %T()", type.typeName)
                        else it.addCode(
                            CodeBlock.builder()
                                .add("return %T(", type.typeName)
                                .also { code ->
                                    val lastIndex = type.dependencies.lastIndex
                                    for ((index, parameter) in type.dependencies.withIndex()) {
                                        val isLast = index == lastIndex
                                        if (isLast) code.add(parameter.name)
                                        else code.add("${parameter.name}, ")
                                    }
                                }
                                .add(")")
                                .build()
                        )
                    }
                    .build()
            )
            .build()
        file.writeTo(filer)
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
