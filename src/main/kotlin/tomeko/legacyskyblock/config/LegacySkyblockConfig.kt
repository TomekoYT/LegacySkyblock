package tomeko.legacyskyblock.config

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import tomeko.legacyskyblock.tooltip.NBTTypes
import tomeko.legacyskyblock.utils.Constants
import tomeko.legacyskyblock.utils.Debug
import tomeko.legacyskyblock.utils.SkyblockIslands

object LegacySkyblockConfig : Config(
    Constants.MOD_ID + ".json",
    Constants.MOD_ICON,
    Constants.MOD_NAME,
    Category.HYPIXEL
) {
    val DEPENDENCIES: List<Pair<String, List<String>>> = listOf(

    )

    fun register() {
        if (!LegacySkyblockConfig::class.java.getDeclaredField("hideDamageSplashEnabledIslands")
                .getAnnotation(MultiSelectDropdown::class.java).options
                .contentEquals(SkyblockIslands.entries.map { it.islandName }.toTypedArray())
        ) Debug.forceError("hideDamageSplashEnabled missing options")

        if (!LegacySkyblockConfig::class.java.getDeclaredField("NBTDataEnabledTypes")
                .getAnnotation(MultiSelectDropdown::class.java).options
                .contentEquals(NBTTypes.entries.map { it.nbtName }.toTypedArray())
        ) Debug.forceError("NBTDataEnabledTypes missing options")

        preload()
        for ((condition, dependencies) in DEPENDENCIES) {
            for (dependency in dependencies) {
                addDependency(dependency, condition)
            }
        }

        if (hideDamageSplashEnabledIslands.size != SkyblockIslands.entries.size)
            hideDamageSplashEnabledIslands = BooleanArray(SkyblockIslands.entries.size) { false }

        if (NBTDataEnabledTypes.size != NBTTypes.entries.size)
            NBTDataEnabledTypes = BooleanArray(NBTTypes.entries.size) { it == NBTTypes.entries.size - 1 }
    }

    private const val CATEGORY_HUD: String = "HUD"
    private const val SUBCATEGORY_PET_DISPLAY: String = "Pet Display"

    @Info(
        title = "Pet Display can be edited by clicking Edit HUD in top left corner",
        category = CATEGORY_HUD,
        subcategory = SUBCATEGORY_PET_DISPLAY
    )
    var petDisplayEditInfo: Nothing? = null

    @Info(
        title = "Works best with pet widget enabled in /widgets",
        category = CATEGORY_HUD,
        subcategory = SUBCATEGORY_PET_DISPLAY
    )
    var petDisplayWidgetInfo: Nothing? = null


    private const val SUBCATEGORY_ACTION_BAR: String = "Action Bar"

    @Switch(
        title = "Hide Defense",
        category = CATEGORY_HUD,
        subcategory = SUBCATEGORY_ACTION_BAR
    )
    var actionBarHideDefense = false

    @Switch(
        title = "Hide True Defense",
        category = CATEGORY_HUD,
        subcategory = SUBCATEGORY_ACTION_BAR
    )
    var actionBarHideTrueDefense = false

    @Switch(
        title = "Hide Florid Zombie Sword's Charges",
        category = CATEGORY_HUD,
        subcategory = SUBCATEGORY_ACTION_BAR
    )
    var actionBarHideFloridZombieSwordsCharges = false


    private const val CATEGORY_GUI: String = "GUI"
    private const val SUBCATEGORY_MIDDLE_CLICK_GUI_ITEMS: String = "Middle Click GUI Items"

    @JvmField
    @Switch(
        title = "Middle Click GUI Items",
        category = CATEGORY_GUI,
        subcategory = SUBCATEGORY_MIDDLE_CLICK_GUI_ITEMS
    )
    var middleClickGUIItemsEnabled: Boolean = true


    private const val CATEGORY_TOOLTIP = "Tooltip"
    private const val SUBCATEGORY_MISSING_ENCHANTMENTS = "Missing Enchantments"

    @Switch(
        title = "Show Missing Enchantments",
        category = CATEGORY_TOOLTIP,
        subcategory = SUBCATEGORY_MISSING_ENCHANTMENTS
    )
    var showMissingEnchantments: Boolean = true

    @Switch(
        title = "Show Non-maxed Enchantments",
        category = CATEGORY_TOOLTIP,
        subcategory = SUBCATEGORY_MISSING_ENCHANTMENTS
    )
    var showNonMaxedEnchantments: Boolean = true


    private const val SUBCATEGORY_NBT_DATA = "NBT Data"

    @MultiSelectDropdown(
        title = "Show Item NBT Data",
        checkable = true,
        options = [
            "Ability Scroll",
            "Additional Coins",
            "Art of War Count",
            "Attributes",
            "Auction",
            "Base Stat Boost Percentage",
            "Bid",
            "Bingo Event",
            "Bookworm Books",
            "Boss Tier",
            "Boosters",
            "Cake Owner",
            "Captured Date",
            "Captured Player",
            "Century Year",
            "Century Year Obtained",
            "Color",
            "Date",
            "Divan Powder Coating",
            "Donated Museum",
            "Drill Fuel",
            "Drill Part Engine",
            "Drill Part Fuel Tank",
            "Drill Part Upgrade Module",
            "Dungeon Item",
            "Dungeon Item Level",
            "Edition",
            "Effects",
            "Enchantments",
            "Enhanced",
            "Engine",
            "Ethermerge",
            "Event",
            "Extended",
            "Farming for Dummies Count",
            "Fuel Tank",
            "Fungi Cutter Mode",
            "Gemstones",
            "Gilded Gifted Coins",
            "Historic Dungeon Score",
            "Hook",
            "Hot Potato Count",
            "ID",
            "Initiator Player",
            "Kuudra Cavity Rarity",
            "Lava Creatures Killed",
            "Leaderboard Player",
            "Leaderboard Position",
            "Leaderboard Score",
            "Levelable Experience",
            "Levelable Level",
            "Levelable Overclocks",
            "Levels Found",
            "Line",
            "Logs Cut",
            "Mana Disintegrator Count",
            "Mined Crops",
            "Modifier",
            "Names Found",
            "New Year's Cake",
            "Origin Tag",
            "Party Hat Color",
            "Party Hat Emoji",
            "Party Hat Year",
            "Personal Compact Number",
            "Personal Deletor Active",
            "Personal Deletor Number",
            "Pet Info",
            "Pickonimbus Durability",
            "Player",
            "Playtime",
            "Polarvoid",
            "Potion",
            "Potion Level",
            "Potion Type",
            "Power Ability Scroll",
            "Raffle Win",
            "Raffle Year",
            "Rarity Upgrades",
            "Recipient ID",
            "Recipient Name",
            "Recipient Team",
            "Shown to Coral",
            "Sinker",
            "Skill Requirement",
            "Soul Durability",
            "Soulbound",
            "Spray",
            "Spray Item",
            "Stacking Enchantment",
            "Stats Book",
            "Talisman Enrichment",
            "TD Attune Mode",
            "Tickets",
            "Timestamp",
            "Traps Defused",
            "Tuned Transmission",
            "Upgrade Level",
            "Upgrade Module",
            "Upgraded Rarity",
            "UUID",
            "Water Level",
            "Wet Book Count",
            "Winning Bid",
            "Winning Team",
            "Wood Singularity Count",
            "Year",
            "Year Obtained",
            "Other"
        ],
        category = CATEGORY_TOOLTIP,
        subcategory = SUBCATEGORY_NBT_DATA
    )
    var NBTDataEnabledTypes: BooleanArray = BooleanArray(NBTTypes.entries.size) { it == NBTTypes.entries.size - 1 }


    private const val CATEGORY_MISC: String = "Misc"
    private const val SUBCATEGORY_HIDE_DAMAGE_SPLASH = "Hide Damage Splash"

    @JvmStatic
    @MultiSelectDropdown(
        title = "Hide Damage Splash",
        checkable = true,
        options = [
            "Private Island",
            "SkyBlock Hub",
            "Dungeon Hub",
            "Catacombs",
            "The Barn",
            "The Park",
            "Galatea",
            "Torrhus Canyon",
            "Safari Zone",
            "Gold Mine",
            "Deep Caverns",
            "Dwarven Mines",
            "Crystal Hollows",
            "Spider's Den",
            "The End",
            "Crimson Isle",
            "Kuudra",
            "The Garden",
            "The Rift",
            "Backwater Bayou",
            "Lotus Atoll",
            "Jerry's Workshop"
        ],
        category = CATEGORY_MISC,
        subcategory = SUBCATEGORY_HIDE_DAMAGE_SPLASH
    )
    var hideDamageSplashEnabledIslands: BooleanArray = BooleanArray(SkyblockIslands.entries.size) { false }


    private const val CATEGORY_DEBUG = "Debug"

    @Info(
        title = "Probably should stay disabled",
        category = CATEGORY_DEBUG
    )
    var debugModeInfo: Nothing? = null

    @Switch(
        title = "Debug Mode Enabled",
        category = CATEGORY_DEBUG
    )
    var debugModeEnabled: Boolean = false
}