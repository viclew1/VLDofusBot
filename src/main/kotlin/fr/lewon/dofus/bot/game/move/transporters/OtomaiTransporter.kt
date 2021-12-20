package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.manager.DofusMapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.util.geometry.PointRelative

enum class OtomaiTransporter(idMap: Double, private val optionPointRelative: PointRelative) : ITransporter {

    PLAINES_HERBEUSES(159766.0, PointRelative(0.4706587f, 0.6961078f)),
    VILLAGE_DES_ELEVEURS(160260.0, PointRelative(0.4706587f, 0.7185629f)),
    VILLAGE_COTIER(156174.0, PointRelative(0.4706587f, 0.74251497f));

    private val map = DofusMapManager.getDofusMap(idMap)
    private val transporterMap = DofusMapManager.getDofusMap(20973058.0)

    override fun getTransporterMap(): DofusMap {
        return transporterMap
    }

    override fun getMap(): DofusMap {
        return map
    }

    override fun getNpcPointRelative(): PointRelative {
        return PointRelative(0.656f, 0.522f)
    }

    override fun getOptionPointRelative(): PointRelative {
        return optionPointRelative
    }

}