package fr.lewon.dofus.bot.util.math

object LevenshteinDistanceUtil {

    fun getClosestString(ref: String, choices: Collection<String>): String {
        return choices
            .map { it to computeDistance(ref, it) }
            .minByOrNull { it.second }
            ?.first ?: "?"
    }

    private fun computeDistance(lhs: String, rhs: String): Int {
        val distance =
            Array(lhs.length + 1) { IntArray(rhs.length + 1) }
        for (i in 0..lhs.length) {
            distance[i][0] = i
        }
        for (j in 1..rhs.length) {
            distance[0][j] = j
        }
        for (i in 1..lhs.length) {
            for (j in 1..rhs.length) {
                distance[i][j] = arrayOf(
                    distance[i - 1][j] + 1,
                    distance[i][j - 1] + 1,
                    distance[i - 1][j - 1] + if (lhs[i - 1] == rhs[j - 1]) 0 else 1
                ).minOrNull() ?: -1
            }
        }
        return distance[lhs.length][rhs.length]
    }

}