package magneto.compiler.annotations.registry

import com.squareup.kotlinpoet.*
import magneto.compiler.ProcessEnvironment
import magneto.compiler.annotations.FACTORY_PACKAGE
import magneto.compiler.annotations.getScopeExtensionInterfaceClassName
import magneto.compiler.annotations.requireClassName
import magneto.compiler.annotations.toFactoryFunctionName
import magneto.compiler.model.AnalyzedRegistryType
import magneto.compiler.model.AnalyzedScopeType
import magneto.compiler.model.ScopeRole

fun ProcessEnvironment.generateRegistry(registry: AnalyzedRegistryType) {
    for (scope in registry.scopes) {
        generateScopeExtension(scope)
    }
    // fixme generate registry
}

private fun ProcessEnvironment.generateScopeExtension(scope: AnalyzedScopeType) {
    val scopeInterfaceClassName = scope.typeName.getScopeExtensionInterfaceClassName()
    val scopeClassName = scope.typeName.getScopeExtensionClassName()
    FileSpec
        .builder(scopeClassName.packageName, scopeClassName.simpleName)
        .addType(
            TypeSpec
                .classBuilder(scopeClassName)
                .addSuperinterface(scopeInterfaceClassName)
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .apply {
                            for (parameter in scope.bound) {
                                addParameter(parameter.name, parameter.typeName)
                            }
                        }
                        .build()
                )
                .apply {
                    for (property in scope.bound) {
                        addProperty(
                            PropertySpec.builder(property.name, property.typeName)
                                .initializer(property.name)
                                .addModifiers(KModifier.OVERRIDE)
                                .build()
                        )
                    }
                    for (property in scope.properties) {
                        addProperty(
                            PropertySpec.builder(property.name, property.typeName)
                                .also {
                                    when (property.scopeRole) {
                                        ScopeRole.Inner -> it.addModifiers(KModifier.PRIVATE)
                                        ScopeRole.Exported -> it.addModifiers(KModifier.OVERRIDE)
                                    }
                                }
                                .delegate(
                                    CodeBlock.builder()
                                        .beginControlFlow("lazy")
                                        .also {
                                            val dependencies = property.injectable.dependencies
                                            val factoryName = property.injectable.typeName.toFactoryFunctionName()
                                            val factoryMemberName = MemberName(FACTORY_PACKAGE, factoryName)
                                            if (dependencies.isEmpty()) {
                                                it.addStatement("%M()", factoryMemberName)
                                            } else {
                                                val params = StringBuilder()
                                                for (dependency in dependencies) {
                                                    params.append(dependency.name).append(", ")
                                                }
                                                params.setLength(params.length - 2)
                                                it.addStatement("%M($params)", factoryMemberName)
                                            }
                                        }
                                        .endControlFlow()
                                        .build()
                                )
                                .build()
                        )
                    }
                }
                .build()
        )
        .build()
        .apply {
            writeTo(filer)
        }
}

private fun TypeName.getScopeExtensionClassName(): ClassName {
    val scopeName = requireClassName()
    val name = "${scopeName.packageName.replace(".", "_")}_Magneto${scopeName.simpleName}Extension"
    return ClassName("magneto.generated.extensions", name)
}
