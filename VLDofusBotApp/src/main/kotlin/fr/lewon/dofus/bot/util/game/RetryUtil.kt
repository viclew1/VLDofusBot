package fr.lewon.dofus.bot.util.game

object RetryUtil {

    fun <T> tryUntilSuccess(
        toTry: () -> T?,
        successChecker: (T?) -> Boolean,
        tryCount: Int,
        toCallAfterFail: () -> Unit = {}
    ): T? {
        var currentTryCount = 0
        while (currentTryCount++ < tryCount) {
            val result = toTry()
            if (successChecker(result)) {
                return result
            }
            toCallAfterFail()
        }
        return null
    }

    fun tryUntilSuccess(
        function: (tryCount: Int) -> Boolean,
        totalTries: Int,
        toCallAfterFail: () -> Unit = {}
    ): Boolean {
        var currentTryCount = 0
        while (currentTryCount++ < totalTries) {
            if (function(currentTryCount)) {
                return true
            }
            toCallAfterFail()
        }
        return false
    }

}