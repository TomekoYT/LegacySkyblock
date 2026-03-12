package tomeko.legacyskyblock.hud;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.Debug;
import tomeko.legacyskyblock.utils.HypixelPackets;

//Hide Chat Messages
public class Chat {
    private static boolean guildMOTD = false;

    public static void register() {
        Debug.print("Hide Chat Messages registered");
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, fromActionBar) -> {
            if (fromActionBar || message == null) {
                return true;
            }

            String unformattedMessage = removeFormatting(message.getString());

            //Hide Guild MOTD
            if (LegacySkyblockConfig.hideGuildMOTDEnabled) {
                if (unformattedMessage.startsWith("--------------  Guild: Message Of The Day  --------------")) {
                    guildMOTD = true;
                }
                if (guildMOTD) {
                    if (unformattedMessage.endsWith("-----------------------------------------------------")) {
                        guildMOTD = false;
                    }

                    Debug.print("Guild MOTD canceled");
                    return false;
                }
            }

            //Hide Custom Chat Messages
            for (String messageToHide : LegacySkyblockConfig.customChatMessagesToHide) {
                if (messageToHide.isEmpty()) {
                    continue;
                }

                if (unformattedMessage.contains(messageToHide)) {
                    Debug.print("Message canceled: " + messageToHide);
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
                Debug.print("White Private Message detected: " + message.getString());

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
                Debug.print("White No Rank Message detected: " + message.getString());

                return Text.of(message.getString().replace("§7: ", "§f: "));
            }

            return message;
        });
    }

    private static String removeFormatting(String string) {
        return string.replaceAll("§.", "");
    }
}
