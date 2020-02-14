package magneto.internal

import kotlin.reflect.KClass

object Magneto {

    private val registry by lazy {
        val registryClass = Magneto::class.java.classLoader
            .loadClass("magneto.generated.MagnetoExtensionRegistry")
        registryClass.constructors.first().newInstance() as ExtensionRegistry
    }

    fun <T : Any> createScopeExtension(type: KClass<T>, vararg args: Any): T {
        return registry.createScopeExtension(type, *args)
    }
}
