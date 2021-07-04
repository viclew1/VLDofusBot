package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.model.maps.DofusCoordinate
import fr.lewon.dofus.bot.util.geometry.PointRelative

enum class OtomaiTransporter(private val coordinate: DofusCoordinate, private val optionPointRelative: PointRelative) :
    ITransporter {

    PLAINES_HERBEUSES(DofusCoordinate(-56, 22), PointRelative(0.4706587f, 0.6961078f)),
    VILLAGE_DES_ELEVEURS(DofusCoordinate(-57, 4), PointRelative(0.4706587f, 0.7185629f)),
    VILLAGE_COTIER(DofusCoordinate(-49, 14), PointRelative(0.4706587f, 0.74251497f));


    override fun getTargetCoordinates(): DofusCoordinate {
        return coordinate
    }

    override fun getClosestZaap(): Zaap {
        return Zaap.OTOMAI_VILLAGE_CANOPEE
    }

    override fun getTransporterCoordinates(): DofusCoordinate {
        return DofusCoordinate(-54, 19)
    }

    override fun getNpcPointRelative(): PointRelative {
        return PointRelative(0.656f, 0.522f)
    }

    override fun getOptionPointRelative(): PointRelative {
        return optionPointRelative
    }
}