package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.model.maps.DofusCoordinate
import fr.lewon.dofus.bot.util.geometry.PointRelative

enum class FrigostTransporter(private val coordinate: DofusCoordinate, private val optionPointRelative: PointRelative) :
    ITransporter {

    CHAMPS_DE_GLACE(DofusCoordinate(-68, -34), PointRelative(0.375f, 0.642f)),
    BERCEAU_ALMA(DofusCoordinate(-56, -74), PointRelative(0.375f, 0.665f)),
    LARMES_OURONIGRIDE(DofusCoordinate(-69, -87), PointRelative(0.375f, 0.689f)),
    CREVASSE_PERGE(DofusCoordinate(-78, -82), PointRelative(0.375f, 0.714f));

    override fun getTargetCoordinates(): DofusCoordinate {
        return coordinate
    }

    override fun getClosestZaap(): Zaap {
        return Zaap.FRIGOST_VILLAGE_ENSEVELI
    }

    override fun getTransporterCoordinates(): DofusCoordinate {
        return DofusCoordinate(-76, -66)
    }

    override fun getNpcPointRelative(): PointRelative {
        return PointRelative(0.370f, 0.683f)
    }

    override fun getOptionPointRelative(): PointRelative {
        return optionPointRelative
    }
}