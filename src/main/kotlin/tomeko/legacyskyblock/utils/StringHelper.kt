package tomeko.legacyskyblock.utils

object StringHelper {
    fun removeFormatting(string: String): String {
        return string.replace(Regex("§[0-9a-fk-or]"), "")
    }

    fun parseNumber(string: String?): Double? {
        val value = string?.replace(",", "") ?: return null
        return when {
            value.endsWith("k", ignoreCase = true) ->
                value.dropLast(1).toDoubleOrNull()?.times(1_000)

            value.endsWith("m", ignoreCase = true) ->
                value.dropLast(1).toDoubleOrNull()?.times(1_000_000)

            else -> value.toDoubleOrNull()
        }
    }
}