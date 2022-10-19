package fr.lewon.dofus.bot.gui2.main.scripts.characters.edit.spells

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import fr.lewon.dofus.bot.gui2.custom.rememberScrollbarAdapter
import fr.lewon.dofus.bot.gui2.main.DragTarget
import fr.lewon.dofus.bot.gui2.main.DropTarget
import fr.lewon.dofus.bot.gui2.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui2.util.AppColors
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
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
    Column(Modifier.fillMaxWidth().background(sectionBackgroundColor).border(sectionBorder).padding(4.dp)) {
        SpellsRow(getSpellLine(false))
        SpellsRow(getSpellLine(true))
    }
}

private fun getSpellLine(ctrlModifier: Boolean): List<CharacterSpell> {
    return keys.map {
        CharacterSpellsUIUtil.getCharacterSpell(it, ctrlModifier) ?: CharacterSpell(null, it, ctrlModifier)
    }
}

@Composable
private fun SpellsRow(characterSpells: List<CharacterSpell>) {
    Row {
        for (characterSpell in characterSpells) {
            Row(Modifier.fillMaxWidth().weight(1f).aspectRatio(1f).padding(1.dp)) {
                SpellBox {
                    CharacterSpellContent(characterSpell)
                }
            }
        }
    }
}

@Composable
private fun SpellBox(content: @Composable () -> Unit) {
    val isHovered = remember { mutableStateOf(false) }
    val bgColor = if (isHovered.value) Color.Gray else AppColors.backgroundColor
    Box(
        Modifier.fillMaxSize().background(bgColor).defaultHoverManager(isHovered)
            .border(BorderStroke(1.dp, sectionBorderColor)).padding(1.dp)
    ) {
        content()
    }
}

@Composable
private fun CharacterSpellContent(characterSpell: CharacterSpell) {
    val key = characterSpell.key
    val ctrlModifier = characterSpell.ctrlModifier
    val spell = characterSpell.spellId?.let(SpellManager::getSpell)
    val spellKey = SpellKey(key, ctrlModifier)
    val bgColor = remember { mutableStateOf(Color.Transparent) }
    Box(Modifier.fillMaxSize().background(bgColor.value)) {
        spell?.let { SpellImage(spell, spellKey) }
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
private fun BoxScope.SpellImage(spell: DofusSpell, spellKey: SpellKey?) {
    DragTarget(SpellDrag(spell, spellKey)) {
        SpellAssetManager.getIconPainter(spell.id)?.let { painter ->
            Image(painter, "", Modifier.fillMaxSize().align(Alignment.Center))
        }
    }
}

@Composable
private fun AvailableSpells(characterUIState: CharacterUIState) {
    val spells = SpellVariantManager.getSpellVariants(characterUIState.dofusClassId).sortedWith(compareBy(
        { it.spells.minOf { spell -> spell.levels.minOf { spellLevel -> spellLevel.minPlayerLevel } } },
        { it.id }
    )).flatMap { it.spells }
    DropTarget<SpellDrag>(Modifier.fillMaxSize()) { isInBound, draggedSpell ->
        if (isInBound && draggedSpell?.fromSpellKey != null) {
            val fromSpellKey = draggedSpell.fromSpellKey
            CharacterSpellsUIUtil.updateSpellId(fromSpellKey.key, fromSpellKey.ctrlModifier, null)
        }
        Box(Modifier.fillMaxSize().background(sectionBackgroundColor).border(sectionBorder)) {
            val state = rememberLazyGridState()
            LazyVerticalGrid(
                columns = GridCells.Fixed(8),
                modifier = Modifier.padding(start = 3.dp, end = 13.dp, top = 3.dp, bottom = 3.dp),
                state = state
            ) {
                items(spells) {
                    SpellBox {
                        DefaultTooltipArea(
                            it.name,
                            tooltipAlignment = Alignment.CenterStart,
                            shape = CutCornerShape(5, 25, 25, 5)
                        ) {
                            SpellImage(it, null)
                        }
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.fillMaxHeight().width(8.dp).align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(state),
            )
        }
    }
}