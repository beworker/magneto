package magneto.generated.extensions2

import magneto.internal.ScopeExtension
import universe.Constellation
import universe.TypeA
import universe.TypeB
import universe.TypeC

// feature module (under magneto.generated.scopes)
//@ScopeExtension(metadata = "one")
interface universe_UniverseScopeExtension2 {

    // provided
    val language: String
    // other exported parent scope's variables

    // exported
    val constellations: Set<Constellation>
    val typeA: TypeA

    // implicit (used as dependencies, but not explicitly declared)
    val typeB: TypeB
    val typeC: TypeC?
}
