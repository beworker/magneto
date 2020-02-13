package magneto.compiler.annotations.registry

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import magneto.compiler.ProcessEnvironment
import magneto.compiler.annotations.FACTORY_PACKAGE
import magneto.compiler.annotations.getScopeExtensionInterfaceClassName
import magneto.compiler.annotations.requireClassName
import magneto.compiler.annotations.toFactoryFunctionName
import magneto.compiler.model.AnalyzedRegistryType
import magneto.compiler.model.AnalyzedScopeType
import magneto.compiler.model.Visibility

fun ProcessEnvironment.generateRegistry(registry: AnalyzedRegistryType) {
    for (scope in registry.scopes) {
        generateScopeExtension(scope)
    }
    generateExtensionRegistry(registry)
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
                                    when (property.visibility) {
                                        Visibility.Public -> it.addModifiers(KModifier.OVERRIDE)
                                        Visibility.Private -> it.addModifiers(KModifier.PRIVATE)
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

fun ProcessEnvironment.generateExtensionRegistry(registry: AnalyzedRegistryType) {
    val extensionRegistryClassName = ClassName("magneto.generated", "MagnetoExtensionRegistry")
    val extensionRegistryInterfaceClassName = ClassName("magneto.internal", "ExtensionRegistry")
    val returnType = TypeVariableName("T", Any::class)
    val classType = ClassName("kotlin.reflect", "KClass").parameterizedBy(returnType)

    FileSpec
        .builder(extensionRegistryClassName.packageName, extensionRegistryClassName.simpleName)
        .addType(
            TypeSpec
                .classBuilder(extensionRegistryClassName)
                .addSuperinterface(extensionRegistryInterfaceClassName)
                .addFunction(
                    FunSpec.builder("createScopeExtension")
                        .addModifiers(KModifier.OVERRIDE)
                        .addAnnotation(
                            AnnotationSpec.builder(Suppress::class)
                                .addMember("\"UNCHECKED_CAST\"")
                                .build()
                        )
                        .addTypeVariable(returnType)
                        .addParameter("type", classType)
                        .addParameter("args", Any::class, KModifier.VARARG)
                        .returns(returnType)
                        .addCode(
                            CodeBlock.builder()
                                .also {
                                    if (registry.scopes.isNotEmpty()) {
                                        it.beginControlFlow("return when(type)")
                                        for (scope in registry.scopes) {
                                            val scopeClassName = scope.typeName.getScopeExtensionClassName()
                                            val scopeInterface = scope.typeName.getScopeExtensionInterfaceClassName()
                                            it.addStatement("%T::class ->", scopeInterface)
                                            it.indent()
                                            it.addStatement("%T(", scopeClassName)
                                            val lastIndex = scope.bound.lastIndex
                                            for ((index, parameter) in scope.bound.withIndex()) {
                                                val append = if (index == lastIndex) "" else ","
                                                it.addStatement("  args[$index] as %T$append", parameter.typeName)
                                            }
                                            it.addStatement(") as T")
                                            it.unindent()
                                        }
                                        it.addStatement("else -> error(\"Cannot find \$type\")")
                                        it.endControlFlow()
                                    } else {
                                        it.addStatement("else -> error(\"Cannot find \$type\")")
                                    }
                                }
                                .build()
                        )
                        .build()
                )
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
