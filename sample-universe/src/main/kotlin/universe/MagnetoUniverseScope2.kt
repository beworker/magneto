package universe

import magneto.generated.extensions2.universe_UniverseScopeExtension2
import magneto.internal.Magneto

// feature module (under feature package)
internal class MagnetoUniverseScope2(
    language: String
) : UniverseScope(language) {

    private val extension: universe_UniverseScopeExtension2 =
        Magneto.createScopeExtension(
            universe_UniverseScopeExtension2::class,
            language
        )

    // provided
    //override val name: InternalType by lazy { InternalType(typeA, typeB) }

    // exported
    //override val constellations: Set<Constellation> get() = extension.constellations
    override val typeA: TypeA get() = extension.typeA
    override val typeZ: TypeZ
        get() = TODO("implement")
    //override val typeA: TypeA by lazy { TypeA(typeC) }

    // implicit types
    private val typeB: TypeB get() = extension.typeB
}
