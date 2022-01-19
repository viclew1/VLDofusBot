package fr.lewon.dofus.bot.game.move.transporters

import fr.lewon.dofus.bot.core.manager.d2o.managers.MapManager
import fr.lewon.dofus.bot.core.model.maps.DofusMap

enum class FrigostTransporter(idMap: Double, private val optionIndex: Int) : ITransporter {

    CHAMPS_DE_GLACE(54167842.0, 4),
    BERCEAU_ALMA(54161738.0, 3),
    LARMES_OURONIGRIDE(54168407.0, 2),
    CREVASSE_PERGE(54173010.0, 1);

    private val map = MapManager.getDofusMap(idMap)
    private val transporterMap = MapManager.getDofusMap(60035079.0)

    override fun getTransporterMap(): DofusMap {
        return transporterMap
    }

    override fun getMap(): DofusMap {
        return map
    }

    override fun getNpcId(): Int {
        return 1286
    }

    override fun getOptionIndex(): Int {
        return optionIndex
    }

}