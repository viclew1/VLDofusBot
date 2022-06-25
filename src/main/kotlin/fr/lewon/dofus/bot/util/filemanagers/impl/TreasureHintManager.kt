package fr.lewon.dofus.bot.util.filemanagers.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.elem.graphical.impl.NormalGraphicalElementData
import fr.lewon.dofus.bot.core.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.core.d2p.maps.element.GraphicalElement
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.model.hunt.DofusPointOfInterest
import fr.lewon.dofus.bot.core.model.maps.DofusMap
import fr.lewon.dofus.bot.game.DofusBoard
import fr.lewon.dofus.bot.model.hint.GfxIdsByPoiLabel
import fr.lewon.dofus.bot.util.filemanagers.ToInitManager
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

object TreasureHintManager : ToInitManager {

    private lateinit var gfxIdsByPoiLabel: GfxIdsByPoiLabel
    private lateinit var gfxIdsByPoiLabelFile: File

    override fun initManager() {
        gfxIdsByPoiLabelFile = File("${VldbFilesUtil.getVldbConfigDirectory()}/hint_gfx_ids_by_label")
        if (gfxIdsByPoiLabelFile.exists()) {
            gfxIdsByPoiLabel = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readValue(gfxIdsByPoiLabelFile)
        } else {
            gfxIdsByPoiLabel = GfxIdsByPoiLabel()
            saveHintStoreContent()
        }
    }

    override fun getNeededManagers(): List<ToInitManager> {
        return emptyList()
    }

    private fun saveHintStoreContent() {
        with(OutputStreamWriter(FileOutputStream(gfxIdsByPoiLabelFile, false), StandardCharsets.UTF_8)) {
            write(ObjectMapper().writeValueAsString(gfxIdsByPoiLabel))
            close()
        }
    }

    fun isPointOfInterestOnMap(map: DofusMap, pointOfInterest: DofusPointOfInterest): Boolean {
        val gfxIds = gfxIdsByPoiLabel[pointOfInterest.label] ?: error("Unknown POI element")
        return D2PMapsAdapter.getCompleteCellDataByCellId(map.id)
            .flatMap { it.value.graphicalElements }
            .filter { isValidGraphicalElement(it) }
            .map { D2PElementsAdapter.getElement(it.elementId) }
            .filterIsInstance<NormalGraphicalElementData>()
            .map { it.gfxId }
            .intersect(gfxIds)
            .isNotEmpty()
    }

    private fun isValidGraphicalElement(ge: GraphicalElement): Boolean {
        val cellId = ge.cell.cellId
        val mapCellsCount = DofusBoard.MAP_CELLS_COUNT
        val mapWidth = DofusBoard.MAP_WIDTH
        val cellHalfHeight = D2PMapsAdapter.CELL_HALF_HEIGHT
        val cellHalfWidth = D2PMapsAdapter.CELL_HALF_WIDTH
        val topOk = cellId >= mapWidth * 2 || ge.pixelOffset.y >= 0
        val bottomOk = cellId <= mapCellsCount - mapWidth * 2 || ge.pixelOffset.y <= cellHalfHeight
        val divideLeftover = cellId % (mapWidth * 2)
        val leftOk = divideLeftover != 0 && divideLeftover != mapWidth
                || divideLeftover == 0 && ge.pixelOffset.x >= -cellHalfWidth
                || divideLeftover == mapWidth && ge.pixelOffset.x >= -2 * cellHalfWidth
        val rightOk = divideLeftover != mapWidth * 2 - 1 && divideLeftover != mapWidth - 1
                || divideLeftover == mapWidth * 2 - 1 && ge.pixelOffset.x <= cellHalfWidth * 1.5f
                || divideLeftover == mapWidth - 1 && ge.pixelOffset.x <= cellHalfWidth * 2.5f
        return topOk && bottomOk && leftOk && rightOk
    }

    fun addHintGfxMatch(pointOfInterestLabel: String, gfxId: Int) {
        gfxIdsByPoiLabel.computeIfAbsent(pointOfInterestLabel) { HashSet() }
            .add(gfxId)
        saveHintStoreContent()
    }

    fun removeHintGfxMatch(pointOfInterestLabel: String) {
        gfxIdsByPoiLabel.remove(pointOfInterestLabel)
        saveHintStoreContent()
    }
}


