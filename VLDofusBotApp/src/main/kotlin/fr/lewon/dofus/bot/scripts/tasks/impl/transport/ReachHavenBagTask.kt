package fr.lewon.dofus.bot.scripts.tasks.impl.transport

class ReachHavenBagTask : AbstractHavenBagTask(true) {
    override fun onStarted(): String {
        return "Reaching haven bag ..."
    }
}