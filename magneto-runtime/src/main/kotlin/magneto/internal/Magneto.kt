package magneto.internal

import kotlin.reflect.KClass

object Magneto {

    private val registry by lazy {
        val registryClass = Class.forName("solomon.internal.Registry")
        registryClass.constructors.first().newInstance()
    }

    @Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
    fun <T : Any> getFactory(type: KClass<T>): T {
        return registry as T
    }
}
