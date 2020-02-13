import universe.MagnetoUniverseScope

fun main() {

    val scope = MagnetoUniverseScope("en")
    println(scope.language)
    println(scope.typeA)
    println(scope.typeZ)

    /*
    val scope = Magneto.createScope(UniverseScope::class, "en")
    for (constellation in scope.constellations) {
        println(constellation.abbreviation)
        println(constellation.symbolism)
        println(constellation.stars)
    }
     */
}
