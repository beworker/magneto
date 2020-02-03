package magneto.internal

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class Factory(
    val version: Int = 0,
    val data: String
)
