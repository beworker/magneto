package magneto.compiler.annotations.registry

import com.squareup.kotlinpoet.*
import magneto.compiler.ProcessEnvironment
import magneto.compiler.annotations.getScopeExtensionInterfaceClassName
import magneto.compiler.annotations.requireClassName
import magneto.compiler.model.RegistryType
import magneto.compiler.model.ScopeType

fun ProcessEnvironment.generateRegistry(registry: RegistryType) {
    for (scope in registry.scopes) {
        generateScopeExtension(scope)
    }
}

private fun ProcessEnvironment.generateScopeExtension(scope: ScopeType) {
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
                            for (parameter in scope.parameters) {
                                addParameter(parameter.name, parameter.typeName)
                            }
                        }
                        .build()
                )
                .apply {
                    for (parameter in scope.parameters) {
                        addProperty(
                            PropertySpec.builder(parameter.name, parameter.typeName)
                                .initializer(parameter.name)
                                .addModifiers(KModifier.OVERRIDE)
                                .build()
                        )
                    }
                    for (property in scope.properties) {
                        addProperty(
                            PropertySpec.builder(property.name, property.typeName)
                                .addModifiers(KModifier.OVERRIDE)
                                .delegate("lazy { TODO() }")
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
