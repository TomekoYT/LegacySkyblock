package tomeko.legacyskyblock.config

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import tomeko.legacyskyblock.utils.Constants

object LegacySkyblockConfig : Config(
    Constants.MOD_ID + ".json",
    Constants.MOD_ICON,
    Constants.MOD_NAME,
    Category.HYPIXEL
) {
    val DEPENDENCIES: List<Pair<String, List<String>>> = listOf(
        "NBTDataEnabled" to listOf(
            "NBTDataHideEnchantments",
            "NBTDataHideGemstones"
        )
    )

    fun register() {
        preload()
        for ((condition, dependencies) in DEPENDENCIES) {
            for (dependency in dependencies) {
                addDependency(dependency, condition)
            }
        }
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
    @Switch(title = "Enabled", category = CATEGORY_GUI, subcategory = SUBCATEGORY_MIDDLE_CLICK_GUI_ITEMS)
    var middleClickGUIItemsEnabled: Boolean = true


    private const val CATEGORY_MISC: String = "Misc"
    private const val SUBCATEGORY_HIDE_DAMAGE_SPLASH = "Hide Damage Splash"

    @JvmStatic
    @MultiSelectDropdown(
        title = "Hide Damage Splash",
        checkable = true,
        category = CATEGORY_MISC,
        subcategory = SUBCATEGORY_HIDE_DAMAGE_SPLASH,
        options = [
            "Private Island",
            "SkyBlock Hub",
            "Dungeon Hub",
            "Catacombs",
            "The Barn",
            "The Park",
            "Galatea",
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
        ]
    )
    var hideDamageSplashEnabledIslands: BooleanArray = BooleanArray(20) { false }


    private const val SUBCATEGORY_NBT_DATA = "NBT Data"

    @Switch(
        title = "Show Item NBT Data in Tooltip",
        category = CATEGORY_MISC,
        subcategory = SUBCATEGORY_NBT_DATA
    )
    var NBTDataEnabled = false

    @Switch(
        title = "Hide Enchantments",
        category = CATEGORY_MISC,
        subcategory = SUBCATEGORY_NBT_DATA
    )
    var NBTDataHideEnchantments = true

    @Switch(
        title = "Hide Gemstones",
        category = CATEGORY_MISC,
        subcategory = SUBCATEGORY_NBT_DATA
    )
    var NBTDataHideGemstones = true


    private const val CATEGORY_DEBUG = "Debug"

    @Switch(
        title = "Debug Mode Enabled",
        category = CATEGORY_DEBUG
    )
    var debugModeEnabled: Boolean = false
}