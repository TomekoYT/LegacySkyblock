package tomeko.legacyskyblock.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.Functions;
import tomeko.legacyskyblock.utils.HypixelPackets;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    private static boolean guildMOTD = false;

    @WrapMethod(method = "addMessage")
    private void onChatMessage(Text message, Operation<Void> original) {
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
                original.call(message);
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
            original.call(newMessage);
            return;
        }
        if (LegacySkyblockConfig.whiteNoRankMessagesEnabled && HypixelPackets.onHypixel && message.getString().contains("§7: ")) {
            original.call(Text.of(message.getString().replace("§7: ", "§f: ")));
            return;
        }

        original.call(message);
    }
}
