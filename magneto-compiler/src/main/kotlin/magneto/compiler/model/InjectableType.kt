package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class InjectableType(
    val typeName: TypeName,
    val interfaceTypeName: TypeName,
    val parameters: List<ParameterType>
)
