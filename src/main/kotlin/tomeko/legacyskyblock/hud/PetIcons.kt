package tomeko.legacyskyblock.hud

import com.google.common.collect.ImmutableMultimap
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ResolvableProfile
import tomeko.legacyskyblock.utils.Constants
import tomeko.legacyskyblock.utils.JsonHelper
import tomeko.legacyskyblock.utils.NeuItemHelper
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object PetIcons {
    @Volatile
    private var cachedPetsData: PetsData? = null

    fun getPetsData(location: Path): PetsData {
        try {
            cachedPetsData?.let { return it }

            Files.newBufferedReader(location).use { reader ->
                val loaded = JsonHelper.GSON.fromJson(reader, PetsData::class.java) ?: PetsData()
                cachedPetsData = loaded
                return loaded
            }
        } catch (_: Exception) {
            return PetsData()
        }
    }

    fun getPetItemInternalName(coloredDisplayName: String, location: Path): String? {
        return getPetsData(location).pet_item_display_name_to_id?.get(coloredDisplayName)
    }

    data class PetsData(
        var pet_types: Map<String, String>? = null,
        var pet_item_display_name_to_id: Map<String, String>? = null
    )

    private val DATA_ROOT = FabricLoader.getInstance().configDir
        .resolve(Constants.MOD_ID)
        .resolve("data")

    private val ITEMS_ROOT = DATA_ROOT.resolve("items")
    private val PETS_CONSTANTS_PATH = DATA_ROOT.resolve("constants/pets.json")
    private const val PETS_CONSTANTS_URL = "${Constants.NEU_REPO_RAW_BASE_URL}constants/pets.json"

    private val RARITY_INDEX = mapOf(
        "COMMON" to 0,
        "UNCOMMON" to 1,
        "RARE" to 2,
        "EPIC" to 3,
        "LEGENDARY" to 4,
        "MYTHIC" to 5
    )

    private val iconCache = ConcurrentHashMap<String, ItemStack?>()

    private const val PREFETCH_THREAD_COUNT = 8

    fun register() {
        JsonHelper.downloadAndCacheJson(PETS_CONSTANTS_URL, PETS_CONSTANTS_PATH)
        prefetchAllIconsAsync()
    }

    private fun prefetchAllIconsAsync() {
        Thread({
            try {
                val data = getPetsData(PETS_CONSTANTS_PATH)
                val petIds = data.pet_types?.keys ?: emptySet()
                val petItemIds = data.pet_item_display_name_to_id?.values?.toSet() ?: emptySet()

                if (petIds.isEmpty() && petItemIds.isEmpty()) return@Thread

                val executor = Executors.newFixedThreadPool(PREFETCH_THREAD_COUNT) { r ->
                    Thread(r, "${Constants.MOD_ARCHIVES_NAME}-PetIconPrefetch-Worker").apply { isDaemon = true }
                }

                try {
                    for (petId in petIds) {
                        for (rarityIndex in RARITY_INDEX.values) {
                            executor.execute { getIcon("$petId;$rarityIndex") }
                        }
                    }

                    for (petItemId in petItemIds) {
                        executor.execute { getIcon(petItemId) }
                    }

                    executor.shutdown()
                    executor.awaitTermination(5, TimeUnit.MINUTES)
                } finally {
                    executor.shutdownNow()
                }
            } catch (_: Exception) {
            }
        }, "${Constants.MOD_ARCHIVES_NAME}-PetIconPrefetch").apply {
            isDaemon = true
            start()
        }
    }

    fun getPetIcon(petId: String, rarity: String?): ItemStack? {
        val rarityIndex = rarity?.let { RARITY_INDEX[it.uppercase(Locale.ROOT)] } ?: return null
        return getIcon("$petId;$rarityIndex")
    }

    fun getPetItemIcon(petItem: String?, petItemRarity: String?): ItemStack? {
        if (petItem.isNullOrBlank()) return null

        val colorCode = petItemRarity?.let { PetDisplay.getChatColorFromRarity(it) }
        val mappedName = colorCode?.let { getPetItemInternalName("$it$petItem", PETS_CONSTANTS_PATH) }
        if (mappedName != null) return getIcon(mappedName)

        val guess = petItem.uppercase(Locale.ROOT).replace(" ", "_")
        return getIcon(guess) ?: getIcon("PET_ITEM_$guess")
    }

    private fun getIcon(internalName: String): ItemStack? {
        if (iconCache.containsKey(internalName)) return iconCache[internalName]

        val icon = buildIcon(internalName)
        iconCache[internalName] = icon
        return icon
    }

    private fun buildIcon(internalName: String): ItemStack? {
        val neuItem = NeuItemHelper.getNeuItem(internalName, ITEMS_ROOT) ?: return null

        val texture = neuItem.skullTexture
        if (texture != null) {
            val builder = ImmutableMultimap.builder<String, Property>()
            builder.put("textures", Property("textures", texture))
            val properties = PropertyMap(builder.build())

            val profile = GameProfile(UUID.randomUUID(), internalName.take(16), properties)

            val stack = ItemStack(Items.PLAYER_HEAD)
            stack.set(DataComponents.PROFILE, ResolvableProfile.createResolved(profile))
            return stack
        }

        val itemModelId = neuItem.itemModel ?: return null
        val identifier = Identifier.tryParse(itemModelId) ?: return null
        val item = BuiltInRegistries.ITEM.get(identifier).orElse(null) ?: return null

        return ItemStack(item)
    }
}