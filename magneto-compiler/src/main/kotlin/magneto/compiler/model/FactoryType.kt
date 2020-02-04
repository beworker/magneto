package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class FactoryType(
    val typeName: TypeName,
    val dependencies: List<DependencyType>
)
