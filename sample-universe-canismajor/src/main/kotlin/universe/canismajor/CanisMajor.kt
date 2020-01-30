package universe.canismajor

import magneto.Injectable
import universe.Constellation

@Injectable(type = Constellation::class)
internal class CanisMajor : Constellation {
    override val abbreviation = "CMa"
    override val symbolism = "The greater dog"
    override val stars = setOf("Sirius", "Adhara", "Wezen", "Mirzam", "Aludra")
}
