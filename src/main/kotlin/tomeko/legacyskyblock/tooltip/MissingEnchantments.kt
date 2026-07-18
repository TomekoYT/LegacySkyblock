package tomeko.legacyskyblock.tooltip

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomData
import tomeko.legacyskyblock.config.LegacySkyblockConfig
import tomeko.legacyskyblock.utils.Constants
import tomeko.legacyskyblock.utils.HypixelPackets
import tomeko.legacyskyblock.utils.JsonHelper
import tomeko.legacyskyblock.utils.SkyblockIslands
import java.nio.file.Files
import java.util.BitSet
import java.util.Locale
import kotlin.collections.iterator
import kotlin.math.min

object MissingEnchantments {
    private val DATA_ROOT = FabricLoader.getInstance().configDir.resolve(Constants.MOD_ID).resolve("data")
    private val ENCHANTS_JSON_PATH = DATA_ROOT.resolve("constants/enchants.json")
    private const val ENCHANTS_JSON_URL =
        "https://raw.githubusercontent.com/NotEnoughUpdates/NotEnoughUpdates-REPO/master/constants/enchants.json"

    private const val MAX_LINE_WIDTH = 200
    private const val LIST_PREFIX = "› "

    private val ROMAN_NUMERALS = setOf(
        "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"
    )
    private val ROMAN = arrayOf("", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X")

    private val ITEM_TYPE_LINE = Regex(
        "(?:COMMON|UNCOMMON|RARE|EPIC|LEGENDARY|MYTHIC|DIVINE|VERY SPECIAL|SPECIAL)\\s+(?:DUNGEON\\s+)?" +
                "(SWORD|LONGSWORD|BOW|AXE|PICKAXE|DRILL|FISHING ROD|FISHING WEAPON|SHOVEL|FARMING TOOL|HELMET|CHESTPLATE|LEGGINGS|BOOTS|GAUNTLET|GLOVES|BELT|NECKLACE|BRACELET|CLOAK|CARNIVAL MASK)\\b"
    )

    private var lastItemType: String? = null
    private var lastCurrentEnchants: Map<String, Int> = emptyMap()
    private var lastMissingNamesSorted: List<String> = emptyList()
    private var lastNotMaxedNamesSorted: List<String> = emptyList()

    private var lastNormalizedTokens: List<String>? = null
    private var lastInsertIndex = -1

    private var lastRenderedForMissing: List<String> = emptyList()
    private var lastRenderedExpanded = false
    private var lastRenderBlock: List<Component> = emptyList()

    private var lastStack = ItemStack.EMPTY

    private val poolIdsByEnchant = mutableMapOf<String, MutableList<Int>>()
    private var poolCount = 0
    private var poolsLoaded = false
    private val prettyNameCache = HashMap<String, String>(512)

    fun register() {
        ItemTooltipCallback.EVENT.register(::onTooltip)
        JsonHelper.downloadAndCacheJson(ENCHANTS_JSON_URL, ENCHANTS_JSON_PATH)
    }

    private fun onTooltip(
        stack: ItemStack,
        ctx: Item.TooltipContext,
        flag: TooltipFlag,
        tooltipLines: MutableList<Component>
    ) {
        if (!HypixelPackets.inSkyblock || HypixelPackets.currentIsland == SkyblockIslands.THE_RIFT || !LegacySkyblockConfig.showMissingEnchantments) return

        val expanded = Minecraft.getInstance().hasShiftDown()

        if (!lastStack.isEmpty && ItemStack.isSameItemSameComponents(stack, lastStack)) {
            if (expanded == lastRenderedExpanded) {
                if (lastRenderBlock.isNotEmpty()) insertBlock(tooltipLines, lastRenderBlock, lastCurrentEnchants)
                return
            }
            if (lastMissingNamesSorted.isNotEmpty() || lastNotMaxedNamesSorted.isNotEmpty()) {
                lastRenderBlock = if (expanded) {
                    buildExpandedBlock(lastMissingNamesSorted, lastNotMaxedNamesSorted)
                } else {
                    buildCollapsedBlock(lastMissingNamesSorted.size, lastNotMaxedNamesSorted.size)
                }
                lastRenderedExpanded = expanded
                lastRenderedForMissing = lastMissingNamesSorted
                insertBlock(tooltipLines, lastRenderBlock, lastCurrentEnchants)
            }
            return
        }

        val hovered = readHoveredItemInfo(stack, tooltipLines) ?: return

        if (!isSameItem(hovered)) {
            lastMissingNamesSorted = findMissingEnchantNames(hovered.itemType, hovered.currentEnchants.keys)

            lastNotMaxedNamesSorted = if (LegacySkyblockConfig.showNonMaxedEnchantments) {
                findNotMaxedEnchantNames(hovered.itemType, hovered.currentEnchants)
            } else {
                emptyList()
            }

            lastItemType = hovered.itemType
            lastCurrentEnchants = hovered.currentEnchants
            lastRenderedForMissing = emptyList()
            lastNormalizedTokens = null
            lastInsertIndex = -1
        }

        lastStack = stack.copy()

        if (lastMissingNamesSorted.isEmpty() && lastNotMaxedNamesSorted.isEmpty()) return

        if (lastMissingNamesSorted != lastRenderedForMissing || expanded != lastRenderedExpanded) {
            lastRenderBlock = if (expanded) {
                buildExpandedBlock(lastMissingNamesSorted, lastNotMaxedNamesSorted)
            } else {
                buildCollapsedBlock(lastMissingNamesSorted.size, lastNotMaxedNamesSorted.size)
            }
            lastRenderedForMissing = lastMissingNamesSorted
            lastRenderedExpanded = expanded
        }

        insertBlock(tooltipLines, lastRenderBlock, lastCurrentEnchants)
    }

    private fun isSameItem(hovered: HoveredItemInfo): Boolean {
        return hovered.itemType == lastItemType && hovered.currentEnchants == lastCurrentEnchants
    }

    private fun buildCollapsedBlock(missingCount: Int, notMaxedCount: Int): List<Component> {
        val out = mutableListOf<Component>()
        out.add(Component.literal(""))
        if (missingCount > 0) {
            out.add(
                Component.literal("Missing Enchantments: $missingCount (Hold Shift)")
                    .withStyle(ChatFormatting.RED)
            )
        }
        if (LegacySkyblockConfig.showNonMaxedEnchantments && notMaxedCount > 0) {
            out.add(
                Component.literal("Non-maxed Enchantments: $notMaxedCount (Hold Shift)")
                    .withStyle(ChatFormatting.GOLD)
            )
        }
        return out
    }

    private fun buildExpandedBlock(
        missingNamesSorted: List<String>,
        notMaxedNamesSorted: List<String>
    ): List<Component> {
        val mc = Minecraft.getInstance()
        val out = mutableListOf<Component>()

        if (missingNamesSorted.isNotEmpty()) {
            out.add(Component.literal(""))
            out.add(
                Component.literal("Missing Enchantments:")
                    .withStyle(ChatFormatting.RED)
            )
            appendWrappedNames(mc, out, missingNamesSorted, ChatFormatting.GRAY)
        }

        if (LegacySkyblockConfig.showNonMaxedEnchantments && notMaxedNamesSorted.isNotEmpty()) {
            out.add(Component.literal(""))
            out.add(
                Component.literal("Non-maxed Enchantments:")
                    .withStyle(ChatFormatting.GOLD)
            )
            appendWrappedNames(mc, out, notMaxedNamesSorted, ChatFormatting.YELLOW)
        }

        return out
    }

    private fun appendWrappedNames(
        mc: Minecraft,
        out: MutableList<Component>,
        names: List<String>,
        color: ChatFormatting
    ) {
        val commaWidth = mc.font.width(", ")
        val prefixWidth = mc.font.width(LIST_PREFIX)
        val maxWidth = MAX_LINE_WIDTH - prefixWidth

        val currentLine = mutableListOf<String>()
        var currentWidth = 0

        for (name in names) {
            val nameWidth = mc.font.width(name)
            val addWidth = if (currentLine.isEmpty()) nameWidth else commaWidth + nameWidth

            if (currentLine.isNotEmpty() && currentWidth + addWidth > maxWidth) {
                out.add(
                    Component.literal(LIST_PREFIX + currentLine.joinToString(", "))
                        .withStyle(color)
                )
                currentLine.clear()
                currentWidth = 0
            }

            currentLine.add(name)
            currentWidth += if (currentLine.size == 1) nameWidth else addWidth
        }

        if (currentLine.isNotEmpty()) {
            out.add(
                Component.literal(LIST_PREFIX + currentLine.joinToString(", "))
                    .withStyle(color)
            )
        }
    }

    private fun insertBlock(
        tooltipLines: MutableList<Component>,
        block: List<Component>,
        currentEnchants: Map<String, Int>
    ) {
        tooltipLines.addAll(findInsertIndex(tooltipLines, currentEnchants.keys), block)
    }

    private fun findInsertIndex(tooltipLines: List<Component>, enchantKeys: Set<String>): Int {
        if (lastInsertIndex in 0..tooltipLines.size) return lastInsertIndex

        var tokens = lastNormalizedTokens
        if (tokens == null) {
            tokens = ArrayList(enchantKeys.size)
            for (key in enchantKeys) {
                val t = normalizeEnchantToken(key)
                if (t.isNotEmpty()) tokens.add(t)
            }
            lastNormalizedTokens = tokens
        }

        var lastRomanLine = -1
        var lastEnchantLine = -1

        for (i in tooltipLines.indices) {
            val raw = tooltipLines[i].string
            val line = raw.lowercase(Locale.ROOT)

            val trimmed = raw.trimEnd()
            val spaceIdx = trimmed.lastIndexOf(' ')
            if (spaceIdx >= 0 && ROMAN_NUMERALS.contains(trimmed.substring(spaceIdx + 1))) {
                lastRomanLine = i
                continue
            }

            var isEnchantLine = line.contains("enchant")
            if (!isEnchantLine) {
                for (token in tokens) {
                    if (line.contains(token)) {
                        isEnchantLine = true
                        break
                    }
                }
            }
            if (isEnchantLine) {
                lastEnchantLine = i
            }
        }

        val anchor = if (lastRomanLine >= 0) lastRomanLine
        else if (lastEnchantLine >= 0) lastEnchantLine
        else tooltipLines.size - 1

        val base = min(anchor + 1, tooltipLines.size)

        for (i in base until min(tooltipLines.size, base + 8)) {
            if (tooltipLines[i].string.isEmpty()) {
                lastInsertIndex = i
                return i
            }
        }
        lastInsertIndex = base
        return base
    }

    private fun normalizeEnchantToken(enchantId: String): String {
        return enchantId.replace("ultimate_", "")
            .replace('_', ' ')
            .lowercase(Locale.ROOT)
    }

    private data class HoveredItemInfo(val itemType: String, val currentEnchants: Map<String, Int>)

    private fun readHoveredItemInfo(stack: ItemStack, tooltipLines: List<Component>): HoveredItemInfo? {
        val itemType = readItemType(tooltipLines) ?: return null

        val currentEnchants = readCurrentEnchants(stack)
        if (currentEnchants.containsKey("ultimate_one_for_all")) return null

        return HoveredItemInfo(itemType, currentEnchants)
    }

    private fun readItemType(tooltipLines: List<Component>): String? {
        for (i in tooltipLines.indices.reversed()) {
            val line = tooltipLines[i].string
            val match = ITEM_TYPE_LINE.find(line) ?: continue
            return match.groupValues[1].trim().uppercase(Locale.ROOT)
        }
        return null
    }

    private fun readCurrentEnchants(stack: ItemStack): Map<String, Int> {
        val customData = stack.get(DataComponents.CUSTOM_DATA) ?: return emptyMap()
        return extractEnchantIds(customData)
    }

    private fun extractEnchantIds(customData: CustomData): Map<String, Int> {
        val tag = customData.copyTag()

        return tag.getCompound("enchantments")
            .map { enchantments ->
                buildMap {
                    for (key in enchantments.keySet()) {
                        enchantments.getInt(key).ifPresent { level ->
                            put(key, level)
                        }
                    }
                }
            }
            .orElse(emptyMap()) ?: emptyMap()
    }

    private fun findMissingEnchantNames(itemType: String, currentEnchants: Set<String>): List<String> {
        if (!loadPoolsIfNeeded()) return emptyList()

        val possibleEnchants = JsonHelper.getEnchants(itemType, ENCHANTS_JSON_PATH)
        if (possibleEnchants.isEmpty()) return emptyList()

        val satisfiedPools = buildSatisfiedPools(currentEnchants)

        val itemSupportsUltimate = possibleEnchants.any { isUltimateEnchant(it) }
        val hasUltimateOnItem = hasUltimateEnchant(currentEnchants)

        val missing = mutableListOf<String>()

        for (enchantId in possibleEnchants) {
            if (isUltimateEnchant(enchantId)) continue

            if (isMissingEnchant(enchantId, currentEnchants, satisfiedPools)) {
                missing.add(toPrettyName(enchantId))
            }
        }

        if (itemSupportsUltimate && !hasUltimateOnItem) {
            missing.add("Ultimate enchant")
        }

        missing.sortWith(String.CASE_INSENSITIVE_ORDER)
        return missing
    }

    private fun isUltimateEnchant(enchantId: String): Boolean {
        return enchantId.startsWith("ultimate_")
    }

    private fun loadPoolsIfNeeded(): Boolean {
        if (poolsLoaded) return true

        if (!Files.exists(ENCHANTS_JSON_PATH)) return false

        val pools = JsonHelper.getEnchantPools(ENCHANTS_JSON_PATH)
        if (pools.isEmpty()) return false

        poolIdsByEnchant.clear()
        poolCount = pools.size

        for (poolId in pools.indices) {
            for (enchantId in pools[poolId]) {
                poolIdsByEnchant.computeIfAbsent(enchantId) { mutableListOf() }.add(poolId)
            }
        }

        poolsLoaded = true
        return true
    }

    private fun buildSatisfiedPools(currentEnchants: Set<String>): BitSet {
        val satisfiedPools = BitSet(poolCount)
        for (enchantId in currentEnchants) {
            val poolIds = poolIdsByEnchant[enchantId]
            if (poolIds != null) {
                for (poolId in poolIds) satisfiedPools.set(poolId)
            }
        }
        return satisfiedPools
    }

    private fun isMissingEnchant(enchantId: String, currentEnchants: Set<String>, satisfiedPools: BitSet): Boolean {
        if (currentEnchants.contains(enchantId)) return false
        if (currentEnchants.contains("ultimate_one_for_all")) return false

        val poolIds = poolIdsByEnchant[enchantId] ?: return true

        for (poolId in poolIds) {
            if (satisfiedPools.get(poolId)) return false
        }

        return true
    }

    private fun hasUltimateEnchant(currentEnchants: Set<String>): Boolean {
        return currentEnchants.any { it.startsWith("ultimate_") }
    }

    private fun findNotMaxedEnchantNames(itemType: String, currentEnchants: Map<String, Int>): List<String> {
        val notMaxed = mutableListOf<String>()

        for ((id, currentLevel) in currentEnchants) {
            val maxLevel = JsonHelper.getMaxLevel(id, ENCHANTS_JSON_PATH)
            if (maxLevel > 0 && currentLevel < maxLevel) {
                notMaxed.add("${toPrettyName(id)} ${toRoman(currentLevel)}→${toRoman(maxLevel)}")
            }
        }

        notMaxed.sortWith(String.CASE_INSENSITIVE_ORDER)
        return notMaxed
    }

    private fun toPrettyName(enchantId: String): String {
        return prettyNameCache.getOrPut(enchantId) { formatEnchantName(enchantId) }
    }

    private fun toRoman(level: Int): String {
        return if (level in 1 until ROMAN.size) ROMAN[level] else level.toString()
    }

    private fun formatEnchantName(enchantId: String): String {
        var s = enchantId.lowercase(Locale.ROOT)
            .replace("ultimate_", "")
            .replace("turbo_", "turbo-")
            .replace('_', ' ')
            .trim()

        s = titleCase(s)
        return if (s.equals("pristine", ignoreCase = true)) "Prismatic" else s
    }

    private fun titleCase(input: String): String {
        if (input.isEmpty()) return input

        val parts = input.split("\\s+".toRegex())
        val out = StringBuilder(input.length)

        for (i in parts.indices) {
            val p = parts[i]
            if (p.isEmpty()) continue

            if (i > 0) out.append(' ')
            out.append(p[0].uppercaseChar()).append(p.substring(1))
        }

        return out.toString()
    }
}
