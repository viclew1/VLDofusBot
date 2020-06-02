val globalStart = getTime()
while (getTime() - globalStart < 2 * 60 * 60 * 1000) {
    runScript("Fetch another hunt.kt")
    val start = getTime()
    try {
        runScript("Reach hunt start.kt")
        executeHunt()
        runScript("Fight.kt")
    } catch (e: Exception) {
        val duration = getTime() - start
        sleep(610000 - duration)
        if (getHuntPanel() != null) {
            clickChain(listOf("cancel_hunt.png", "ok_cancel.png"))
        }
    }
    clearCache()
}