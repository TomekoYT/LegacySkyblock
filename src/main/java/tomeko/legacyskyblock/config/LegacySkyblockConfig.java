package tomeko.legacyskyblock.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LegacySkyblockConfig {
    public static final ConfigClassHandler<LegacySkyblockConfig> CONFIG = ConfigClassHandler.createBuilder(LegacySkyblockConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("legacyskyblock.json"))
                    .build())
            .build();

    public static boolean shouldOpenConfig = false;

    //Hide Damage Splash
    @SerialEntry
    public static boolean hideDamageSplashEnabled = false;
    @SerialEntry
    public static boolean hideDamageSplashWorkOutsideDungeons = false;

    //White Chat Messages
    private static final String SKYHANNI_CHAT_FORMATTING_WARNING = "\n\n§cDoesn't work with SkyHanni's Chat Formatting enabled!";
    @SerialEntry
    public static boolean whiteNoRankMessagesEnabled = true;
    @SerialEntry
    public static boolean whitePrivateMessagesEnabled = true;

    //Hide Guild MOTD
    @SerialEntry
    public static boolean hideGuildMOTDEnabled = false;

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
    @SerialEntry
    public static boolean middleClickGUIWorkOutsideSkyblock = false;
    private static final String MIDDLE_CLICK_GUI_WORK_OUTSIDE_SKYBLOCK_WARNING = "\n\n§cEXPECT BUGS WITH THIS OPTION ENABLED!\n§7Report issues here: §3https://github.com/TomekoYT/LegacySkyblock/issues";

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.literal("LegacySkyblock"))

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("GUI & HUD"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Action Bar"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Hide Defense"))
                                        .description(OptionDescription.of(Text.literal("Hide Defense in action bar")))
                                        .binding(defaults.hideDefense, () -> config.hideDefense, newVal -> config.hideDefense = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Hide True Defense"))
                                        .description(OptionDescription.of(Text.literal("Hide True Defense in action bar")))
                                        .binding(defaults.hideTrueDefense, () -> config.hideTrueDefense, newVal -> config.hideTrueDefense = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Hide Florid Zombie Sword's charges"))
                                        .description(OptionDescription.of(Text.literal("Hide Florid Zombie Sword's charges in action bar")))
                                        .binding(defaults.hideFloridZombieSwordsCharges, () -> config.hideFloridZombieSwordsCharges, newVal -> config.hideFloridZombieSwordsCharges = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Middle Click GUI Items"))
                                .description(OptionDescription.of(Text.literal("Use middle click instead of left click in GUIs")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .binding(defaults.middleClickGUIEnabled, () -> config.middleClickGUIEnabled, newVal -> config.middleClickGUIEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Work outside Skyblock"))
                                        .description(OptionDescription.of(Text.literal("Make Middle Click GUI Items work outside Hypixel Skyblock" + MIDDLE_CLICK_GUI_WORK_OUTSIDE_SKYBLOCK_WARNING)))
                                        .binding(defaults.middleClickGUIWorkOutsideSkyblock, () -> config.middleClickGUIWorkOutsideSkyblock, newVal -> config.middleClickGUIWorkOutsideSkyblock = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Chat"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Modify Chat Messages"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Color no rank messages"))
                                        .description(OptionDescription.of(Text.literal("§fMake messages of players without a rank white" + SKYHANNI_CHAT_FORMATTING_WARNING)))
                                        .binding(defaults.whiteNoRankMessagesEnabled, () -> config.whiteNoRankMessagesEnabled, newVal -> config.whiteNoRankMessagesEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Color private messages"))
                                        .description(OptionDescription.of(Text.literal("§fMake private messages white" + SKYHANNI_CHAT_FORMATTING_WARNING)))
                                        .binding(defaults.whitePrivateMessagesEnabled, () -> config.whitePrivateMessagesEnabled, newVal -> config.whitePrivateMessagesEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Hide Guild MOTD"))
                                .description(OptionDescription.of(Text.literal("Hide guild's message of the day")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .binding(defaults.hideGuildMOTDEnabled, () -> config.hideGuildMOTDEnabled, newVal -> config.hideGuildMOTDEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Dungeons"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Hide Damage Splash"))
                                .description(OptionDescription.of(Text.literal("Hide damage splash on mobs while in dungeons")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .binding(defaults.hideDamageSplashEnabled, () -> config.hideDamageSplashEnabled, newVal -> config.hideDamageSplashEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Work outside Dungeons"))
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

    private static void openConfigOnTick(MinecraftClient client) {
        if (!shouldOpenConfig) return;

        shouldOpenConfig = false;
        client.setScreen(configScreen(client.currentScreen));
    }
}