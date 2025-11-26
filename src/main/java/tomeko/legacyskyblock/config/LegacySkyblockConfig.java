package tomeko.legacyskyblock.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import tomeko.legacyskyblock.utils.ItemUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LegacySkyblockConfig {
    public static final ConfigClassHandler<LegacySkyblockConfig> CONFIG = ConfigClassHandler.createBuilder(LegacySkyblockConfig.class)
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("legacyskyblock.json"))
                    .build())
            .build();

    //Auto Refill
    public static final ItemUtil[] refillItems = {new ItemUtil("Ender Pearl", "ENDER_PEARL", 16), new ItemUtil("Spirit Leap", "SPIRIT_LEAP", 16), new ItemUtil("Superboom TNT", "SUPERBOOM_TNT", 64), new ItemUtil("Decoy", "DUNGEON_DECOY", 64), new ItemUtil("Inflatable Jerry", "INFLATABLE_JERRY", 64)};
    private static final String[] refillOptionNames = new String[refillItems.length];
    private static final String[] refillOptionDescriptions = new String[refillItems.length];

    static {
        for (int i = 0; i < refillItems.length; i++) {
            refillOptionNames[i] = refillItems[i].name + " Refill";
            refillOptionDescriptions[i] = "Enable " + refillOptionNames[i] + " from sacks when dungeon starts";
        }
    }

    @SerialEntry
    public static HashMap<String, Boolean> refillEnabled = new HashMap<>();

    //Hide Damage Splash
    @SerialEntry
    public static boolean hideDamageSplashEnabled = false;

    //Toggle Sprint
    @SerialEntry
    public static boolean toggleSprintEnabled = false;
    @SerialEntry
    public static String toggleSprintText = "Sprint Toggled";
    @SerialEntry
    public static Color toggleSprintTextColor = Color.WHITE;
    @SerialEntry
    public static boolean toggleSprintTextShadowEnabled = false;
    @SerialEntry
    public static int toggleSprintTextWidth = 10;
    @SerialEntry
    public static int toggleSprintTextHeight = 10;

    //Actionbar
    @SerialEntry
    public static boolean hideDefense = false;
    @SerialEntry
    public static boolean hideTrueDefense = false;
    @SerialEntry
    public static boolean hideFloridZombieSwordsCharges = false;

    //Health Vignette
    @SerialEntry
    public static boolean healthVignetteEnabled = true;
    @SerialEntry
    public static float healthVignetteOpacityPercentage = 25F;
    @SerialEntry
    public static float healthVignetteHealthPercentage = 20F;

    //Middle Click GUI Items
    @SerialEntry
    public static boolean middleClickGUIEnabled = true;

    //No Death Animation
    @SerialEntry
    public static boolean noDeathAnimationEnabled = true;

    //Modify Screenshot Message
    @SerialEntry
    public static boolean modifyScreenshotMessageEnabled = true;
    @SerialEntry
    public static boolean modifyScreenshotMessageAddName = false;
    @SerialEntry
    public static boolean modifyScreenshotMessageAddCopy = true;
    @SerialEntry
    public static boolean modifyScreenshotMessageAddOpen = true;
    @SerialEntry
    public static boolean modifyScreenshotMessageAddOpenFolder = true;
    @SerialEntry
    public static boolean modifyScreenshotMessageAddDelete = true;

    //Auto Copy Screenshot
    @SerialEntry
    public static boolean autoCopyScreenshotEnabled = false;

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
    public static boolean hideGuildMOTDEnabled = false;

    //Hide Custom Chat Messages
    @SerialEntry
    public static List<String> customChatMessagesToHide = new ArrayList<>();

    public static Screen configScreen(Screen parent) {
        return YetAnotherConfigLib.create(CONFIG, ((defaults, config, builder) -> builder
                .title(Text.literal("LegacySkyblock"))

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Dungeons"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Auto Refill Items on Dungeon Start"))
                                .description(OptionDescription.of(Text.literal("Auto refill certain items from sacks when dungeon starts")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal(refillOptionNames[0]))
                                        .description(OptionDescription.of(Text.literal(refillOptionDescriptions[0])))
                                        .binding(defaults.refillEnabled.get(refillItems[0].id), () -> config.refillEnabled.get(refillItems[0].id), newVal -> config.refillEnabled.put(refillItems[0].id, newVal))
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal(refillOptionNames[1]))
                                        .description(OptionDescription.of(Text.literal(refillOptionDescriptions[1])))
                                        .binding(defaults.refillEnabled.get(refillItems[1].id), () -> config.refillEnabled.get(refillItems[1].id), newVal -> config.refillEnabled.put(refillItems[1].id, newVal))
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal(refillOptionNames[2]))
                                        .description(OptionDescription.of(Text.literal(refillOptionDescriptions[2])))
                                        .binding(defaults.refillEnabled.get(refillItems[2].id), () -> config.refillEnabled.get(refillItems[2].id), newVal -> config.refillEnabled.put(refillItems[2].id, newVal))
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal(refillOptionNames[3]))
                                        .description(OptionDescription.of(Text.literal(refillOptionDescriptions[3])))
                                        .binding(defaults.refillEnabled.get(refillItems[3].id), () -> config.refillEnabled.get(refillItems[3].id), newVal -> config.refillEnabled.put(refillItems[3].id, newVal))
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal(refillOptionNames[4]))
                                        .description(OptionDescription.of(Text.literal(refillOptionDescriptions[4])))
                                        .binding(defaults.refillEnabled.get(refillItems[4].id), () -> config.refillEnabled.get(refillItems[4].id), newVal -> config.refillEnabled.put(refillItems[4].id, newVal))
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Hide Damage Splash"))
                                .description(OptionDescription.of(Text.literal("Hide damage splash on mobs while in dungeons")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .binding(defaults.hideDamageSplashEnabled, () -> config.hideDamageSplashEnabled, newVal -> config.hideDamageSplashEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Screen"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Toggle Sprint (KeyBind)"))
                                .description(OptionDescription.of(Text.literal("Use a KeyBind to toggle sprint")))
                                .option(Option.<String>createBuilder()
                                        .name(Text.literal("Text"))
                                        .binding(defaults.toggleSprintText, () -> config.toggleSprintText, newVal -> config.toggleSprintText = newVal)
                                        .controller(opt -> StringControllerBuilder.create(opt))
                                        .build())
                                .option(Option.<Color>createBuilder()
                                        .name(Text.literal("Color"))
                                        .binding(defaults.toggleSprintTextColor, () -> config.toggleSprintTextColor, newVal -> config.toggleSprintTextColor = newVal)
                                        .controller(opt -> ColorControllerBuilder.create(opt))
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Text Shadow"))
                                        .binding(defaults.toggleSprintTextShadowEnabled, () -> config.toggleSprintTextShadowEnabled, newVal -> config.toggleSprintTextShadowEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.literal("Position X"))
                                        .binding(defaults.toggleSprintTextWidth, () -> config.toggleSprintTextWidth, newVal -> config.toggleSprintTextWidth = newVal)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                                .formatValue(value -> Text.literal(String.valueOf(value)))
                                                .range(0, MinecraftClient.getInstance().getWindow().getScaledWidth())
                                                .step(1))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.literal("Position Y"))
                                        .binding(defaults.toggleSprintTextHeight, () -> config.toggleSprintTextHeight, newVal -> config.toggleSprintTextHeight = newVal)
                                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                                .formatValue(value -> Text.literal(String.valueOf(value)))
                                                .range(0, MinecraftClient.getInstance().getWindow().getScaledHeight())
                                                .step(1))
                                        .build())
                                .build())

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
                                .name(Text.literal("Health Vignette"))
                                .description(OptionDescription.of(Text.literal("Turn screen red when below % of health")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .binding(defaults.healthVignetteEnabled, () -> config.healthVignetteEnabled, newVal -> config.healthVignetteEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Set Opacity Percentage"))
                                        .description(OptionDescription.of(Text.literal("Set Health Vignette's opacity\nSet 0% to disable")))
                                        .binding(defaults.healthVignetteOpacityPercentage, () -> config.healthVignetteOpacityPercentage, newVal -> config.healthVignetteOpacityPercentage = newVal)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .formatValue(value -> Text.literal(String.format("%,.0f", value) + "%"))
                                                .range(0F, 100F)
                                                .step(1F))
                                        .build())
                                .option(Option.<Float>createBuilder()
                                        .name(Text.literal("Set Health Percentage"))
                                        .description(OptionDescription.of(Text.literal("Set % of health for which Health Vignette will be shown")))
                                        .binding(defaults.healthVignetteHealthPercentage, () -> config.healthVignetteHealthPercentage, newVal -> config.healthVignetteHealthPercentage = newVal)
                                        .controller(opt -> FloatSliderControllerBuilder.create(opt)
                                                .formatValue(value -> Text.literal(String.format("%,.0f", value) + "%"))
                                                .range(0F, 100F)
                                                .step(1F))
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
                                .build())

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("No Death Animation"))
                                .description(OptionDescription.of(Text.literal("Remove death animation when mob is killed")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .binding(defaults.noDeathAnimationEnabled, () -> config.noDeathAnimationEnabled, newVal -> config.noDeathAnimationEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())

                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Screenshots"))

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Modify Screenshot Message"))
                                .description(OptionDescription.of(Text.literal("Modify message sent after taking a screenshot")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .binding(defaults.modifyScreenshotMessageEnabled, () -> config.modifyScreenshotMessageEnabled, newVal -> config.modifyScreenshotMessageEnabled = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Add Screenshot Name"))
                                        .binding(defaults.modifyScreenshotMessageAddName, () -> config.modifyScreenshotMessageAddName, newVal -> config.modifyScreenshotMessageAddName = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Add [COPY] Button"))
                                        .binding(defaults.modifyScreenshotMessageAddCopy, () -> config.modifyScreenshotMessageAddCopy, newVal -> config.modifyScreenshotMessageAddCopy = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Add [OPEN] Button"))
                                        .binding(defaults.modifyScreenshotMessageAddOpen, () -> config.modifyScreenshotMessageAddOpen, newVal -> config.modifyScreenshotMessageAddOpen = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Add [OPEN FOLDER] Button"))
                                        .binding(defaults.modifyScreenshotMessageAddOpenFolder, () -> config.modifyScreenshotMessageAddOpenFolder, newVal -> config.modifyScreenshotMessageAddOpenFolder = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Add [DELETE] Button"))
                                        .binding(defaults.modifyScreenshotMessageAddDelete, () -> config.modifyScreenshotMessageAddDelete, newVal -> config.modifyScreenshotMessageAddDelete = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())

                        .group(OptionGroup.createBuilder()
                                .name(Text.literal("Auto Copy Screenshot"))
                                .description(OptionDescription.of(Text.literal("Automatically copy the screenshot image after pressing F2")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.literal("Enabled"))
                                        .binding(defaults.autoCopyScreenshotEnabled, () -> config.autoCopyScreenshotEnabled, newVal -> config.autoCopyScreenshotEnabled = newVal)
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
                                        .binding(defaults.hideGuildMOTDEnabled, () -> config.hideGuildMOTDEnabled, newVal -> config.hideGuildMOTDEnabled = newVal)
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

    public static void register() {
        LegacySkyblockConfig.CONFIG.load();

        for (ItemUtil refillItem : refillItems) {
            if (!refillEnabled.containsKey(refillItem.id)) {
                refillEnabled.put(refillItem.id, false);
            }
        }

        LegacySkyblockConfig.CONFIG.save();
    }
}