package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.util.geometry.PointRelative

enum class OtomaiTransporter(
    private val coordinates: DofusCoordinates,
    private val optionPointRelative: PointRelative
) :
    ITransporter {

    PLAINES_HERBEUSES(DofusCoordinates(-56, 22), PointRelative(0.4706587f, 0.6961078f)),
    VILLAGE_DES_ELEVEURS(DofusCoordinates(-57, 4), PointRelative(0.4706587f, 0.7185629f)),
    VILLAGE_COTIER(DofusCoordinates(-49, 14), PointRelative(0.4706587f, 0.74251497f));


    override fun getTransporterCoordinates(): DofusCoordinates {
        return DofusCoordinates(-54, 19)
    }

    override fun getClosestZaap(): Zaap {
        return Zaap.OTOMAI_VILLAGE_CANOPEE
    }

    override fun getCoordinates(): DofusCoordinates {
        return coordinates
    }

    override fun getNpcPointRelative(): PointRelative {
        return PointRelative(0.656f, 0.522f)
    }

    override fun getOptionPointRelative(): PointRelative {
        return optionPointRelative
    }

    override fun isAltWorld(): Boolean {
        return false
    }
}