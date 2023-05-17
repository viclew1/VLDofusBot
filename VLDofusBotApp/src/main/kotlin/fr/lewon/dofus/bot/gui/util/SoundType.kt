package fr.lewon.dofus.bot.gui.util

import fr.lewon.dofus.bot.util.filemanagers.impl.GlobalConfigManager
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip


enum class SoundType(private val soundFileName: String, private val playSoundCondition: () -> Boolean = { true }) {

    ARCH_MONSTER_FOUND("arch_monster_found.wav", {
        GlobalConfigManager.readConfig().let { it.enableSounds && it.playArchMonsterSound }
    }),
    QUEST_MONSTER_FOUND("quest_monster_found.wav", {
        GlobalConfigManager.readConfig().let { it.enableSounds && it.playQuestMonsterSound }
    }),
    OBJECT_CRAFT("craft.wav"),
    FAILED("failed.wav"),
    SUCCEEDED("success.wav");

    private fun buildClip(): Clip {
        val inputStream = javaClass.getResourceAsStream("/sounds/$soundFileName")
            ?: error("Sound IS not found")
        val ais = AudioSystem.getAudioInputStream(BufferedInputStream(inputStream))
        val clip = AudioSystem.getClip()
        clip.open(ais)
        return clip
    }

    fun playSound(forcePlay: Boolean = false) {
        if (forcePlay || playSoundCondition()) {
            Thread { buildClip().start() }.start()
        }
    }

}