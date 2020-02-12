package magneto.compiler.annotations

import com.squareup.kotlinpoet.*

internal fun TypeName.getScopeClassName(): ClassName =
    when (this) {
        is ClassName -> ClassName(packageName, "Magneto$simpleName")
        Dynamic -> TODO()
        is LambdaTypeName -> TODO()
        is ParameterizedTypeName -> TODO()
        is TypeVariableName -> TODO()
        is WildcardTypeName -> TODO()
    }

internal fun TypeName.requireClassName(): ClassName =
    when (this) {
        is ClassName -> ClassName(packageName, simpleName)
        else -> error("Cannot cast $this to ClassName")
    }

internal fun TypeName.getScopeExtensionInterfaceClassName(): ClassName {
    val scopeName = requireClassName()
    val name = "${scopeName.packageName.replace(".", "_")}_${scopeName.simpleName}Extension"
    return ClassName("magneto.generated.extensions", name)
}

internal fun TypeName.toFactoryFunctionName(): String =
    when (this) {
        is ClassName -> canonicalName.replace(".", "_")
        Dynamic -> TODO()
        is LambdaTypeName -> TODO()
        is ParameterizedTypeName -> TODO()
        is TypeVariableName -> TODO()
        is WildcardTypeName -> TODO()
    }

internal const val FACTORY_PACKAGE = "magneto.generated.factories"
