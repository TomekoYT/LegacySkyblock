package tomeko.legacyskyblock.misc;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

import java.util.ArrayList;

public class AutoTip {
    private static int tickCounter = 0;
    private static final ArrayList<String> autoTipMessages = new ArrayList<>();

    static {
        autoTipMessages.add("You tipped");
        autoTipMessages.add("You already tipped everyone that has boosters active, so there");
        autoTipMessages.add("No one has a network booster active right now! Try again later.");
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(AutoTip::autoTip);
        ClientReceiveMessageEvents.ALLOW_GAME.register(AutoTip::hideAutoTipMessages);
    }

    private static void autoTip(MinecraftClient client) {
        if (client.player == null || !HypixelPackets.onHypixel || !LegacySkyblockConfig.autoTipEnabled) {
            tickCounter = 0;
            return;
        }

        if (tickCounter == 0) {
            client.player.networkHandler.sendChatCommand("tip all");
        }

        tickCounter++;
        if (tickCounter >= LegacySkyblockConfig.autoTipInterval * 1200) {
            tickCounter = 0;
        }
    }

    private static boolean hideAutoTipMessages(Text message, boolean fromActionBar) {
        if (fromActionBar || !LegacySkyblockConfig.hideAutoTipMessagesEnabled || message == null) {
            return true;
        }

        for (String messageToHide : autoTipMessages) {
            if (message.getString().contains(messageToHide)) {
                return false;
            }
        }

        return true;
    }
}
