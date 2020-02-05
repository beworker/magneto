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

internal fun TypeName.getScopeExtensionInterfaceClassName(): ClassName {
    val scopeName = getScopeClassName().canonicalName.replace(".", "_")
    return ClassName("magneto.generated.extensions", "${scopeName}Extension")
}
