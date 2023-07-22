package fr.lewon.dofus.bot.gui.main.exploration.seenmonsters

import androidx.compose.ui.graphics.painter.Painter
import fr.lewon.dofus.bot.core.model.entity.DofusMonster
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.gui.util.UiResource

data class SeenMonstersUiState(
    val seenMonstersByMap: Map<DofusMap, List<SeenMonster>> = emptyMap(),
)

data class SeenMonster(
    val time: Long,
    val map: DofusMap,
    val monster: DofusMonster,
    val type: SeenMonsterType,
)

enum class SeenMonsterType(val iconPainter: Painter?) {
    Archmonster(UiResource.ARCHMONSTER.imagePainter),
    QuestMonster(UiResource.QUEST_MONSTER.imagePainter),
    Monster(null)
}