fun main() {
    val objectMapper = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val content1 =
        "{\"Dé en glace\":[400310],\"Canard en plastique\":[31149,70552],\"Crâne dans un trou\":[69466,69465,69460],\"Rose noire\":[63746],\"Chaussette à pois\":[400319],\"Œuf dans un trou\":[45689,69461,49032],\"Arbre glacé\":[69612],\"Plaque gravée d'un fantôme\":[400336],\"Tombe inondée\":[59594,69599],\"Affiche de carte au trésor\":[44786,16643,70547],\"Bouton de couture\":[70554],\"Flèche dans une pomme\":[400322],\"Casque à cornes\":[70545],\"Ceinture cloutée\":[70555,70556],\"Bougie dans un trou\":[69462],\"Bannière brâkmarienne déchirée\":[400315],\"Squelette d'Ouginak pendu\":[400335],\"Clef dorée\":[69518,69517],\"Pioche plantée\":[70566],\"Rocher taillé en arètes de poisson\":[69592],\"Théière à rayures\":[69469,69470],\"Poupée koalak\":[45690],\"Sève qui s'écoule\":[45683],\"Buisson poupée sadida\":[400318],\"Champignon rayé\":[69450,69451],\"Bannière bontarienne déchirée\":[40031],\"Tombe gravée d'un bouclier\":[69598],\"Plaque gravée d'un symbole égal\":[69444],\"Dessin koalak\":[28271,28267],\"Ancre dorée\":[69528],\"Menottes\":[15433],\"Tambour à rayures\":[69621],\"Crâne de cristal\":[69649],\"Blé noir et blanc\":[400311],\"Tombe inondée de sang\":[69599],\"Framboisier\":[70572],\"Serrure dorée\":[69447],\"Crâne de Roublard\":[69456],\"Statue koalak\":[69616,69617],\"Talisman en papier\":[69454,67086],\"Girouette dragodinde\":[69458],\"Anneau d'or\":[70562,70564,70561,36219],\"Crâne de likrone\":[400330],\"Œil de shushu peint\":[69467],\"Marionnette\":[600074],\"Fleurs smiley\":[69453],\"Crâne de Crâ\":[69457],\"Cadran solaire\":[70578],\"Plaque gravée d'un cœur\":[400324],\"Torii cassé\":[69629],\"Dofus en bois\":[69530],\"Canne à kebab\":[400327],\"Échelle cassée\":[600072],\"Kama peint\":[69414],\"Slip à petit cœur\":[69526],\"Grelot\":[52183,1614,1598,1597],\"Sucre d'orge\":[38472],\"Kaliptus grignoté\":[45700],\"Hache brisée\":[70546],\"Niche dans une caisse\":[70541],\"Poisson grillé embroché\":[70543],\"Minouki\":[69464],\"Plaque gravée d'un soleil\":[69441],\"Boule dorée de marin\":[69642],\"Panneau nonosse\":[45762],\"Étoile en papier plié\":[400320,400316,400321],\"Kaliptus à fleurs jaunes\":[69614],\"Rocher Dofus\":[69613],\"Bombe cœur\":[400328],\"Carapace de tortue\":[70579],\"Coquillage à pois\":[70534],\"Croix en pierre brisée\":[70537],\"Dessin dragodinde\":[28272],\"Sapin couché\":[70538],\"Paire de lunettes\":[70550],\"Crâne de likrone dans la glace\":[400331],\"Kaliptus coupé\":[45699,45698],\"Plaque gravée d'un crâne\":[69445,69477],\"Épouvantail à pipe\":[400317],\"Plaque gravée d'un logo Ankama\":[69475],\"Rose des vents dorée\":[69427],\"Queue d'Osamodas\":[20302],\"Bonbon bleu\":[400329],\"Fer à cheval\":[54480],\"Cairn\":[70548],\"Tricycle\":[38779],\"Stèle chacha\":[69618],\"Plaque gravée d'une flèche\":[69481],\"Os dans la lave\":[70568],\"Tissu à carreaux noué\":[69449],\"Rocher crâne\":[69620]}"
    val content2 =
        "{\"Canard en plastique\":[70552,31149],\"Crâne dans un trou\":[69460,69465,69466],\"Rose noire\":[63745,63746],\"Chaussette à pois\":[400319],\"Bouton de couture\":[70554],\"Flèche dans une pomme\":[400322],\"Ceinture cloutée\":[70555,70556],\"Bougie dans un trou\":[69462],\"Bannière brâkmarienne déchirée\":[400315],\"Poupée koalak\":[45690],\"Buisson poupée sadida\":[400318],\"Champignon rayé\":[69451,69450],\"Bannière bontarienne déchirée\":[400314,40031],\"Tombe gravée d'un bouclier\":[69598],\"Plaque gravée d'un symbole égal\":[69444],\"Ancre dorée\":[69528],\"Crâne de cristal\":[69649],\"Blé noir et blanc\":[400311],\"Tambour à rayures\":[69621],\"Tombe inondée de sang\":[69599],\"Rocher à sédimentation verticale\":[70536],\"Statue koalak\":[69617,69616],\"Girouette dragodinde\":[69458],\"Anneau d'or\":[70561,70562,70564,36219],\"Œil de shushu peint\":[69467],\"Marionnette\":[600074],\"Échelle cassée\":[600072],\"Kama peint\":[69414],\"Slip à petit cœur\":[52320,69526],\"Niche dans une caisse\":[70541],\"Poisson grillé embroché\":[70543],\"Minouki\":[69464],\"Boule dorée de marin\":[69642],\"Kaliptus à fleurs jaunes\":[69614],\"Rocher Dofus\":[69613],\"Carapace de tortue\":[70579],\"Croix en pierre brisée\":[70537],\"Dessin dragodinde\":[28272],\"Paire de lunettes\":[70550],\"Crâne de likrone dans la glace\":[400331],\"Kaliptus coupé\":[45698,45699],\"Plaque gravée d'un crâne\":[69445,69477],\"Plaque gravée d'un logo Ankama\":[69475],\"Rose des vents dorée\":[69427],\"Fer à cheval\":[54480],\"Tricycle\":[38779],\"Stèle chacha\":[69618],\"Plaque gravée d'une flèche\":[69474,69481],\"Plaque gravée d'un wukin\":[69483],\"Tissu à carreaux noué\":[69449],\"Rocher crâne\":[69620],\"Dé en glace\":[400310],\"Crâne de renne\":[70539],\"Œuf dans un trou\":[69461,45689],\"Arbre glacé\":[69612],\"Tombe inondée\":[59594,69599],\"Plaque gravée d'un fantôme\":[400336],\"Affiche de carte au trésor\":[44786,70547,16643],\"Casque à cornes\":[70545],\"Clef dorée\":[69517,69518],\"Squelette d'Ouginak pendu\":[400335],\"Pioche plantée\":[70567,70566],\"Rocher taillé en arètes de poisson\":[69592],\"Théière à rayures\":[69469,69470],\"Sève qui s'écoule\":[45683],\"Dessin koalak\":[28267,28271],\"Menottes\":[15433],\"Framboisier\":[70572],\"Plaque gravée d'une lune\":[69442],\"Serrure dorée\":[69447],\"Langue dans un trou\":[69463],\"Crâne de Roublard\":[69456],\"Talisman en papier\":[69454,67086],\"Crâne de likrone\":[400330],\"Fleurs smiley\":[69453],\"Crâne de Crâ\":[69457],\"Cadran solaire\":[70578],\"Plaque gravée d'un cœur\":[400324],\"Torii cassé\":[69629],\"Dofus en bois\":[69530],\"Canne à kebab\":[400327],\"Grelot\":[52183,1597,1614,1598],\"Plaque gravée d'un œil\":[69482],\"Kaliptus grignoté\":[45700],\"Sucre d'orge\":[38472],\"Hache brisée\":[70546],\"Plaque gravée d'un soleil\":[69441],\"Panneau nonosse\":[45762],\"Étoile en papier plié\":[400320,400321,400316],\"Bombe cœur\":[400328],\"Coquillage à pois\":[70534],\"Épouvantail à pipe\":[400317],\"Queue d'Osamodas\":[20302],\"Bonbon bleu\":[400329],\"Plaque gravée d'un symbole de quête\":[69473],\"Cairn\":[70548],\"Os dans la lave\":[70568]}"
    val obj1 = objectMapper.readValue<GfxIdsByPoiLabel>(content1)
    val obj2 = objectMapper.readValue<GfxIdsByPoiLabel>(content2)
    for (e in obj1.entries) {
        val hintLabel = e.key
        val hintGfxIds = e.value
        if (obj2.containsKey(hintLabel)) {
            obj2[hintLabel]?.addAll(hintGfxIds)
        } else {
            obj2[hintLabel] = hintGfxIds
        }
    }
    println(objectMapper.writeValueAsString(obj2))
}