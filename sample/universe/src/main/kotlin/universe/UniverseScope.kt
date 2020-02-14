package universe

import magneto.Scope

@Scope
abstract class UniverseScope(
    // bound instances
    private val defaultHemisphere: Hemisphere
) {
    // exported instances
    abstract val telescope: Telescope
    abstract val universe: Universe
}
