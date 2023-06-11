package fr.lewon.dofus.bot.core

import fr.lewon.dofus.bot.core.d2o.D2OUtil
import fr.lewon.dofus.bot.core.d2p.elem.D2PElementsAdapter
import fr.lewon.dofus.bot.core.d2p.gfx.D2PItemsGfxAdapter
import fr.lewon.dofus.bot.core.d2p.gfx.D2PMonstersGfxAdapter
import fr.lewon.dofus.bot.core.d2p.gfx.D2PWorldGfxAdapter
import fr.lewon.dofus.bot.core.d2p.maps.D2PMapsAdapter
import fr.lewon.dofus.bot.core.i18n.I18NUtil
import fr.lewon.dofus.bot.core.io.gamefiles.VldbFilesUtil
import fr.lewon.dofus.bot.core.io.stream.ByteArrayReader
import fr.lewon.dofus.bot.core.ui.managers.UIIconManager
import fr.lewon.dofus.bot.core.ui.managers.XmlUiUtil
import fr.lewon.dofus.bot.core.world.WorldGraphUtil
import org.reflections.Reflections
import java.io.File

object VldbCoreInitializer {

    var DEBUG = false

    fun initAll(mapsDecryptionKey: String = "", mapsDecryptionKeyCharset: String = "") {
        processInitialization({ I18NUtil.init() }, "Initializing I18N ... ")
        processInitialization({ initAllD2O() }, "Initializing D2O ... ")
        processInitialization({ initAllD2P(mapsDecryptionKey, mapsDecryptionKeyCharset) }, "Initializing D2P ... ")
        processInitialization({ initVldbManagers() }, "Initializing VLDB managers ... \n")
        processInitialization({ initWorldGraph() }, "Initializing world graph ... ")
        processInitialization({ initUiIcons() }, "Initializing UI Icons ... ")
        processInitialization({ initUIXml() }, "Initializing UI XML ... ")
    }

    private fun initAllD2O() {
        val d2oPath = "${VldbFilesUtil.getDofusDirectory()}/data/common"
        File(d2oPath).listFiles()
            ?.filter { it.absolutePath.endsWith(".d2o") }
            ?.forEach { D2OUtil.init(it.absolutePath) }
            ?: error("Maps directory not found : $d2oPath}")
    }

    private fun initAllD2P(mapsDecryptionKey: String, mapsDecryptionKeyCharset: String) {
        D2PMapsAdapter.DECRYPTION_KEY = mapsDecryptionKey
        D2PMapsAdapter.DECRYPTION_KEY_CHARSET = mapsDecryptionKeyCharset
        val mapsPath = "${VldbFilesUtil.getDofusDirectory()}/content/maps"
        D2PElementsAdapter.initStream("$mapsPath/elements.ele")
        File(mapsPath).listFiles()
            ?.filter { it.absolutePath.endsWith(".d2p") }
            ?.forEach { D2PMapsAdapter.initStream(it.absolutePath) }
            ?: error("Maps directory not found : $mapsPath}")
        val worldGfxPath = "${VldbFilesUtil.getDofusDirectory()}/content/gfx/world"
        File(worldGfxPath).listFiles()
            ?.filter { it.absolutePath.endsWith(".d2p") }
            ?.forEach { D2PWorldGfxAdapter.initStream(it.absolutePath) }
            ?: error("World gfx directory not found : $worldGfxPath}")
        val itemsGfxPath = "${VldbFilesUtil.getDofusDirectory()}/content/gfx/items"
        File(itemsGfxPath).listFiles()
            ?.filter { it.absolutePath.endsWith(".d2p") }
            ?.forEach { D2PItemsGfxAdapter.initStream(it.absolutePath) }
            ?: error("Items gfx directory not found : $worldGfxPath}")
        val monstersGfxPath = "${VldbFilesUtil.getDofusDirectory()}/content/gfx/monsters"
        File(monstersGfxPath).listFiles()
            ?.filter { it.absolutePath.endsWith(".d2p") }
            ?.forEach { D2PMonstersGfxAdapter.initStream(it.absolutePath) }
            ?: error("Monsters gfx directory not found : $worldGfxPath}")
    }

    private fun initVldbManagers() {
        val basePackageName = VldbManager::class.java.packageName
        val managers = Reflections(basePackageName)
            .getSubTypesOf(VldbManager::class.java)
            .mapNotNull { it.kotlin.objectInstance }

        val initializedManagers = ArrayList<VldbManager>()
        managers.forEach { initManager(it, initializedManagers) }
    }

    private fun initManager(
        manager: VldbManager,
        initializedManagers: ArrayList<VldbManager>
    ) {
        manager.getNeededManagers().forEach {
            initManager(it, initializedManagers)
        }
        if (!initializedManagers.contains(manager)) {
            val startMessage = " - Initializing manager [${manager::class.java.simpleName}] ... "
            processInitialization({ manager.initManager() }, startMessage)
            initializedManagers.add(manager)
        }
    }

    private fun initWorldGraph() {
        val worldGraphPath = "${VldbFilesUtil.getDofusDirectory()}/content/maps/world-graph.binary"
        val worldGraphFile = File(worldGraphPath)
        if (!worldGraphFile.exists() || !worldGraphFile.isFile) {
            error("World graph file not found")
        }
        WorldGraphUtil.init(ByteArrayReader(worldGraphFile.readBytes()))
    }

    private fun initUiIcons() {
        val themeDir = File("${VldbFilesUtil.getDofusDirectory()}/content/themes/darkStone")
        initUiIcons(themeDir)
        themeDir.listFiles()?.filter { it.absolutePath.endsWith(".json") }
            ?.forEach { UIIconManager.initThemeData(it) }
            ?: error("Theme data files not found : ${themeDir.absolutePath}")
    }

    private fun initUiIcons(dir: File) {
        val iconFiles = dir.listFiles()?.filter { it.absolutePath.endsWith(".png") }
            ?: error("Icon files not found : ${dir.absolutePath}")
        iconFiles.forEach { UIIconManager.initIcon(it) }
        dir.listFiles()?.filter { it.isDirectory }?.forEach { initUiIcons(it) }
    }

    private fun initUIXml() {
        initUIXml(File("${VldbFilesUtil.getDofusDirectory()}/ui"))
        XmlUiUtil.initAllContainers()
    }

    private fun initUIXml(dir: File) {
        val xmlFiles = dir.listFiles()?.filter { it.absolutePath.endsWith(".xml") }
            ?: error("XML files not found : ${dir.absolutePath}")
        xmlFiles.forEach { XmlUiUtil.init(it) }
        dir.listFiles()?.filter { it.isDirectory }?.forEach { initUIXml(it) }
    }

    private fun processInitialization(initialization: () -> Unit, startMessage: String) {
        print(startMessage)
        val startTime = System.currentTimeMillis()
        initialization()
        val duration = System.currentTimeMillis() - startTime
        println("OK - $duration millis")
    }

}