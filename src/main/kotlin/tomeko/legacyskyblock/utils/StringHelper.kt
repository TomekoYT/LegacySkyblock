package tomeko.legacyskyblock.utils

fun String.removeFormatting(): String = replace(Regex("§[0-9A-FK-OR]", RegexOption.IGNORE_CASE), "")

fun String?.parseNumber(): Double? {
    val value = this?.replace(",", "") ?: return null

    val multiplier: Long = when (value.lastOrNull()?.lowercaseChar()) {
        'k' -> 1_000
        'm' -> 1_000_000
        'b' -> 1_000_000_000
        't' -> 1_000_000_000_000
        else -> 1
    }

    val number = if (multiplier == 1L) value else value.dropLast(1)
    return number.toDoubleOrNull()?.times(multiplier)
}