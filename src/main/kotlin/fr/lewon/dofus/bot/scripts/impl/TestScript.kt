package fr.lewon.dofus.bot.scripts.impl

import fr.lewon.dofus.bot.scripts.DofusBotScript
import fr.lewon.dofus.bot.scripts.DofusBotScriptParameter
import fr.lewon.dofus.bot.scripts.tasks.impl.moves.MoveTopTask
import fr.lewon.dofus.bot.scripts.tasks.impl.transport.ReachHavenBagTask
import fr.lewon.dofus.bot.util.logs.LogItem

object TestScript : DofusBotScript("Test") {


    override fun getParameters(): List<DofusBotScriptParameter> {
        return listOf()
    }

    override fun getStats(): List<Pair<String, String>> {
        return listOf()
    }

    override fun getDescription(): String {
        return "Test"
    }

    override fun execute(logItem: LogItem?) {
        MoveTopTask().run(logItem)
        MoveTopTask().run(logItem)
        ReachHavenBagTask().run(logItem)
    }

}