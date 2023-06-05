package fr.lewon.dofus.bot.scripts.harvest

enum class JobSkills(val job: String, val skills: List<Int>) {
    Mow("Mow", listOf(45, 46, 50, 52, 53, 54, 57, 58, 159, 191, 296, 307, 308, 341)),
    Chop("Chop", listOf(10, 33, 34, 35, 37, 38, 39, 40, 139, 141, 154, 155, 158, 174, 190, 299, 306, 343, 411)),
    Fish("Fish", listOf(124, 125, 126, 127, 128, 129, 130, 131, 136, 189, 301, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 347, 352, 389)),
    Gather("Gather", listOf(68, 69, 71, 72, 73, 74, 160, 188, 300, 303, 304, 305, 338, 344, 386, 393)),
    Collect("Collect", listOf(24, 25, 26, 28, 29, 30, 31, 55, 56, 161, 162, 192, 298, 342, 391, 392, 410));

    companion object {
        fun checkSkillExists(job: String, skillId: Int): Boolean {
            val jobEnum = values().find { it.job == job }
            return jobEnum?.skills?.contains(skillId) ?: values().any { it.skills.contains(skillId) }
        }

        fun getJobList(): List<String> {
            return values().map { it.job }.sorted()
        }
    }
}