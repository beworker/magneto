package universe

import magneto.Injectable

interface Telescope {
    val positionedAt: Hemisphere
    fun positionAt(hemisphere: Hemisphere)
}

enum class Hemisphere { NQ2, SQ1 }

@Injectable(type = Telescope::class)
internal class DefaultTelescope(
    defaultHemisphere: Hemisphere
) : Telescope {
    private var hemisphere: Hemisphere = defaultHemisphere

    override val positionedAt: Hemisphere
        get() = hemisphere

    override fun positionAt(hemisphere: Hemisphere) {
        this.hemisphere = hemisphere
    }
}
