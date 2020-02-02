package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class DependencyType(
    val name: String,
    val typeName: TypeName
)
