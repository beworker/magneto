package magneto.compiler.model

import com.squareup.kotlinpoet.TypeName

data class InjectableType(
    val typeName: TypeName,
    val interfaceTypeName: TypeName,
    val dependencies: List<DependencyType>
) {
    val typeId: String by lazy { typeName.toString() }
}
