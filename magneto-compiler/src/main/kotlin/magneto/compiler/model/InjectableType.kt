package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class InjectableType(
    val typeName: TypeName,
    val interfaceName: TypeName,
    val parameters: List<ParameterType>
)
