import magneto.Registry
import universe.MagnetoUniverseScope

@Registry
interface MagnetoRegistry

fun main() {
    val scope = MagnetoUniverseScope("en")
    for (constellation in scope.constellations) {
        println(constellation.abbreviation)
        println(constellation.symbolism)
        println(constellation.stars)
    }
}
