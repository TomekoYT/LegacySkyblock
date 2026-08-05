package tomeko.legacyskyblock.neu

import tomeko.legacyskyblock.utils.Constants
import tomeko.legacyskyblock.utils.JsonHelper.getOrDownloadJson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Path

data class NeuItemStub(
    val itemId: String?,
    val damage: Int,
    val itemModel: String?,
    val skullTexture: String?
)

object NeuItemHelper {
    private val SKULL_TEXTURE_VALUE = Regex("Value:\"([A-Za-z0-9+/=]+)\"")
    private val ITEM_MODEL_ID = Regex("ItemModel:\"([a-z0-9_.:/-]+)\"")

    fun getNeuItem(internalName: String, location: Path): NeuItemStub? {
        val target = location.resolve("$internalName.json")
        val url = "${Constants.NEU_REPO_RAW_BASE_URL}items/${
            URLEncoder.encode(internalName, StandardCharsets.UTF_8).replace("+", "%20")
        }.json"

        val root = getOrDownloadJson(url, target)?.takeIf { it.isJsonObject }?.asJsonObject ?: return null

        val nbttag = root.get("nbttag")?.takeIf { it.isJsonPrimitive }?.asString
        return NeuItemStub(
            itemId = root.get("itemid")?.takeIf { it.isJsonPrimitive }?.asString,
            damage = root.get("damage")?.takeIf { it.isJsonPrimitive }?.asInt ?: 0,
            itemModel = nbttag?.let { ITEM_MODEL_ID.find(it)?.groupValues?.get(1) },
            skullTexture = nbttag?.let { SKULL_TEXTURE_VALUE.find(it)?.groupValues?.get(1) }
        )
    }
}