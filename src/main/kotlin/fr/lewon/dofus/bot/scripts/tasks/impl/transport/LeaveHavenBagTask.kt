package fr.lewon.dofus.bot.scripts.tasks.impl.transport

class LeaveHavenBagTask : AbstractHavenBagTask(false) {

    override fun onStarted(): String {
        return "Leaving haven bag ..."
    }
}