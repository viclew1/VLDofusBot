package fr.lewon.dofus.bot.gui.alert

import fr.lewon.dofus.bot.util.filemanagers.ConfigManager
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip


enum class SoundType(private val soundFileName: String, private val playSoundCondition: () -> Boolean = { true }) {

    ARCH_MONSTER_FOUND("arch_monster_found.wav", { ConfigManager.config.playArchMonsterSound }),
    QUEST_MONSTER_FOUND("quest_monster_found.wav", { ConfigManager.config.playQuestMonsterSound }),
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

    fun playSound() {
        if (playSoundCondition()) {
            Thread {
                val clip = buildClip()
                clip.start()
            }.start()
        }
    }

}