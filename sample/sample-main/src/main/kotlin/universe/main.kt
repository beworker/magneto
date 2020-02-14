package universe

fun main() {
    val scope = MagnetoUniverseScope(Hemisphere.SQ1)
    val universe = scope.universe
    val telescope = scope.telescope

    universe.canisMajor.printVisibilityIn(telescope)
    universe.ursaMajor.printVisibilityIn(telescope)

    telescope.positionAt(Hemisphere.NQ2)
    universe.canisMajor.printVisibilityIn(telescope)
    universe.ursaMajor.printVisibilityIn(telescope)
}

private fun Constellation.printVisibilityIn(telescope: Telescope) {
    val visibility = if (isVisible) "visible" else "not visible"
    println("$name is $visibility in ${telescope.positionedAt}")
}
