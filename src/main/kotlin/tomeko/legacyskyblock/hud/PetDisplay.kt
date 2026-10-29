package tomeko.legacyskyblock.hud

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.ContainerScreen
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import org.polyfrost.compose.composables.PolyBox
import org.polyfrost.compose.composables.PolyColumn
import org.polyfrost.compose.composables.PolyMcText
import org.polyfrost.compose.composables.PolyModifier
import org.polyfrost.compose.composables.PolyRow
import org.polyfrost.compose.composables.absoluteAt
import org.polyfrost.compose.composables.padding
import org.polyfrost.compose.composables.size
import org.polyfrost.compose.layout.PolyInsets
import org.polyfrost.compose.mc.McFontQueue
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.hud.v1.Hud
import org.polyfrost.oneconfig.api.hud.v1.HudManager
import org.polyfrost.oneconfig.api.ui.v1.item.PolyItemIcon
import tomeko.legacyskyblock.neu.PetFetcher
import tomeko.legacyskyblock.utils.Constants
import tomeko.legacyskyblock.location.HypixelPackets
import tomeko.legacyskyblock.location.SkyblockIslands
import tomeko.legacyskyblock.utils.removeFormatting
import java.util.Optional
import kotlin.math.*

class PetDisplay : Hud("${Constants.MOD_ID}_pet_display.json", "Pet Display", Category.PLAYER) {
    companion object {
        fun register() {
            HudManager.register(PetDisplay(), Constants.MOD_ID, Constants.MOD_ICON)
            PetFetcher.register()
            ClientTickEvents.END_CLIENT_TICK.register(::searchTab)
            ClientReceiveMessageEvents.GAME.register(::onChatMessage)
            ClientTickEvents.END_CLIENT_TICK.register(::scanLoadoutsMenu)
            ClientTickEvents.END_CLIENT_TICK.register(::scanRiftAndSafariMenus)
        }

        private const val CATEGORY_GENERAL = "General"
        private const val SUBCATEGORY_GENERAL = "General"
        private const val SUBCATEGORY_PET = "Pet"

        private const val CATEGORY_ICONS = "Icons"
        private const val SUBCATEGORY_PET_ICON = "Pet Icon"
        private const val SUBCATEGORY_PET_ITEM_ICON = "Pet Item Icon"

        @Include
        var petNameCache: String? = null

        @Include
        var petLevelCache: Int? = null

        @Include
        var petRarityCache: String? = null

        @Include
        var petItemCache: String? = null

        @Include
        var petItemRarityCache: String? = null

        var petXPLineCache: Component? = null

        @Include
        var riftPetNameCache: String? = null

        @Include
        var riftPetLevelCache: Int? = null

        @Include
        var riftPetRarityCache: String? = null

        @Include
        var riftPetItemCache: String? = null

        @Include
        var riftPetItemRarityCache: String? = null

        @Include
        var safariPetNameCache: String? = null

        @Include
        var safariPetLevelCache: Int? = null

        @Include
        var safariPetRarityCache: String? = null

        @Include
        var safariPetItemCache: String? = null

        @Include
        var safariPetItemRarityCache: String? = null

        private var tickCooldown = 0

        private fun searchTab(mc: Minecraft) {
            if (tickCooldown > 0) {
                tickCooldown--
                return
            }

            if (!HypixelPackets.inSkyblock) return

            val connection = mc.connection ?: return

            val sortedPlayers = connection.listedOnlinePlayers.sortedWith(
                compareBy<PlayerInfo> { it.team?.name ?: "" }
                    .thenBy { it.profile.name ?: "" }
            )

            var foundHeader = false
            var parsedName = false
            var parsedItemOrXp = false

            for (player in sortedPlayers) {
                var component = player.tabListDisplayName ?: Component.literal("")
                var plainText = component.string.removeFormatting().trim()

                if (!foundHeader) {
                    if (plainText == "Pet:") {
                        foundHeader = true
                    }
                    continue
                }

                if (!parsedName) {
                    if (plainText.endsWith(" ✦")) {
                        plainText = removeSkinStar(plainText)
                        if (component.siblings.isNotEmpty()) {
                            val copy: MutableComponent = component.copy()
                            copy.siblings.removeLast()
                            component = copy
                        }
                    }

                    val match = Regex("^\\[Lvl (\\d+)] (.*)$").find(plainText)
                    if (match != null) {
                        petLevelCache = match.groupValues[1].toInt()
                        petNameCache = match.groupValues[2]
                        petRarityCache = getRarityFromComponentColor(component.siblings[2].style.color!!.value)
                        parsedName = true
                        continue
                    } else {
                        if (plainText == "No pet selected") resetAll()
                        break
                    }
                }

                if (!parsedItemOrXp) {
                    if (isXpLine(plainText)) {
                        setPetXPFromTab(component)
                        resetItem()
                        break
                    } else {
                        if (plainText.isEmpty() || plainText == "MAX LEVEL" || plainText.firstOrNull() == '+') {
                            resetItem()
                            petXPLineCache = null
                            break
                        }

                        petItemCache = plainText
                        petItemRarityCache = getRarityFromComponentColor(component.siblings[1].style.color!!.value)
                        parsedItemOrXp = true
                        continue
                    }
                }

                if (isXpLine(plainText)) {
                    setPetXPFromTab(component)
                    break
                }

                petXPLineCache = null
                break
            }
        }

        private fun onChatMessage(message: Component, fromActionBar: Boolean) {
            if (!HypixelPackets.inSkyblock || fromActionBar) return

            val autoPetMatch = Regex(
                "^§cAutopet §eequipped your §7\\[Lvl (\\d+)] (§.)((?:[^§]|§.)+?)(?:§d ✦)?§e! §a§lVIEW RULE$"
            ).find(message.string)

            if (autoPetMatch != null) {
                setTickCooldown()
                petLevelCache = autoPetMatch.groupValues[1].toInt()
                petRarityCache = getRarityFromChatColor(autoPetMatch.groupValues[2])
                petNameCache = autoPetMatch.groupValues[3]

                resetItem()
                petXPLineCache = null
                return
            }


            val levelUpMatch = Regex(
                "^Your\\s+(.+?)\\s+leveled up to level\\s+(\\d+)!$"
            ).find(message.string)

            if (levelUpMatch != null) {
                val name = levelUpMatch.groupValues[1]
                val level = levelUpMatch.groupValues[2].toInt()

                if (name != petNameCache
                    || getRarityFromComponentColor(message.siblings[1].style.color!!.value) != petRarityCache
                    || (petLevelCache != null && level <= petLevelCache!!)
                ) return

                setTickCooldown()
                petLevelCache = level
                petXPLineCache = null
                return
            }

            val holdingPetItemMatch = Regex(
                "^Your pet is now holding (.+)\\.$"
            ).find(message.string)

            if (holdingPetItemMatch != null) {
                val sibling = message.siblings[1]

                setTickCooldown()
                if (HypixelPackets.currentIsland == SkyblockIslands.THE_RIFT) {
                    riftPetItemCache = sibling.string
                    riftPetItemRarityCache = getRarityFromComponentColor(sibling.style.color!!.value)
                } else {
                    petItemCache = sibling.string
                    petItemRarityCache = getRarityFromComponentColor(sibling.style.color!!.value)
                }
                return
            }

            val removedPetItemMatch = Regex(
                "^You removed (.+) from your pet!$"
            ).find(message.string)

            if (removedPetItemMatch != null) {
                setTickCooldown()
                resetItem()
                return
            }
        }

        private fun scanLoadoutsMenu(mc: Minecraft) {
            if (!HypixelPackets.inSkyblock) return

            val screen =
                //? if >= 26.2 {
                mc.gui.screen()
            //?} else {
            //mc.screen
            //?}
            if (screen !is ContainerScreen || !screen.title.string.endsWith("Loadouts")) return

            val item = screen.menu.container.getItem(21)
            val component = removeFavoriteAndSkinStar(item.hoverName)
            val match = Regex("^\\[Lvl (\\d+)] (.*)$").find(component.string)

            if (match == null) {
                resetAll()
                return
            }

            val level = match.groupValues[1].toInt()
            val name = match.groupValues[2]

            setTickCooldown()
            petNameCache = name
            petLevelCache = level
            petRarityCache = getRarityFromComponentColor(component.siblings[1].style.color!!.value)

            val tooltip: MutableList<Component> =
                item.getTooltipLines(Item.TooltipContext.EMPTY, mc.player, TooltipFlag.NORMAL)

            searchForPetItemInTooltip(tooltip)
            petXPLineCache = null
        }

        private fun scanRiftAndSafariMenus(mc: Minecraft) {
            if (HypixelPackets.currentIsland != SkyblockIslands.THE_RIFT && HypixelPackets.currentIsland != SkyblockIslands.CRITTER_SAFARI) return

            val screen =
                //? if >= 26.2 {
                mc.gui.screen()
            //?} else {
            //mc.screen
            //?}
            if (screen !is ContainerScreen || screen.title.string != "SkyBlock Menu") return

            val item = screen.menu.container.getItem(
                if (HypixelPackets.currentIsland == SkyblockIslands.THE_RIFT) 30
                else 29
            )

            val match = Regex("^\\[Lvl (\\d+)] (.*)$").find(item.hoverName.string)
            if (match == null) {
                if (HypixelPackets.currentIsland == SkyblockIslands.THE_RIFT) {
                    riftPetNameCache = null
                    riftPetLevelCache = null
                    riftPetRarityCache = null
                    resetRiftItem()
                } else {
                    safariPetNameCache = null
                    safariPetLevelCache = null
                    safariPetRarityCache = null
                    resetSafariItem()
                }
                return
            }

            val level = match.groupValues[1].toInt()
            val name = match.groupValues[2]

            if (HypixelPackets.currentIsland == SkyblockIslands.THE_RIFT) {
                riftPetNameCache = name
                riftPetLevelCache = level
                riftPetRarityCache = getRarityFromComponentColor(item.hoverName.siblings[1].style.color!!.value)
                resetRiftItem()
            } else {
                safariPetNameCache = name
                safariPetLevelCache = level
                safariPetRarityCache = getRarityFromComponentColor(item.hoverName.siblings[1].style.color!!.value)
                resetSafariItem()
            }

            val tooltip: MutableList<Component> =
                item.getTooltipLines(Item.TooltipContext.EMPTY, mc.player, TooltipFlag.NORMAL)

            for (line in tooltip) {
                val match = Regex("Held Item: (.*)").find(line.string) ?: continue

                if (HypixelPackets.currentIsland == SkyblockIslands.THE_RIFT) {
                    riftPetItemCache = match.groupValues[1]
                    riftPetItemRarityCache = getRarityFromComponentColor(line.siblings[1].style.color!!.value)
                } else {
                    safariPetItemCache = match.groupValues[1]
                    safariPetItemRarityCache = getRarityFromComponentColor(line.siblings[1].style.color!!.value)
                }
                return
            }

            resetSafariItem()
        }

        fun getRarityFromComponentColor(color: Int): String? = when (color) {
            16777215 -> "COMMON"
            5635925 -> "UNCOMMON"
            5592575 -> "RARE"
            11141290 -> "EPIC"
            16755200 -> "LEGENDARY"
            16733695 -> "MYTHIC"
            else -> null
        }

        fun getRarityFromChatColor(color: String): String? = when (color) {
            "§f" -> "COMMON"
            "§a" -> "UNCOMMON"
            "§9" -> "RARE"
            "§5" -> "EPIC"
            "§6" -> "LEGENDARY"
            "§d" -> "MYTHIC"
            else -> null
        }

        fun getChatColorFromRarity(rarity: String): String? = when (rarity) {
            "COMMON" -> "§f"
            "UNCOMMON" -> "§a"
            "RARE" -> "§9"
            "EPIC" -> "§5"
            "LEGENDARY" -> "§6"
            "MYTHIC" -> "§d"
            else -> null
        }

        private fun getArgbFromRarity(rarity: String?): Int? = when (rarity) {
            "COMMON" -> 0xFFFFFFFF.toInt()
            "UNCOMMON" -> 0xFF55FF55.toInt()
            "RARE" -> 0xFF5555FF.toInt()
            "EPIC" -> 0xFFAA00AA.toInt()
            "LEGENDARY" -> 0xFFFFAA00.toInt()
            "MYTHIC" -> 0xFFFF55FF.toInt()
            else -> null
        }

        fun resetAll() {
            petNameCache = null
            petLevelCache = null
            petRarityCache = null
            resetItem()
            petXPLineCache = null
        }

        private fun resetItem() {
            petItemCache = null
            petItemRarityCache = null
        }

        private fun resetRiftItem() {
            riftPetItemCache = null
            riftPetItemRarityCache = null
        }

        private fun resetSafariItem() {
            safariPetItemCache = null
            safariPetItemRarityCache = null
        }

        private fun setPetXPFromTab(component: Component) {
            val copy: MutableComponent = component.copy()
            copy.siblings.removeFirst()
            petXPLineCache = copy
        }

        private fun isXpLine(text: String): Boolean {
            return text.firstOrNull()?.isDigit() == true
        }

        private fun removeSkinStar(name: String): String {
            if (name.endsWith(" ✦")) return name.dropLast(2)
            return name
        }

        fun setTickCooldown() {
            tickCooldown = 60
        }

        fun removeFavoriteAndSkinStar(name: Component): Component {
            if (name.siblings.isEmpty()) return name

            val copy = name.copy()

            val first: Component = copy.siblings.first()
            if (first.string.startsWith("⭐ ")) {
                copy.siblings.removeFirst()
            }

            if (copy.siblings.isEmpty()) return copy

            val last: Component = copy.siblings.last()
            if (last.string.endsWith(" ✦")) {
                copy.siblings.removeLast()
            }

            return copy
        }

        private fun getPetID(petName: String): String = when (petName) {
            "Montezuma" -> "FRACTURED_MONTEZUMA_SOUL"
            else -> petName.uppercase().replace(" ", "_")
        }

        fun searchForPetItemInTooltip(tooltip: MutableList<Component>) {
            for (line in tooltip) {
                val match = Regex("Held Item: (.*)").find(line.string) ?: continue

                petItemCache = match.groupValues[1]
                petItemRarityCache = getRarityFromComponentColor(line.siblings[1].style.color!!.value)
                return
            }

            resetItem()
        }
    }

