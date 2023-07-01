package fr.lewon.dofus.bot.core.criterion

import fr.lewon.dofus.bot.core.criterion.parse.CriterionOperator
import fr.lewon.dofus.bot.core.criterion.simple.*
import fr.lewon.dofus.bot.core.model.charac.DofusCharacterBasicInfo
import fr.lewon.dofus.bot.core.utils.LockUtils.executeSyncOperation
import java.util.concurrent.locks.ReentrantLock

object DofusCriterionParser {

    private val lock = ReentrantLock()
    private val parsedCriteriaStore = HashMap<String, DofusCriterion>()
    private val customCriterionBuilderByKey =
        HashMap<String, (operator: CriterionOperator, expectedValue: String) -> DofusCriterion>()

    fun registerCustomCriterion(
        criterionStr: String,
        buildCriterion: (operator: CriterionOperator, expectedValue: String) -> DofusCriterion
    ) {
        lock.executeSyncOperation {
            customCriterionBuilderByKey[criterionStr] = buildCriterion
        }
    }

    fun parse(criterionStr: String): DofusCriterion {
        return lock.executeSyncOperation {
            parsedCriteriaStore.computeIfAbsent(criterionStr) { doParse(criterionStr) }
        }
    }

    private fun doParse(criterionStr: String): DofusCriterion {
        val criterionByKey = HashMap<String, DofusCriterion>()
        var trimmedCriterionStr = criterionStr.replace(" ", "")
        var keyIndex = 0
        while (trimmedCriterionStr.contains("(")) {
            val end = trimmedCriterionStr.indexOf(')')
            val start = trimmedCriterionStr.substring(0, end).lastIndexOf('(')
            val subStr = trimmedCriterionStr.substring(start + 1, end)
            val key = "{${keyIndex++}}"
            criterionByKey[key] = parseWithoutParenthesis(subStr, criterionByKey)
            trimmedCriterionStr = trimmedCriterionStr.replaceRange(start, end + 1, key)
        }
        return parseWithoutParenthesis(trimmedCriterionStr, criterionByKey)
    }

    private fun parseWithoutParenthesis(
        criterionStr: String,
        criterionByKey: HashMap<String, DofusCriterion>
    ): DofusCriterion {
        val orSplitBlocks = criterionStr.split("|")
        val criteria = orSplitBlocks.map {
            criterionByKey[it] ?: parseCriterionAndBlock(it, criterionByKey)
        }
        var criterion: DofusCriterion? = null
        for (subCriterion in criteria) {
            criterion = criterion?.or(subCriterion) ?: subCriterion
        }
        return criterion ?: DofusFalseCriterion
    }

    private fun parseCriterionAndBlock(
        criterionStr: String,
        criterionByKey: HashMap<String, DofusCriterion>
    ): DofusCriterion {
        val andSplitBlocks = criterionStr.split("&")
        val criteria = andSplitBlocks.map {
            criterionByKey[it] ?: parseSimpleCriterion(it)
        }
        var criterion: DofusCriterion? = null
        for (subCriterion in criteria) {
            criterion = criterion?.and(subCriterion) ?: subCriterion
        }
        return criterion ?: DofusFalseCriterion
    }

    private fun parseSimpleCriterion(criterionStr: String): DofusCriterion {
        val operator = CriterionOperator.values().firstOrNull { criterionStr.contains(it.char) }
            ?: return DofusFalseCriterion
        val splitCriterionStr = criterionStr.split(operator.char)
        val key = splitCriterionStr[0]
        val expectedValue = splitCriterionStr[1]
        return when (key) {
            "PG" -> DofusBreedCriterion(operator, expectedValue.toInt())
            "Qf" -> DofusQuestFinishedCriterion(expectedValue.toInt())
            "Qa" -> DofusQuestActiveCriterion(expectedValue.toInt())
            "Qo" -> DofusQuestObjectiveCriterion(operator, expectedValue.toInt())
            "HS" -> DofusHasStateCriterion(operator, expectedValue.toInt())
            else -> parseCustomCriterion(key, operator, expectedValue)
        }
    }

    private fun parseCustomCriterion(key: String, operator: CriterionOperator, expectedValue: String): DofusCriterion {
        return customCriterionBuilderByKey[key]?.invoke(operator, expectedValue)
            ?: DofusFalseCriterion
    }

    object DofusFalseCriterion : DofusCriterion() {
        override fun check(characterInfo: DofusCharacterBasicInfo): Boolean = false
    }

    object DofusTrueCriterion : DofusCriterion() {
        override fun check(characterInfo: DofusCharacterBasicInfo): Boolean = true
    }

}