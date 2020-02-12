package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class AnalyzedDependencyType(
    val name: String,
    val typeName: TypeName,
    val injectable: InjectableType,
    val scopeRole: ScopeRole
)

enum class ScopeRole { Exported, Inner }
