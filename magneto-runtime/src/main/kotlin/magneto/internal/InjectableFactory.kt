package magneto.internal

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class InjectableFactory(
    val version: Int = 0,
    val metadata: String
)
