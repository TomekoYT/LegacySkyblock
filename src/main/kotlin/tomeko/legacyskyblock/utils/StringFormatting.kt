package tomeko.legacyskyblock.utils

object StringFormatting {
    fun removeFormatting(string: String): String {
        return string.replace(Regex("§[0-9a-fk-or]"), "")
    }
}