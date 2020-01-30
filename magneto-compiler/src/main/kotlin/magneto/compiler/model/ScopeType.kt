package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class ScopeType(
    val typeName: TypeName,
    val bounds: List<ParameterType>,
    val declarations: List<ParameterType>
)
