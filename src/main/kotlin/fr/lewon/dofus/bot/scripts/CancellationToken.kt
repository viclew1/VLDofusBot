package fr.lewon.dofus.bot.scripts

class CancellationToken(var cancel: Boolean = false) {

    fun checkCancel() {
        if (cancel) {
            error("Task has been canceled")
        }
    }
}