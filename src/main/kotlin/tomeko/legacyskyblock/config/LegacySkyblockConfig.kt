package tomeko.legacyskyblock.config

import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import tomeko.legacyskyblock.utils.Constants

object LegacySkyblockConfig : Config(
    Constants.MOD_ID + ".json",
    "/assets/" + Constants.MOD_ID + "/icon.png",
    Constants.MOD_NAME,
    Category.HYPIXEL
) {
    fun register() {
        save()
    }

    private const val CATEGORY_HUD: String = "HUD"
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


    private const val CATEGORY_DUNGEONS: String = "Dungeons"
    private const val SUBCATEGORY_HIDE_DAMAGE_SPLASH = "Hide Damage Splash"

    @Switch(
        title = "Enabled",
        category = CATEGORY_DUNGEONS,
        subcategory = SUBCATEGORY_HIDE_DAMAGE_SPLASH
    )
    var hideDamageSplashEnabled: Boolean = false

    @Switch(
        title = "Work Outside Dungeons",
        category = CATEGORY_DUNGEONS,
        subcategory = SUBCATEGORY_HIDE_DAMAGE_SPLASH
    )
    var hideDamageSplashWorkOutsideDungeons: Boolean = false
}