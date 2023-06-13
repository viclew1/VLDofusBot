package fr.lewon.dofus.bot.core.d2p.maps.cell

class MapData(
    val completeCellDataByCellId: Map<Int, CompleteCellData>,
    val backgroundFixtures: List<Fixture>,
    val foregroundFixtures: List<Fixture>,
)