    @Slider(
        title = "Lines Padding",
        min = 0f,
        max = 10f,
        step = 0.1f,
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_GENERAL
    )
    var linesPadding = 3f

    @Slider(
        title = "Background Margin",
        min = 0f,
        max = 10f,
        step = 0.1f,
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_GENERAL
    )
    var backgroundMargin = 4f


    @Switch(
        title = "Show Pet Name",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showName = true

    @Switch(
        title = "Show Pet Level",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showLevel = true

    @Switch(
        title = "Show Pet Item Name",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showItemName = true

    @Switch(
        title = "Show Pet XP",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showXP = true

    @Switch(
        title = "Show in The Rift",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showInRift = true

    @Switch(
        title = "Show in Critter Safari",
        category = CATEGORY_GENERAL,
        subcategory = SUBCATEGORY_PET
    )
    var showInCritterSafari = true


    @Switch(
        title = "Show Pet Icon",
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ICON
    )
    var showIcon = true

    @Slider(
        title = "Icon Size",
        min = 1f,
        max = 64f,
        step = 1f,
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ICON
    )
    var iconSize = 32f

    @Slider(
        title = "Icon Padding",
        min = 0f,
        max = 10f,
        step = 0.1f,
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ICON
    )
    var iconPadding = 3f

    @Switch(
        title = "Show Pet Item Icon",
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ITEM_ICON
    )
    var showItemIcon = true

    @Slider(
        title = "Item Icon Size",
        min = 1f,
        max = 32f,
        step = 1f,
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ITEM_ICON
    )
    var itemIconSize = 16f

    @Slider(
        title = "Item Icon Padding",
        min = 0f,
        max = 5f,
        step = 0.1f,
        category = CATEGORY_ICONS,
        subcategory = SUBCATEGORY_PET_ITEM_ICON
    )
    var itemIconPadding = 2f

    private data class Segment(val text: String, val color: Int?)

    private data class PetData(
        val name: List<Segment>,
        val item: List<Segment>?,
        val xp: List<Segment>?,
        val icon: ItemStack?,
        val itemIcon: ItemStack?,
        val linesPadding: Float,
        val backgroundMargin: Float,
        val iconSize: Float,
        val iconPadding: Float,
        val itemIconSize: Float,
        val itemIconPadding: Float
    )

    private var dataState: MutableState<PetData?> = mutableStateOf(null)

    private fun segments(component: Component): List<Segment> {
        val out = mutableListOf<Segment>()
        component.visit({ style, text ->
            if (text.isNotEmpty()) {
                out += Segment(text, style.color?.value?.let { 0xFF000000.toInt() or it })
            }
            Optional.empty()
        }, Style.EMPTY)
        return out
    }

    private fun collect(editing: Boolean): PetData? {
        if (!editing && !HypixelPackets.inSkyblock) return null

        val petName: String?
        val petLevel: Int?
        val petRarity: String?
        val petItem: String?
        val petItemRarity: String?
        val petXPLine: Component?

        if (editing
            && ((HypixelPackets.currentIsland == SkyblockIslands.THE_RIFT && (!showInRift || riftPetNameCache == null || riftPetLevelCache == null || riftPetRarityCache == null))
                    || ((HypixelPackets.currentIsland == SkyblockIslands.CRITTER_SAFARI) && (!showInCritterSafari || safariPetNameCache == null || safariPetLevelCache == null || safariPetRarityCache == null))
                    || (!HypixelPackets.inSkyblock || petNameCache == null || petLevelCache == null || petRarityCache == null)
                    )
        ) {
            petName = "Golden Dragon"
            petLevel = 124
            petRarity = "LEGENDARY"
            petItem = "Antique Remedies"
            petItemRarity = "EPIC"
            petXPLine = Component.empty()
                .append(Component.literal("475,180").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("/").withStyle(ChatFormatting.GOLD))
                .append(Component.literal("1.9M XP ").withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("(25.2%)").withStyle(ChatFormatting.GOLD))
        } else if (HypixelPackets.currentIsland == SkyblockIslands.THE_RIFT) {
            if (showInRift) {
                petName = riftPetNameCache
                petLevel = riftPetLevelCache
                petRarity = riftPetRarityCache
                petItem = riftPetItemCache
                petItemRarity = riftPetItemRarityCache
            } else {
                petName = null
                petLevel = null
                petRarity = null
                petItem = null
                petItemRarity = null
            }
            petXPLine = null
        } else if (HypixelPackets.currentIsland == SkyblockIslands.CRITTER_SAFARI) {
            if (showInCritterSafari) {
                petName = safariPetNameCache
                petLevel = safariPetLevelCache
                petRarity = safariPetRarityCache
                petItem = safariPetItemCache
                petItemRarity = safariPetItemRarityCache
            } else {
                petName = null
                petLevel = null
                petRarity = null
                petItem = null
                petItemRarity = null
            }
            petXPLine = null
        } else {
            petName = petNameCache
            petLevel = petLevelCache
            petRarity = petRarityCache
            petItem = petItemCache
            petItemRarity = petItemRarityCache
            petXPLine = petXPLineCache
        }

        if (petName == null || petLevel == null || petRarity == null) return null

        val shouldShowPetItem = showItemName && petItem != null && petItemRarity != null
        val shouldShowPetItemIcon = showItemIcon && petItem != null && petItemRarity != null
        val shouldShowPetXP = showXP && petXPLine != null

        val nameLine = mutableListOf<Segment>()
        if (showLevel) nameLine += Segment("[Lvl $petLevel] ", 0xFFAAAAAA.toInt())
        if (showName) nameLine += Segment(petName, getArgbFromRarity(petRarity))

        val itemLine =
            if (shouldShowPetItem) listOf(Segment(petItem, getArgbFromRarity(petItemRarity)))
            else null

        val xpLine =
            if (shouldShowPetXP) segments(petXPLine)
            else null

        fun getPetItemStack(): ItemStack? = PetFetcher.getPetItemIcon(petItem, petItemRarity)

        val icon: ItemStack? =
            if (showIcon)
                PetFetcher.getPetIcon(getPetID(petName), petRarity)
            else if (shouldShowPetItemIcon)
                getPetItemStack()
            else
                null

        val hasBothIcons = icon != null && showIcon && shouldShowPetItemIcon
        val itemIcon = if (hasBothIcons) getPetItemStack() else null

        return PetData(
            name = nameLine,
            item = itemLine,
            xp = xpLine,
            icon = icon,
            itemIcon = itemIcon,
            linesPadding = linesPadding,
            backgroundMargin = backgroundMargin,
            iconSize = iconSize,
            iconPadding = iconPadding,
            itemIconSize = itemIconSize,
            itemIconPadding = itemIconPadding
        )
    }

    init {
        padLeft = 4f
        padRight = 4f
        padTop = 4f
        padBottom = 4f
        staticWidth = false
    }

    override val description: String = "Your active pet, its held item and XP in Hypixel SkyBlock."

    override fun update(): Boolean {
        dataState.value = collect(!isReal || HudManager.isEditing)
        return true
    }

    override fun updateFrequency(): Long = 50_000_000L

    override fun setup() {
        super.setup()
        update()
    }

    override fun hasBackground(): Boolean = dataState.value != null

    @Composable
    override fun Content() {
        val data = dataState.value ?: return

        val scale = textScale
        val color = PolyColor(textColor, textChroma, textChromaSpeed)
        val shadow = showShadow

        val lines = listOfNotNull(data.name.takeIf { it.isNotEmpty() }, data.item, data.xp)

        val lineHeight = McFontQueue.measureTextHeight(scale)
        val linesGap = data.linesPadding * scale
        val textHeight = if (lines.isEmpty()) 0f else lines.size * lineHeight + (lines.size - 1) * linesGap
        val textWidth = lines.maxOfOrNull { lineWidth(it, scale) } ?: 0f

        val icon = data.icon
        val iconPx = if (icon != null) data.iconSize * scale else 0f
        val itemIconPx = data.itemIconSize * scale

        val itemIconPad = (data.itemIconPadding - 3f) * scale
        val itemIconExtra = if (data.itemIcon != null) max(0f, itemIconPx / 2f + itemIconPad) else 0f

        val coreHeight = max(iconPx, textHeight)
        val iconY = (coreHeight - iconPx) / 2f
        val textY = (coreHeight - textHeight) / 2f
        val totalHeight = max(coreHeight, iconY + iconPx + itemIconExtra)

        key(showBackground, bgColor, bgChroma, bgChromaSpeed, bgRadius) {
            val margin = backgroundMargin * scale
            PolyBox(modifier = hudBackground().padding(PolyInsets(padLeft + margin, padTop + margin, padRight + margin, padBottom + margin))) {
                PolyRow(gap = if (icon != null && lines.isNotEmpty()) data.iconPadding * scale else 0f) {
                    if (icon != null) {
                        PolyBox(modifier = PolyModifier.size(iconPx + itemIconExtra, totalHeight)) {
                            PolyBox(modifier = PolyModifier.absoluteAt(0f, iconY)) {
                                PolyItemIcon(icon, iconPx, decorations = false)
                            }

                            data.itemIcon?.let { itemIcon ->
                                val itemIconX = iconPx - itemIconPx / 2f + itemIconPad
                                val itemIconY = iconY + iconPx - itemIconPx / 2f + itemIconPad
                                PolyBox(modifier = PolyModifier.absoluteAt(itemIconX, itemIconY)) {
                                    PolyItemIcon(itemIcon, itemIconPx, decorations = false)
                                }
                            }
                        }
                    }

                    if (lines.isNotEmpty()) {
                        PolyBox(modifier = PolyModifier.size(textWidth, totalHeight)) {
                            PolyBox(modifier = PolyModifier.absoluteAt(0f, textY)) {
                                PolyColumn(gap = linesGap) {
                                    for (line in lines) TextLine(line, color, shadow, scale)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun lineWidth(line: List<Segment>, scale: Float): Float =
        line.sumOf { McFontQueue.measureTextWidth(it.text, scale).toDouble() }.toFloat()

    @Composable
    private fun TextLine(line: List<Segment>, color: PolyColor, shadow: Boolean, scale: Float) {
        PolyRow(gap = 0f) {
            for ((text, color1) in line) {
                val segmentColor = color1?.let(::PolyColor) ?: color
                ShadowedText(text, segmentColor, shadow, scale)
            }
        }
    }

    @Composable
    private fun ShadowedText(text: String, color: PolyColor, shadow: Boolean, scale: Float) {
        if (!shadow) {
            PolyMcText(text, color = color, shadow = false, scale = scale)
            return
        }

        val offset = 0.5f
        val width = McFontQueue.measureTextWidth(text, scale)
        val height = McFontQueue.measureTextHeight(scale)

        PolyBox(modifier = PolyModifier.size(width, height)) {
            PolyMcText(
                text,
                color = PolyColor(shadowColor, shadowChroma, shadowChromaSpeed),
                shadow = false,
                scale = scale,
                modifier = PolyModifier.absoluteAt(
                    shadowOffsetX * offset * scale,
                    shadowOffsetY * offset * scale
                )
            )
            PolyMcText(
                text,
                color = color,
                shadow = false,
                scale = scale,
                modifier = PolyModifier.absoluteAt(0f, 0f)
            )
        }
    }

    override fun minimumSize(): Pair<Float, Float> =
        (padLeft + padRight + 2f) to (padTop + padBottom + 2f)

    override fun defaultPosition(): Pair<Float, Float> = 10f to 10f
    override fun multipleInstancesAllowed(): Boolean = true
    override fun deletable(): Boolean = true

    override fun clone(): Hud = (super.clone() as PetDisplay).also {
        it.dataState = mutableStateOf(dataState.value)
    }
}