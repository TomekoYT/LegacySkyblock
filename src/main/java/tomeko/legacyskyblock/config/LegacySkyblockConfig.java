package tomeko.legacyskyblock.config;

import net.minecraft.client.gui.screens.Screen;
import org.polyfrost.oneconfig.api.config.v1.Config;
import org.polyfrost.oneconfig.api.config.v1.annotations.Include;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;
import org.polyfrost.oneconfig.utils.v1.dsl.ScreensKt;
import tomeko.legacyskyblock.dungeons.HideDamageSplash;
import tomeko.legacyskyblock.hud.ActionBar;
import tomeko.legacyskyblock.utils.Constants;

public class LegacySkyblockConfig extends Config {
    public static final LegacySkyblockConfig INSTANCE = new LegacySkyblockConfig();

    public LegacySkyblockConfig() {
        super(Constants.MOD_ID + ".json", "/assets/" + Constants.MOD_ID + "/icon.png", Constants.MOD_NAME, Category.HYPIXEL);
        save();
    }

    public void openScreen() {
        ScreensKt.createScreen(INSTANCE);
    }

    public Screen openScreen(Screen parent) {
        return ScreensKt.createScreen(INSTANCE);
    }

    public static final String CATEGORY_SCREEN = "Screen";

    @Include
    private ActionBar actionBar = new ActionBar();

    @Switch(
            title = "Enabled",
            category = CATEGORY_SCREEN,
            subcategory = "Middle Click GUI Items"
    )
    public boolean middleClickGUIEnabled = true;


    public static final String CATEGORY_DUNGEONS = "Dungeons";

    @Include
    private HideDamageSplash hideDamageSplash = new HideDamageSplash();
}