package fr.lewon.dofus.bot.gui.alert

import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip


enum class SoundType(soundFileName: String) {

    RARE_MONSTER_FOUND("rare_monster_found.wav"),
    OBJECT_CRAFT("craft.wav"),
    FAILED("failed.wav");

    private val clip: Clip

    init {
        val ais = AudioSystem.getAudioInputStream(
            BufferedInputStream(javaClass.getResourceAsStream("/sounds/$soundFileName") ?: error("Sound IS not found"))
        )
        clip = AudioSystem.getClip()
        clip.open(ais)
    }

    fun playSound() {
        clip.microsecondPosition = 0
        clip.start()
    }

}