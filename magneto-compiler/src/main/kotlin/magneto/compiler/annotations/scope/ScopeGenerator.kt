package magneto.compiler.annotations.scope

import com.squareup.kotlinpoet.*
import magneto.compiler.ProcessEnvironment
import magneto.compiler.model.ScopeType
import magneto.internal.Magneto

fun ProcessEnvironment.generateScopes(scopes: List<ScopeType>) {
    for (scope in scopes) {
        val scopeClassName = scope.typeName.getScopeClassName()
        val factoryClassName = scope.typeName.getFactoryClassName()

        FileSpec
            .builder(scopeClassName.packageName, scopeClassName.simpleName)
            .addType(
                TypeSpec
                    .classBuilder(scopeClassName)
                    .superclass(scope.typeName)
                    .also { typeBuilder ->
                        for (parameter in scope.bounds) {
                            typeBuilder.addSuperclassConstructorParameter(parameter.name)
                        }
                        if (scope.declarations.isNotEmpty()) {
                            typeBuilder
                                .addProperty(
                                    PropertySpec.builder("_factory", factoryClassName)
                                        .addModifiers(KModifier.PRIVATE)
                                        .delegate(
                                            "lazy { %T.getFactory(%T::class) }",
                                            Magneto::class,
                                            factoryClassName
                                        )
                                        .build()
                                )
                        }
                        for (override in scope.declarations) {
                            typeBuilder.addProperty(
                                PropertySpec.builder(override.name, override.typeName)
                                    .addModifiers(KModifier.OVERRIDE)
                                    .delegate("lazy { _factory.${override.name}(this) }")
                                    .build()
                            )
                        }
                    }
                    .primaryConstructor(
                        FunSpec
                            .constructorBuilder()
                            .also {
                                for (parameter in scope.bounds) {
                                    it.addParameter(parameter.name, parameter.typeName)
                                }
                            }
                            .build()
                    )
                    .build()
            )
            .build()
            .writeTo(filer)

        if (scope.declarations.isNotEmpty()) {
            FileSpec
                .builder(factoryClassName.packageName, factoryClassName.simpleName)
                .addType(
                    TypeSpec
                        .interfaceBuilder(factoryClassName)
                        .also { type ->
                            for (override in scope.declarations) {
                                type.addFunction(
                                    FunSpec
                                        .builder(override.name)
                                        .addModifiers(KModifier.ABSTRACT)
                                        .addParameter("scope", scope.typeName)
                                        .returns(override.typeName)
                                        .build()
                                )
                            }
                        }
                        .build()
                )
                .build()
                .also {
                    it.writeTo(filer)
                }
        }
    }
}

private fun TypeName.getScopeClassName(): ClassName =
    when (this) {
        is ClassName -> ClassName(packageName, "Magneto$simpleName")
        Dynamic -> TODO()
        is LambdaTypeName -> TODO()
        is ParameterizedTypeName -> TODO()
        is TypeVariableName -> TODO()
        is WildcardTypeName -> TODO()
    }

private fun TypeName.getFactoryClassName(): ClassName {
    val scopeName = getScopeClassName().canonicalName.replace(".", "_")
    return ClassName("magneto.factories", "scope_${scopeName}")
}
