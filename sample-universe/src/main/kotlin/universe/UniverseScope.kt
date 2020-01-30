package universe

import magneto.Scope

@Scope
abstract class UniverseScope(
    val language: String
) {
    abstract val constellations: Set<Constellation>
}

@Scope
abstract class StellarScope(
    val distance: Int
) {
    abstract val name: String
}
