package magneto.generated.extensions2

import magneto.generated.factories2.create_universe_canismajor_CanisMajor2
import universe.Constellation
import universe.TypeA
import universe.TypeB
import universe.TypeC

// main module (under feature module)
class universe_MagnetoUniverseScopeExtension2(
    override val language: String
    // other already created instances (if any), never lists
) : universe_UniverseScopeExtension2 {

    // if constellations has items, we shall keep
    // separate private instances of those type as
    // implicit instances in the extension

    private val majorCanisConstellation: Constellation by lazy {
        create_universe_canismajor_CanisMajor2()
    }

    override val constellations: Set<Constellation> by lazy {
        emptySet<Constellation>()
    }

    override val typeA: TypeA by lazy { TypeA(typeC) }
    override val typeB: TypeB by lazy { TypeB() }
    override val typeC: TypeC? by lazy { TypeC(typeB) }
}
