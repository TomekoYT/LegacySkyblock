package tomeko.legacyskyblock.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class LegacySkyblockConfig {
    public static final ConfigClassHandler<LegacySkyblockConfig> CONFIG = ConfigClassHandler.createBuilder(LegacySkyblockConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("legacyskyblock.json"))
                    .build())
            .build();

    //Middle Click GUI Items
    @SerialEntry
    public static boolean middleClickGUIEnabled = true;

    //Actionbar
    @SerialEntry
    public static boolean hideDefense = false;
    @SerialEntry
    public static boolean hideTrueDefense = false;
    @SerialEntry
    public static boolean hideFloridZombieSwordsCharges = false;

    //MVP++ Emotes
    @SerialEntry
    public static boolean MVPEmotesEnabled = false;

    //White Chat Messages
    private static final String SKYHANNI_CHAT_FORMATTING_WARNING = "\n\n§cDoesn't work with SkyHanni's Chat Formatting enabled!";
    @SerialEntry
    public static boolean whiteNoRankMessagesEnabled = true;
    @SerialEntry
    public static boolean whitePrivateMessagesEnabled = true;

    //Hide Chat Messages
    @SerialEntry
    public static boolean hideGuildMOTD = false;

    //Hide Custom Chat Messages
    @SerialEntry
    public static List<String> customChatMessagesToHide = new ArrayList<>();

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.literal("LegacySkyblock"))

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("GUI"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Middle Click GUI Items"))
                                .description(OptionDescription.of(Text.literal("Use middle click instead of left click in GUI's")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .description(OptionDescription.of(Text.literal("Enable Middle Click GUI Items")))
                                        .binding(defaults.middleClickGUIEnabled, () -> config.middleClickGUIEnabled, newVal -> config.middleClickGUIEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())


                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("HUD"))

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
                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Chat"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Modify Chat Messages"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("MVP++ emotes"))
                                        .description(OptionDescription.of(Text.literal("Use MVP++ emotes in chat\nExample: ❤ instead of <3")))
                                        .binding(defaults.MVPEmotesEnabled, () -> config.MVPEmotesEnabled, newVal -> config.MVPEmotesEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
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
                                .name(Text.literal("Hide Chat Messages"))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Hide guild MOTD"))
                                        .description(OptionDescription.of(Text.literal("Hide guild's message of the day")))
                                        .binding(defaults.hideGuildMOTD, () -> config.hideGuildMOTD, newVal -> config.hideGuildMOTD = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())

                        .group(ListOption.<String>createBuilder()
                                .name(Text.literal("Hide Custom Chat Messages"))
                                .binding(defaults.customChatMessagesToHide, () -> config.customChatMessagesToHide, newVal -> config.customChatMessagesToHide = newVal)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .build())
                        .build())
        )).generateScreen(parent);
    }
}