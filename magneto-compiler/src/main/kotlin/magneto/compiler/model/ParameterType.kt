package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class ParameterType(
    val name: String,
    val typeName: TypeName
)
