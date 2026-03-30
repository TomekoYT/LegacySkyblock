package tomeko.legacyskyblock.hud;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

//Hide Chat Messages
public class Chat {
    private static boolean guildMOTD = false;

    public static void register() {
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
                MutableComponent newMessage = message.plainCopy().withStyle(ChatFormatting.LIGHT_PURPLE);
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
                            newMessage.append(Component.literal(sibling.substring(0, colonIndex)).setStyle(message.getSiblings().get(i).getStyle()));
                        }
                        newMessage.append(Component.literal(":").withStyle(ChatFormatting.WHITE));
                        if (colonIndex + 1 <= sibling.length() - 1) {
                            newMessage.append(Component.literal(sibling.substring(colonIndex + 1)).setStyle(message.getSiblings().get(i).getStyle()));
                        }

                        semicolonPassed = true;
                        continue;
                    }
                    newMessage.append(message.getSiblings().get(i));
                }

                if (!semicolonPassed) {
                    return message;
                }

                newMessage.append(message.getSiblings().get(n - 1).copy().withStyle(ChatFormatting.WHITE));
                return newMessage;
            }

            if (LegacySkyblockConfig.whiteNoRankMessagesEnabled && HypixelPackets.onHypixel && message.getString().contains("§7: ")) {
                return Component.nullToEmpty(message.getString().replace("§7: ", "§f: "));
            }

            return message;
        });
    }

    private static String removeFormatting(String string) {
        return string.replaceAll("§.", "");
    }
}
