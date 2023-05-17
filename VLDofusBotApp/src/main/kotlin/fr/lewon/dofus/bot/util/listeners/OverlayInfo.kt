package fr.lewon.dofus.bot.util.listeners

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent
import fr.lewon.dofus.bot.overlay.AbstractOverlay
import fr.lewon.dofus.bot.overlay.impl.BreedingOverlay
import fr.lewon.dofus.bot.overlay.impl.GfxOverlay
import fr.lewon.dofus.bot.overlay.impl.LOSOverlay
import fr.lewon.dofus.bot.overlay.impl.UIOverlay

enum class OverlayInfo(
    val title: String,
    val description: String,
    val defaultDisplay: Boolean,
    val overlay: AbstractOverlay,
    val keys: List<Int>
) {

    LOS(
        "Line Of Sight (Ctrl + L)",
        "Displays available LOS on cell hover",
        true,
        LOSOverlay,
        listOf(NativeKeyEvent.VC_L, NativeKeyEvent.VC_CONTROL)
    ),
    BREEDING(
        "Breeding (Ctrl + B)",
        "Displays breeding info (DEV)",
        false,
        BreedingOverlay,
        listOf(NativeKeyEvent.VC_B, NativeKeyEvent.VC_CONTROL)
    ),
    UI(
        "UI (Ctrl + U)",
        "Displays UI XML parsing (DEV)",
        false,
        UIOverlay,
        listOf(NativeKeyEvent.VC_U, NativeKeyEvent.VC_CONTROL)
    ),
    GFX(
        "Gfx (Ctrl + G)",
        "Displays Gfx elements on current map (DEV)",
        false,
        GfxOverlay,
        listOf(NativeKeyEvent.VC_G, NativeKeyEvent.VC_CONTROL)
    );

}