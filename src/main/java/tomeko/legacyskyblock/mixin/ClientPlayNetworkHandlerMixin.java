package tomeko.legacyskyblock.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.Functions;
import tomeko.legacyskyblock.utils.HypixelPackets;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    private static boolean guildMOTD = false;

    @WrapMethod(method = "onGameMessage")
    private void onChatMessage(GameMessageS2CPacket packet, Operation<Void> original) {
        Text message = packet.content();
        String unformattedMessage = Functions.removeFormatting(message.getString());

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
                return;
            }
        }

        //Hide Custom Chat Messages
        for (String messageToHide : LegacySkyblockConfig.customChatMessagesToHide) {
            if (messageToHide.isEmpty()) continue;
            if (unformattedMessage.contains(messageToHide)) {
                return;
            }
        }

        //White Chat Messages
        if (LegacySkyblockConfig.whitePrivateMessagesEnabled && HypixelPackets.onHypixel && (message.getString().startsWith("From ") || message.getString().startsWith("To "))) {
            MutableText newMessage = message.copyContentOnly().formatted(Formatting.LIGHT_PURPLE);
            int n = message.getSiblings().size();
            if (n < 1) {
                original.call(packet);
                return;
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
                    if (colonIndex + 1 <= sibling.length()) {
                        newMessage.append(Text.literal(sibling.substring(colonIndex + 1)).setStyle(message.getSiblings().get(i).getStyle()));
                    }
                    newMessage.append(Text.literal(sibling.substring(colonIndex + 1)));

                    semicolonPassed = true;
                    continue;
                }
                newMessage.append(message.getSiblings().get(i));
            }

            newMessage.append(message.getSiblings().get(n - 1).copy().formatted(Formatting.WHITE));
            MinecraftClient.getInstance().player.sendMessage(newMessage, false);
            return;
        }
        if (LegacySkyblockConfig.whiteNoRankMessagesEnabled && HypixelPackets.onHypixel && message.getString().contains("§7: ")) {
            MinecraftClient.getInstance().player.sendMessage(Text.of(message.getString().replace("§7: ", "§f: ")), false);
            return;
        }

        original.call(packet);
    }
}
