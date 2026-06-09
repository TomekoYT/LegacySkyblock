package tomeko.legacyskyblock.hud;

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.Component;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

public class ActionBar {
    @Switch(
            title = "Hide Defense",
            category = LegacySkyblockConfig.CATEGORY_SCREEN,
            subcategory = "Action Bar"
    )
    private boolean hideDefense = false;

    @Switch(
            title = "Hide True Defense",
            category = LegacySkyblockConfig.CATEGORY_SCREEN,
            subcategory = "Action Bar"
    )
    private boolean hideTrueDefense = false;

    @Switch(
            title = "Hide Florid Zombie Sword's Charges",
            category = LegacySkyblockConfig.CATEGORY_SCREEN,
            subcategory = "Action Bar"
    )
    private boolean hideFloridZombieSwordsCharges = false;

    public ActionBar() {
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, fromActionBar) -> {
            if (!fromActionBar || !HypixelPackets.inSkyblock) {
                return message;
            }

            //Hide True Defense
            if (hideTrueDefense) {
                message = replaceActionBar(message, "§f.*?§f❂ True Defense");
            }

            //Hide Defense
            if (hideDefense) {
                message = replaceActionBar(message, "§a.*?§a❈ Defense");
            }

            //Hide Florid Zombie Sword's charges
            if (hideFloridZombieSwordsCharges) {
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

    private Component replaceActionBar(Component message, String replace) {
        String blank = " {5}";
        message = Component.nullToEmpty(message.getString().replaceAll(replace + blank, ""));
        message = Component.nullToEmpty(message.getString().replaceAll(blank + replace, ""));
        message = Component.nullToEmpty(message.getString().replaceAll(replace, ""));
        return message;
    }
}
