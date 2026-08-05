package tomeko.legacyskyblock.neu

import tomeko.legacyskyblock.utils.JsonHelper.GSON
import java.nio.file.Files
import java.nio.file.Path

data class EnchantData(
    var enchants: Map<String, List<String>>? = null,
    var enchant_pools: List<List<String>>? = null,
    var enchants_xp_cost: Map<String, List<Int>>? = null
)

object NeuEnchantsHelper {
    @Volatile
    private var cachedEnchantData: EnchantData? = null

    fun getEnchantData(location: Path): EnchantData {
        try {
            cachedEnchantData?.let { return it }

            Files.newBufferedReader(location).use { reader ->
                val loaded = GSON.fromJson(reader, EnchantData::class.java) ?: EnchantData()
                cachedEnchantData = loaded
                return loaded
            }
        } catch (_: Exception) {
            return EnchantData()
        }
    }

    fun getEnchants(type: String, location: Path): List<String> {
        val enchants = getEnchantData(location).enchants ?: return emptyList()
        return enchants[type.uppercase()] ?: emptyList()
    }

    fun getEnchantPools(location: Path): List<List<String>> {
        return getEnchantData(location).enchant_pools ?: emptyList()
    }

    fun getMaxLevel(enchantId: String, location: Path): Int {
        val costs = getEnchantData(location).enchants_xp_cost?.get(enchantId)
        return if (!costs.isNullOrEmpty()) costs.size else -1
    }
}