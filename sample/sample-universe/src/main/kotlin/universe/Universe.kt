package universe

import magneto.Injectable
import universe.constellations.CanisMajor
import universe.constellations.UrsaMajor

@Injectable
class Universe(
    val ursaMajor: UrsaMajor,
    val canisMajor: CanisMajor
)
