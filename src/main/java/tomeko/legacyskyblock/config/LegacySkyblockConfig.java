package tomeko.legacyskyblock.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LegacySkyblockConfig {
    public static final ConfigClassHandler<LegacySkyblockConfig> CONFIG = ConfigClassHandler.createBuilder(LegacySkyblockConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("legacyskyblock.json"))
                    .build())
            .build();

    @SerialEntry
    public static boolean middleClickGUIEnabled = true;
    @SerialEntry
    public static boolean middleClickGUIOutsideSkyblock = false;

    @SerialEntry
    public static boolean actionbarHideDefense = false;
    @SerialEntry
    public static boolean actionbarHideTrueDefense = false;

    @SerialEntry
    public static boolean whiteNoRankMessagesEnabled = true;
    @SerialEntry
    public static boolean whitePrivateMessagesEnabled = true;

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.literal("LegacySkyblock"))

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("GUI"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Middle Click GUI Items"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .description(OptionDescription.of(Text.literal("Enable Middle Click GUI Items")))
                                        .binding(defaults.middleClickGUIEnabled, () -> config.middleClickGUIEnabled, newVal -> config.middleClickGUIEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Work outside of Skyblock"))
                                        .description(OptionDescription.of(Text.literal("Make Middle Click GUI Items work outside of Skyblock")))
                                        .binding(defaults.middleClickGUIOutsideSkyblock, () -> config.middleClickGUIOutsideSkyblock, newVal -> config.middleClickGUIOutsideSkyblock = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())


                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("HUD"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Actionbar"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Hide Defense"))
                                        .description(OptionDescription.of(Text.literal("Hide Defense in actionbar")))
                                        .binding(defaults.actionbarHideDefense, () -> config.actionbarHideDefense, newVal -> config.actionbarHideDefense = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Hide True Defense"))
                                        .description(OptionDescription.of(Text.literal("Hide True Defense in actionbar")))
                                        .binding(defaults.actionbarHideTrueDefense, () -> config.actionbarHideTrueDefense, newVal -> config.actionbarHideTrueDefense = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Chat"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("White Chat Messages"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Color no rank messages"))
                                        .description(OptionDescription.of(Text.literal("Make messages of players without a rank white")))
                                        .binding(defaults.whiteNoRankMessagesEnabled, () -> config.whiteNoRankMessagesEnabled, newVal -> config.whiteNoRankMessagesEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Color private messages"))
                                        .description(OptionDescription.of(Text.literal("Make private messages white")))
                                        .binding(defaults.whitePrivateMessagesEnabled, () -> config.whitePrivateMessagesEnabled, newVal -> config.whitePrivateMessagesEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
        )).generateScreen(parent);
    }
}
