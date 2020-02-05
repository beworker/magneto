package magneto.compiler.model

data class RegistryType(
    val injectables: List<InjectableType>,
    val scopes: List<ScopeType>
)
