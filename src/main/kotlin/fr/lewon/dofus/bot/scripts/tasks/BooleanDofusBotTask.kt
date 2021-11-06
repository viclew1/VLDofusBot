package fr.lewon.dofus.bot.scripts.tasks

abstract class BooleanDofusBotTask : DofusBotTask<Boolean>() {

    override fun onSucceeded(value: Boolean): String {
        return if (value) super.onSucceeded(value) else "KO"
    }
    
}