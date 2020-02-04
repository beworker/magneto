package magneto.compiler.model

data class RegistryType(
    val injectables: Map<String, InjectableType>,
    val scopes: List<ScopeType>
)
