package fr.lewon.dofus.bot.core.criterion.parse

enum class CriterionOperator(val char: Char, val checkFunc: (Int, Int) -> Boolean) {
    GREATER_THAN('>', { n1, n2 -> n1 > n2 }),
    LOWER_THAN('<', { n1, n2 -> n1 < n2 }),
    EQUALS('=', { n1, n2 -> n1 == n2 }),
    IS_DIFFERENT('!', { n1, n2 -> n1 != n2 })
}