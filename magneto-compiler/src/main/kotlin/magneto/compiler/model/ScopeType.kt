package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class ScopeType(
    val typeName: TypeName,
    val bound: List<DependencyType>,
    val exported: List<DependencyType>
)
