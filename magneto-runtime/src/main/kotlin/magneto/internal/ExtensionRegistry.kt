package magneto.internal

import kotlin.reflect.KClass

interface ExtensionRegistry {
    fun <T : Any> createScopeExtension(type: KClass<T>, vararg args: Any): T
}
