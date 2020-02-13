package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class AnalyzedDependencyType(
    val name: String,
    val typeName: TypeName,
    val injectable: InjectableType,
    val visibility: Visibility
)

enum class Visibility { Public, Private }
