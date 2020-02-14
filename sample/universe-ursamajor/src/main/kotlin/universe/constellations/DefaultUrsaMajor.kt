package universe.constellations

import magneto.Injectable
import universe.Hemisphere
import universe.Telescope

@Injectable(type = UrsaMajor::class)
internal class DefaultUrsaMajor(
    private val telescope: Telescope
) : UrsaMajor {
    override val abbreviation = "UMa"
    override val name = "The Great Bear"
    override val stars = setOf("Dubhe", "Merak", "Phecda", "Megrez", "Mizar", "Alkaid")
    override val isVisible: Boolean get() = telescope.positionedAt == Hemisphere.NQ2
}
