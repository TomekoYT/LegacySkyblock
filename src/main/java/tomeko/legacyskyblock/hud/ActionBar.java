package tomeko.legacyskyblock.hud;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

public class ActionBar {
    public static void register() {
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, fromActionBar) -> {
            if (!fromActionBar || message == null || !HypixelPackets.inSkyblock) {
                return message;
            }

            //Hide True Defense
            if (LegacySkyblockConfig.hideTrueDefense) {
                message = replaceActionBar(message, "§f.*?§f❂ True Defense");
            }

            //Hide Defense
            if (LegacySkyblockConfig.hideDefense) {
                message = replaceActionBar(message, "§a.*?§a❈ Defense");
            }

            //Hide Florid Zombie Sword's charges
            if (LegacySkyblockConfig.hideFloridZombieSwordsCharges) {
                message = replaceActionBar(message, "§e§lⓩⓩⓩⓩⓩ§6§l");
                message = replaceActionBar(message, "§e§lⓩⓩⓩⓩ§6§lⓄ");
                message = replaceActionBar(message, "§e§lⓩⓩⓩ§6§lⓄⓄ");
                message = replaceActionBar(message, "§e§lⓩⓩ§6§lⓄⓄⓄ");
                message = replaceActionBar(message, "§e§lⓩ§6§lⓄⓄⓄⓄ");
                message = replaceActionBar(message, "§e§l§6§lⓄⓄⓄⓄⓄ");
            }
            return message;
        });
    }

    private static Text replaceActionBar(Text message, String replace) {
        String blank = " {5}";
        message = Text.of(message.getString().replaceAll(replace + blank, ""));
        message = Text.of(message.getString().replaceAll(blank + replace, ""));
        message = Text.of(message.getString().replaceAll(replace, ""));
        return message;
    }
}
