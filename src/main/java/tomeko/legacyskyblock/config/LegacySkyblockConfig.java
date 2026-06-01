package tomeko.legacyskyblock.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import tomeko.legacyskyblock.utils.Constants;

public class LegacySkyblockConfig {
    public static final ConfigClassHandler<LegacySkyblockConfig> CONFIG = ConfigClassHandler.createBuilder(LegacySkyblockConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve(Constants.MOD_ID + ".json"))
                    .build())
            .build();

    public static boolean shouldOpenConfig = false;

    //Actionbar
    @SerialEntry
    public static boolean hideDefense = false;
    @SerialEntry
    public static boolean hideTrueDefense = false;
    @SerialEntry
    public static boolean hideFloridZombieSwordsCharges = false;

    //Middle Click GUI Items
    @SerialEntry
    public static boolean middleClickGUIEnabled = true;

    //Hide Damage Splash
    @SerialEntry
    public static boolean hideDamageSplashEnabled = false;
    @SerialEntry
    public static boolean hideDamageSplashWorkOutsideDungeons = false;

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Component.literal(Constants.MOD_NAME))

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("GUI & HUD"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Action Bar"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Hide Defense"))
                                        .description(OptionDescription.of(Component.literal("Hide Defense in action bar")))
                                        .binding(defaults.hideDefense, () -> config.hideDefense, newVal -> config.hideDefense = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Hide True Defense"))
                                        .description(OptionDescription.of(Component.literal("Hide True Defense in action bar")))
                                        .binding(defaults.hideTrueDefense, () -> config.hideTrueDefense, newVal -> config.hideTrueDefense = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Hide Florid Zombie Sword's charges"))
                                        .description(OptionDescription.of(Component.literal("Hide Florid Zombie Sword's charges in action bar")))
                                        .binding(defaults.hideFloridZombieSwordsCharges, () -> config.hideFloridZombieSwordsCharges, newVal -> config.hideFloridZombieSwordsCharges = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())

                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Middle Click GUI Items"))
                                .description(OptionDescription.of(Component.literal("Use middle click instead of left click in GUIs")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enabled"))
                                        .binding(defaults.middleClickGUIEnabled, () -> config.middleClickGUIEnabled, newVal -> config.middleClickGUIEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Dungeons"))

                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Hide Damage Splash"))
                                .description(OptionDescription.of(Component.literal("Hide damage splash on mobs while in dungeons")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Enabled"))
                                        .binding(defaults.hideDamageSplashEnabled, () -> config.hideDamageSplashEnabled, newVal -> config.hideDamageSplashEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Work outside Dungeons"))
                                        .binding(defaults.hideDamageSplashWorkOutsideDungeons, () -> config.hideDamageSplashWorkOutsideDungeons, newVal -> config.hideDamageSplashWorkOutsideDungeons = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
        )).generateScreen(parent);
    }

    public static void register() {
        LegacySkyblockConfig.CONFIG.load();

        ClientTickEvents.END_CLIENT_TICK.register(LegacySkyblockConfig::openConfigOnTick);
    }

    private static void openConfigOnTick(Minecraft client) {
        if (!shouldOpenConfig) return;

        shouldOpenConfig = false;
        client.setScreen(configScreen(client.screen));
    }
}