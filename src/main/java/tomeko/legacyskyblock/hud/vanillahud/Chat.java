package tomeko.legacyskyblock.hud.vanillahud;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class Chat {
    private static boolean guildMOTD = false;
    private static final List<AbstractMap.SimpleEntry<String, String>> EMOTES = new ArrayList<>();

    static {
        EMOTES.add(new AbstractMap.SimpleEntry<>("<3", "❤"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":arrow:", "➜"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":bum:", "♿"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":cat:", "= ＾● ⋏ ●＾ ="));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":cute:", "(✿◠‿◠)"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":dab:", "<o/"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":dj:", "ヽ(⌐■_■)ノ♬"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":dog:", "(ᵔᴥᵔ)"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":gimme:", "༼つ◕_◕༽つ"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":java:", "☕"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":maths:", "√(π+x)=L"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":no:", "✖"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":peace:", "✌"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":puffer:", "<('O')>"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":pvp:", "⚔"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":shrug:", "¯\\_(ツ)_/¯"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":skull:", "☠"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":sloth:", "(・⊝・)"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":snail:", "@'-'"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":snow:", "☃"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":star:", "✮"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":tableflip:", "(╯°□°）╯︵ ┻━┻"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":thinking:", "(0.o?)"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":totem:", "☉_☉"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":typing:", "✎..."));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":wizard:", "('-')⊃━☆ﾟ.*･｡ﾟ"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":yes:", "✔"));
        EMOTES.add(new AbstractMap.SimpleEntry<>(":yey:", "ヽ (◕◡◕) ﾉ"));
        EMOTES.add(new AbstractMap.SimpleEntry<>("ez", "ｅｚ"));
        EMOTES.add(new AbstractMap.SimpleEntry<>("h/", "ヽ(^◇^*)/"));
        EMOTES.add(new AbstractMap.SimpleEntry<>("o/", "( ﾟ◡ﾟ)/"));
    }

    public static void register() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, fromActionBar) -> {
            if (fromActionBar || message == null) {
                return true;
            }

            String unformattedMessage = removeFormatting(message.getString());

            //Hide Chat Messages
            //Hide Guild MOTD
            if (LegacySkyblockConfig.hideGuildMOTD) {
                if (unformattedMessage.startsWith("--------------  Guild: Message Of The Day  --------------")) {
                    guildMOTD = true;
                }
                if (guildMOTD) {
                    if (unformattedMessage.endsWith("-----------------------------------------------------")) {
                        guildMOTD = false;
                    }
                    return false;
                }
            }

            //Hide Custom Chat Messages
            for (String messageToHide : LegacySkyblockConfig.customChatMessagesToHide) {
                if (messageToHide.isEmpty()) {
                    continue;
                }

                if (unformattedMessage.contains(messageToHide)) {
                    return false;
                }
            }

            return true;
        });

        ClientReceiveMessageEvents.MODIFY_GAME.register((message, fromActionBar) -> {
            if (fromActionBar || message == null) {
                return message;
            }

            //White Chat Messages
            //White Private Messages
            if (LegacySkyblockConfig.whitePrivateMessagesEnabled && HypixelPackets.onHypixel && (message.getString().startsWith("From ") || message.getString().startsWith("To "))) {
                MutableText newMessage = message.copyContentOnly().formatted(Formatting.LIGHT_PURPLE);
                int n = message.getSiblings().size();
                if (n < 1) {
                    return message;
                }

                boolean semicolonPassed = false;
                for (int i = 0; i < n - 1; i++) {
                    if (!semicolonPassed && message.getSiblings().get(i).getString().contains(":")) {
                        String sibling = message.getSiblings().get(i).getString();
                        int colonIndex = sibling.indexOf(":");
                        if (colonIndex > 0) {
                            newMessage.append(Text.literal(sibling.substring(0, colonIndex)).setStyle(message.getSiblings().get(i).getStyle()));
                        }
                        newMessage.append(Text.literal(":").formatted(Formatting.WHITE));
                        if (colonIndex + 1 <= sibling.length() - 1) {
                            newMessage.append(Text.literal(sibling.substring(colonIndex + 1)).setStyle(message.getSiblings().get(i).getStyle()));
                        }

                        semicolonPassed = true;
                        continue;
                    }
                    newMessage.append(message.getSiblings().get(i));
                }

                if (!semicolonPassed) {
                    return message;
                }

                newMessage.append(message.getSiblings().get(n - 1).copy().formatted(Formatting.WHITE));
                return newMessage;
            }

            //White No Rank Messages
            if (LegacySkyblockConfig.whiteNoRankMessagesEnabled && HypixelPackets.onHypixel && message.getString().contains("§7: ")) {
                return Text.of(message.getString().replace("§7: ", "§f: "));
            }

            return message;
        });

        ClientSendMessageEvents.MODIFY_CHAT.register((message) -> {
            //MVP++ Emotes
            if (LegacySkyblockConfig.MVPEmotesEnabled) {
                return replaceEmotes(message);
            }

            return message;
        });

        ClientSendMessageEvents.MODIFY_COMMAND.register((command) -> {
            //MVP++ Emotes
            if (LegacySkyblockConfig.MVPEmotesEnabled) {
                String[] words = command.split(" ");
                StringBuilder newCommand = new StringBuilder(words[0]);
                for (int i = 1; i < words.length; i++) {
                    newCommand.append(" ").append(replaceEmotes(words[i]));
                }
                return newCommand.toString();
            }

            return command;
        });
    }

    private static String removeFormatting(String string) {
        return string.replaceAll("§.", "");
    }

    private static String replaceEmotes(String message) {
        for (AbstractMap.SimpleEntry<String, String> entry : EMOTES) {
            message = message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }
}
