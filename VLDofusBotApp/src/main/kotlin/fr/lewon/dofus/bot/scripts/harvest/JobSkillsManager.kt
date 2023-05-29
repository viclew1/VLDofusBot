package fr.lewon.dofus.bot.scripts.harvest

data class JobSkill(val job: String, val skills: List<Int>)

class JobSkillsManager {
    private val jobSkills: List<JobSkill> = listOf(
        JobSkill("Mow", listOf(45, 46, 50, 52, 53, 54, 57, 58, 159, 191, 296, 307, 308, 341)),
        JobSkill("Chop", listOf(10, 33, 34, 35, 37, 38, 39, 40, 139, 141, 154, 155, 158, 174, 190, 299, 306, 343, 411)),
        JobSkill("Fish", listOf(124, 125, 126, 127, 128, 129, 130, 131, 136, 189, 301, 317, 318, 319, 320, 321, 322, 323, 324, 325, 326, 327, 328, 347, 352, 389)),
        JobSkill("Gather", listOf(68, 69, 71, 72, 73, 74, 160, 188, 300, 303, 304, 305, 338, 344, 386, 393)),
        JobSkill("Collect", listOf(24, 25, 26, 28, 29, 30, 31, 55, 56, 161, 162, 192, 298, 342, 391, 392, 410))
    )

    fun checkSkillExists(job: String, skillId: Int): Boolean {
        val filteredSkills = if (job == "") {
            jobSkills
        } else {
            jobSkills.filter { it.job == job }
        }

        return filteredSkills.any { filteredSkills -> filteredSkills.skills.contains(skillId) }
    }
}