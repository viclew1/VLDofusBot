package fr.lewon.dofus.bot.core.ui.managers

import fr.lewon.dofus.bot.core.ui.dat.DatUtil

object TransportSortingUtil {

    fun getZaapSortingMode(): SortingMode {
        return getSortingMode("zaapSelection_sortCriteria", "zaapSelection_descendingSort")
    }

    fun getZaapiSortingMode(): SortingMode {
        return getSortingMode("zaapiSelection_sortCriteria", "zaapiSelection_descendingSort")
    }

    private fun getSortingMode(sortCriteriaKey: String, descendingSortKey: String): SortingMode {
        val taxiData = getAccountTaxiData()
        val sortCriteria = taxiData[sortCriteriaKey].toString()
        val descendingSort = taxiData[descendingSortKey].toString().toBoolean()
        return SortingMode(sortCriteria, descendingSort)
    }

    fun getFavoriteZaapMapIds(): List<Double> {
        return getTaxiData()["favoriteZap"] as List<Double>? ?: emptyList()
    }

    private fun getAccountTaxiData(): HashMap<*, *> {
        return DatUtil.getDatFileContent("AccountModule_Ankama_Taxi.dat", HashMap::class.java)
            ?: HashMap<Any, Any>()
    }

    private fun getTaxiData(): HashMap<*, *> {
        return DatUtil.getDatFileContent("Module_Ankama_Taxi.dat", HashMap::class.java)
            ?: HashMap<Any, Any>()
    }

    data class SortingMode(val sortCriteria: String, val descendingSort: Boolean)

}