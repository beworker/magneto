package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class AnalyzedScopeType(
    val typeName: TypeName,
    val bound: List<DependencyType>,
    val properties: List<AnalyzedDependencyType>
)