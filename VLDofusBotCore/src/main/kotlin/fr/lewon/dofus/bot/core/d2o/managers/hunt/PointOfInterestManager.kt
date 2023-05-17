package fr.lewon.dofus.bot.core.d2o.managers.hunt

import fr.lewon.dofus.bot.core.VldbManager
import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.model.hunt.DofusPointOfInterest

object PointOfInterestManager : VldbManager {

    private lateinit var poiById: Map<Int, DofusPointOfInterest>

    override fun initManager() {
        poiById = D2OUtil.getObjects("PointOfInterest").associate {
            val id = it["id"].toString().toInt()
            val labelId = it["nameId"].toString().toInt()
            val label = I18NUtil.getLabel(labelId) ?: "UNKNOWN POI LABEL"
            id to DofusPointOfInterest(id, label)
        }
    }

    override fun getNeededManagers(): List<VldbManager> {
        return emptyList()
    }

    fun getPointOfInterest(poiId: Int): DofusPointOfInterest? {
        return poiById[poiId]
    }

}