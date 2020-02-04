package magneto.compiler.model

data class RegistryType(
    val factories: Map<String, FactoryType>,
    val scopes: List<ScopeType>
)
