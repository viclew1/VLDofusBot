package fr.lewon.dofus.bot.gui.main.scripts.characters.edit.spells

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.lewon.dofus.bot.core.d2o.managers.characteristic.BreedManager
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellManager
import fr.lewon.dofus.bot.core.d2o.managers.spell.SpellVariantManager
import fr.lewon.dofus.bot.core.model.spell.DofusSpell
import fr.lewon.dofus.bot.core.model.spell.DofusSpellVariant
import fr.lewon.dofus.bot.gui.custom.*
import fr.lewon.dofus.bot.gui.main.DragTarget
import fr.lewon.dofus.bot.gui.main.DropTarget
import fr.lewon.dofus.bot.gui.main.TooltipPlacement
import fr.lewon.dofus.bot.gui.main.TooltipTarget
import fr.lewon.dofus.bot.gui.main.scripts.characters.CharacterUIState
import fr.lewon.dofus.bot.gui.util.AppColors
import fr.lewon.dofus.bot.gui.util.UiResource
import fr.lewon.dofus.bot.model.characters.spells.CharacterSpell
import fr.lewon.dofus.bot.util.filemanagers.impl.SpellAssetManager

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
    Column(Modifier.fillMaxWidth().darkGrayBoxStyle().padding(4.dp)) {
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
                val spell = characterSpell.spellId?.let { SpellManager.getSpell(it) }
                if (spell != null) {
                    TooltipTarget(
                        key = spell.id,
                        tooltipContent = { SpellTooltipContent(spell) },
                        tooltipPlacement = TooltipPlacement.TopCornerAttached,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        SpellBox {
                            CharacterSpellContent(characterSpell)
                        }
                    }
                } else {
                    SpellBox {
                        CharacterSpellContent(characterSpell)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpellBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val isHovered = remember { mutableStateOf(false) }
    val bgColor = if (isHovered.value) Color.Gray else AppColors.backgroundColor
    Box(
        modifier.fillMaxSize().background(bgColor).defaultHoverManager(isHovered)
            .border(BorderStroke(1.dp, AppColors.VERY_DARK_BG_COLOR)).padding(1.dp)
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
private fun SpellImage(spell: DofusSpell, spellKey: SpellKey?) {
    val isParsedCompletely = spell.levels.all { it.isParsedCompletely }
    val isEffectsEmpty = spell.levels.any { it.effects.isEmpty() }
    DragTarget(SpellDrag(spell, spellKey)) {
        SpellAssetManager.getIconPainter(spell.id)?.let { painter ->
            Box(Modifier.fillMaxSize()) {
                Image(
                    painter,
                    "",
                    Modifier.fillMaxSize().align(Alignment.Center),
                    alpha = if (isEffectsEmpty) 0.3f else 1f
                )
                if (!isParsedCompletely) {
                    Image(
                        UiResource.WARNING.imagePainter,
                        "",
                        Modifier.fillMaxSize().align(Alignment.Center),
                        alpha = 0.6f,
                        colorFilter = ColorFilter.tint(if (isEffectsEmpty) Color.Black else Color.Yellow)
                    )
                }
            }
        }
    }
}

@Composable
private fun AvailableSpells(characterUIState: CharacterUIState) {
    val classSpells = getSpells(SpellVariantManager.getSpellVariants(characterUIState.dofusClassId))
    val additionalSpells = getSpells(SpellVariantManager.getSpellVariants(BreedManager.anyBreedId))
    DropTarget<SpellDrag>(Modifier.fillMaxSize()) { isInBound, draggedSpell ->
        if (isInBound && draggedSpell?.fromSpellKey != null) {
            val fromSpellKey = draggedSpell.fromSpellKey
            CharacterSpellsUIUtil.updateSpellId(fromSpellKey.key, fromSpellKey.ctrlModifier, null)
        }
        Box(Modifier.fillMaxSize().darkGrayBoxStyle().padding(5.dp)) {
            val state = rememberScrollState()
            VerticalGrid(
                columns = 8,
                modifier = Modifier.padding(end = 8.dp).verticalScroll(state),
                items = classSpells.plus(additionalSpells)
            ) { spell ->
                SpellBox(Modifier.padding(1.dp)) {
                    TooltipTarget(
                        key = spell.id,
                        tooltipContent = { SpellTooltipContent(spell) },
                        tooltipPlacement = TooltipPlacement.TopCornerAttached,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        SpellImage(spell, null)
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

@Composable
private fun SpellTooltipContent(spell: DofusSpell) {
    val isParsedCompletely = spell.levels.all { spellLevel -> spellLevel.isParsedCompletely }
    val isEffectsEmpty = spell.levels.any { spellLevel -> spellLevel.effects.isEmpty() }
    Column(Modifier.widthIn(max = 300.dp).padding(vertical = 10.dp)) {
        CommonText("${spell.name} (${spell.id})", fontWeight = FontWeight.Bold)
        if (isEffectsEmpty) {
            CommonText("(No effect parsed)")
        } else if (!isParsedCompletely) {
            CommonText("(Effects partially parsed)")
        }
        spell.levels.lastOrNull()?.effects?.takeIf { it.isNotEmpty() }?.let { effects ->
            HorizontalSeparator("Effects", modifier = Modifier.padding(vertical = 5.dp))
            effects.forEach {
                val targets = it.targets.joinToString(" / ") { target ->
                    val typeName = target.type.name
                    val typeIdSuffix = target.id?.let { id -> "($id)" } ?: ""
                    val casterOverwriteTargetStr = if (target.casterOverwriteTarget) "*" else ""
                    "$typeName$casterOverwriteTargetStr$typeIdSuffix"
                }
                CommonText("$targets => ${it.effectType.name}", fontSize = 11.sp)
            }
        }
    }
}

private fun getSpells(spellVariants: List<DofusSpellVariant>) = spellVariants.filter {
    it.spells.none { spell -> spell.adminName !in listOf("", "null") }
}.sortedWith(
    compareBy(
        { it.spells.minOfOrNull { spell -> spell.levels.minOf { spellLevel -> spellLevel.minPlayerLevel } } },
        { it.id })
).flatMap { it.spells }