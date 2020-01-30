package magneto.compiler.model

data class RegistryType(
    val factories: Set<FactoryType>,
    val scopes: Set<ScopeType>
)
