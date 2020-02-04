package magneto.internal

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class ScopeExtension(
    val version: Int = 0,
    val metadata: String
)