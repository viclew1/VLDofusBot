package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.model.maps.DofusCoordinates
import fr.lewon.dofus.bot.util.geometry.PointRelative

enum class FrigostTransporter(
    private val coordinates: DofusCoordinates,
    private val optionPointRelative: PointRelative
) :
    ITransporter {

    CHAMPS_DE_GLACE(DofusCoordinates(-68, -34), PointRelative(0.375f, 0.642f)),
    BERCEAU_ALMA(DofusCoordinates(-56, -74), PointRelative(0.375f, 0.665f)),
    LARMES_OURONIGRIDE(DofusCoordinates(-69, -87), PointRelative(0.375f, 0.689f)),
    CREVASSE_PERGE(DofusCoordinates(-78, -82), PointRelative(0.375f, 0.714f));

    override fun getTransporterCoordinates(): DofusCoordinates {
        return DofusCoordinates(-76, -66)
    }

    override fun getClosestZaap(): Zaap {
        return Zaap.FRIGOST_VILLAGE_ENSEVELI
    }

    override fun getCoordinates(): DofusCoordinates {
        return coordinates
    }

    override fun getNpcPointRelative(): PointRelative {
        return PointRelative(0.370f, 0.683f)
    }

    override fun getOptionPointRelative(): PointRelative {
        return optionPointRelative
    }

    override fun isAltWorld(): Boolean {
        return false
    }
}