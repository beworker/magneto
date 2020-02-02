package magneto.generated

import magneto.generated.extensions.universe_MagnetoUniverseScopeExtension2
import magneto.generated.extensions.universe_UniverseScopeExtension2
import magneto.internal.ExtensionRegistry
import kotlin.reflect.KClass

// main module (under magneto.generated)
class MagnetoUniverseRegistry2 : ExtensionRegistry {
    override fun <T : Any> createScopeExtension(type: KClass<T>, vararg args: Any): T {
        @Suppress("UNCHECKED_CAST")
        return when (type) {
            universe_UniverseScopeExtension2::class ->
                universe_MagnetoUniverseScopeExtension2(
                    args[0] as String
                )
            else -> error("kaboom")
        } as T
    }
}
