package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.manager.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.util.geometry.PointRelative

enum class FrigostTransporter(idMap: Double, private val optionPointRelative: PointRelative) : ITransporter {

    CHAMPS_DE_GLACE(54167842.0, PointRelative(0.375f, 0.642f)),
    BERCEAU_ALMA(54161738.0, PointRelative(0.375f, 0.665f)),
    LARMES_OURONIGRIDE(54168407.0, PointRelative(0.375f, 0.689f)),
    CREVASSE_PERGE(54173010.0, PointRelative(0.375f, 0.714f));

    private val map = MapManager.getDofusMap(idMap)
    private val transporterMap = MapManager.getDofusMap(60035079.0)

    override fun getTransporterMap(): DofusMap {
        return transporterMap
    }

    override fun getMap(): DofusMap {
        return map
    }

    override fun getNpcPointRelative(): PointRelative {
        return PointRelative(0.370f, 0.683f)
    }

    override fun getOptionPointRelative(): PointRelative {
        return optionPointRelative
    }

}