package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class FactoryType(
    val packageName: String,
    val functionName: String,
    val returns: TypeName
)
