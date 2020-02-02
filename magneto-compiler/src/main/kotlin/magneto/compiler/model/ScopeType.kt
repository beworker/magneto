package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class ScopeType(
    val typeName: TypeName,
    val parameters: List<DependencyType>,
    val properties: List<DependencyType>
)
