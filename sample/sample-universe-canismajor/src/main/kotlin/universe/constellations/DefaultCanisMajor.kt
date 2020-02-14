package universe.constellations

import magneto.Injectable
import universe.Hemisphere
import universe.Telescope
import universe.constellations.CanisMajor

@Injectable(type = CanisMajor::class)
internal class DefaultCanisMajor(
    private val telescope: Telescope
) : CanisMajor {
    override val abbreviation = "CMa"
    override val name = "The Greater Dog"
    override val stars = setOf("Sirius", "Adhara", "Wezen", "Mirzam", "Aludra")
    override val isVisible: Boolean
        get() = telescope.positionedAt == Hemisphere.SQ1
}
