package magneto.compiler.annotations.scope

import com.squareup.kotlinpoet.*
import magneto.compiler.ProcessEnvironment
import magneto.compiler.annotations.getScopeClassName
import magneto.compiler.annotations.getScopeExtensionInterfaceClassName
import magneto.compiler.model.ScopeType
import magneto.compiler.protobuf.Metadata
import magneto.internal.Magneto
import magneto.internal.ScopeExtension
import java.nio.charset.Charset

fun ProcessEnvironment.generateScopes(scopes: List<ScopeType>) {
    for (scope in scopes) {
        generateScopeExtensionInterface(scope)
        generateScope(scope)
    }
}

private fun ProcessEnvironment.generateScopeExtensionInterface(scope: ScopeType) {
    val interfaceClassName = scope.typeName.getScopeExtensionInterfaceClassName()
    FileSpec
        .builder(interfaceClassName.packageName, interfaceClassName.simpleName)
        .addType(
            TypeSpec
                .interfaceBuilder(interfaceClassName)
                .addAnnotation(
                    AnnotationSpec
                        .builder(ScopeExtension::class)
                        .addMember("metadata = %S", generateScopeMetadata(scope))
                        .build()
                )
                .apply {
                    for (parameter in scope.bound) {
                        addProperty(parameter.name, parameter.typeName)
                    }
                    for (property in scope.exported) {
                        addProperty(property.name, property.typeName)
                    }
                }
                .build()
        )
        .build()
        .apply {
            writeTo(filer)
        }
}

private fun ProcessEnvironment.generateScope(scope: ScopeType) {
    val scopeClassName = scope.typeName.getScopeClassName()
    val extensionInterfaceClassName = scope.typeName.getScopeExtensionInterfaceClassName()

    FileSpec
        .builder(scopeClassName.packageName, scopeClassName.simpleName)
        .addType(
            TypeSpec
                .classBuilder(scopeClassName)
                .superclass(scope.typeName)
                .apply {
                    addProperty(
                        PropertySpec
                            .builder("_extension", extensionInterfaceClassName)
                            .addModifiers(KModifier.PRIVATE)
                            .initializer(
                                CodeBlock
                                    .builder()
                                    .add("%T.createScopeExtension(", Magneto::class)
                                    .apply {
                                        val lastIndex = scope.bound.lastIndex
                                        if (lastIndex < 0) add("%T::class", extensionInterfaceClassName)
                                        else add("%T::class,", extensionInterfaceClassName)
                                        for ((index, parameter) in scope.bound.withIndex()) {
                                            if (index < lastIndex) add("${parameter.name},")
                                            else add(parameter.name)
                                        }
                                    }
                                    .add(")")
                                    .build()
                            )
                            .build()
                    )
                }
                .apply {
                    for (parameter in scope.bound) {
                        addSuperclassConstructorParameter(parameter.name)
                    }
                    for (property in scope.exported) {
                        addProperty(
                            PropertySpec.builder(property.name, property.typeName)
                                .addModifiers(KModifier.OVERRIDE)
                                .getter(
                                    FunSpec.getterBuilder()
                                        .addStatement("return _extension.%L", property.name)
                                        .build()
                                )
                                .build()
                        )
                    }
                }
                .primaryConstructor(
                    FunSpec
                        .constructorBuilder()
                        .apply {
                            for (parameter in scope.bound) {
                                addParameter(parameter.name, parameter.typeName)
                            }
                        }
                        .build()
                )
                .build()
        )
        .build()
        .apply {
            writeTo(filer)
        }
}

fun generateScopeMetadata(scope: ScopeType): String =
    Metadata.Scope.newBuilder()
        .setType(scope.typeName.toString())
        .apply {
            for (property in scope.exported) {
                addProperty(
                    Metadata.Dependency.newBuilder()
                        .setName(property.name)
                        .setType(property.typeName.toString())
                        .build()
                )
            }
            for (parameter in scope.bound) {
                addParameter(
                    Metadata.Dependency.newBuilder()
                        .setName(parameter.name)
                        .setType(parameter.typeName.toString())
                        .build()
                )
            }
        }
        .build()
        .toByteArray()
        .toString(Charset.forName("UTF-8"))
