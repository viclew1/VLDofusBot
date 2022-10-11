package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.spells

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellManager
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellVariantManager
import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import fr.lewon.dofus.bot.gui2.custom.CommonText
import fr.lewon.dofus.bot.gui2.custom.DefaultTooltipArea
import fr.lewon.dofus.bot.gui2.custom.defaultHoverManager
import fr.lewon.dofus.bot.gui2.main.DragTarget
import fr.lewon.dofus.bot.gui2.main.DropTarget
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.util.filemanagers.impl.SpellAssetManager

private val sectionBackgroundColor = Color(0xFF1b1b1b)
private val sectionBorderColor = Color(0xFF101010)
private val sectionBorder = BorderStroke(2.dp, sectionBorderColor)
private val keys = listOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')

@Composable
fun CharacterSpellsEditionContent(characterUIState: CharacterUIState) {
    Column(Modifier.padding(5.dp)) {
        Row(Modifier.fillMaxSize().weight(1f)) {
            AvailableSpells(characterUIState)
        }
        Spacer(Modifier.height(15.dp))
        CommonText("Spell bar", Modifier.padding(3.dp), fontWeight = FontWeight.SemiBold)
        CharacterSpellBar()
    }
}

@Composable
fun CharacterSpellBar() {
    val firstLineSpells = getSpellLine(false)
    val secondLineSpells = getSpellLine(true)
    Column(Modifier.fillMaxWidth().background(sectionBackgroundColor).border(sectionBorder).padding(4.dp)) {
        SpellsRow(firstLineSpells) { spell, index ->
            CharacterSpellContent(spell, keys[index], false)
        }
        Spacer(Modifier.height(2.dp))
        SpellsRow(secondLineSpells) { spell, index ->
            CharacterSpellContent(spell, keys[index], true)
        }
    }
}

private fun getSpellLine(ctrlModifier: Boolean): List<DofusSpell?> {
    return Array(10) {
        CharacterSpellsUIUtil.getSpellId(keys[it], ctrlModifier)?.spellId?.let(SpellManager::getSpell)
    }.toList()
}

@Composable
private fun CharacterSpellContent(spell: DofusSpell?, key: Char, ctrlModifier: Boolean) {
    val spellKey = SpellKey(key, ctrlModifier)
    val bgColor = remember { mutableStateOf(Color.Transparent) }
    Box(Modifier.fillMaxSize().background(bgColor.value)) {
        if (spell != null) {
            DragTarget(SpellDrag(spell, spellKey)) {
                SpellAssetManager.getIconPainter(spell.id)?.let { painter ->
                    Image(painter, "", Modifier.fillMaxSize().align(Alignment.Center))
                }
            }
        }
        DropTarget<SpellDrag>(Modifier.fillMaxSize()) { isInBound, draggedSpell ->
            bgColor.value = if (isInBound) AppColors.primaryColor else Color.Transparent
            if (isInBound && draggedSpell?.spell?.id != null && draggedSpell.fromSpellKey != spellKey) {
                val fromSpellKey = draggedSpell.fromSpellKey
                if (fromSpellKey != null && spellKey != fromSpellKey) {
                    CharacterSpellsUIUtil.updateSpellId(fromSpellKey.key, fromSpellKey.ctrlModifier, spell?.id)
                }
                CharacterSpellsUIUtil.updateSpellId(key, ctrlModifier, draggedSpell.spell.id)
            }
        }
    }
}

@Composable
private fun AvailableSpells(characterUIState: CharacterUIState) {
    val spells = SpellVariantManager.getSpellVariants(characterUIState.dofusClassId).sortedWith(compareBy(
        { it.spells.minOf { spell -> spell.levels.minOf { spellLevel -> spellLevel.minPlayerLevel } } },
        { it.id }
    )).flatMap { it.spells }
    val spellsPerLine = 8
    val spellsGroups = spells.chunked(spellsPerLine).takeIf { it.isNotEmpty() } ?: listOf(emptyList())
    DropTarget<SpellDrag>(Modifier.fillMaxSize()) { isInBound, draggedSpell ->
        if (isInBound && draggedSpell?.fromSpellKey != null) {
            val fromSpellKey = draggedSpell.fromSpellKey
            CharacterSpellsUIUtil.updateSpellId(fromSpellKey.key, fromSpellKey.ctrlModifier, null)
        }
        Box(Modifier.fillMaxSize().background(sectionBackgroundColor).border(sectionBorder)) {
            val state = rememberScrollState()
            Column(Modifier.padding(start = 4.dp, end = 14.dp, top = 4.dp, bottom = 2.dp).verticalScroll(state)) {
                for (spellGroup in spellsGroups) {
                    SpellsRow(spellGroup, spellsPerLine, false) { spell, _ ->
                        AvailableSpellContent(spell)
                    }
                    Spacer(Modifier.height(2.dp))
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(state),
            )
        }
    }
}

@Composable
private fun AvailableSpellContent(spell: DofusSpell?) {
    Box(Modifier.fillMaxSize()) {
        if (spell != null) {
            DefaultTooltipArea(
                spell.name,
                tooltipAlignment = Alignment.CenterStart,
                shape = CutCornerShape(5, 25, 25, 5)
            ) {
                SpellAssetManager.getIconPainter(spell.id)?.let { painter ->
                    DragTarget(SpellDrag(spell), Modifier.fillMaxSize()) {
                        Image(painter, "", Modifier.fillMaxSize().align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Composable
private fun SpellsRow(
    spells: List<DofusSpell?>,
    lineSize: Int = 10,
    displayNullSpells: Boolean = true,
    spellContent: @Composable (spell: DofusSpell?, index: Int) -> Unit
) {
    Row {
        for (index in 0 until lineSize) {
            val startPadding = if (index == 0) 0.dp else 2.dp
            val isHovered = remember { mutableStateOf(false) }
            Row(Modifier.fillMaxWidth().weight(1f).aspectRatio(1f).padding(start = startPadding)) {
                val spell = spells.getOrNull(index)
                if (spell != null || displayNullSpells) {
                    val bgColor = if (isHovered.value) Color.Gray else AppColors.backgroundColor
                    Box(
                        Modifier.fillMaxSize().background(bgColor).defaultHoverManager(isHovered)
                            .border(BorderStroke(1.dp, sectionBorderColor))
                    ) {
                        spellContent(spell, index)
                    }
                }
            }
        }
    }
}