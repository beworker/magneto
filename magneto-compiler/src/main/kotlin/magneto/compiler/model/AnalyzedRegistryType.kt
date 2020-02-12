package magneto.compiler.model

data class AnalyzedRegistryType(
    val injectables: List<InjectableType>,
    val scopes: List<AnalyzedScopeType>
)