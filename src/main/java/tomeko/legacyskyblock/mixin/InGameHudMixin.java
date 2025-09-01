package tomeko.legacyskyblock.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    //Actionbar
    @WrapMethod(method = "setOverlayMessage")
    private void setOverlayMessage(Text message, boolean tinted, Operation<Void> original) {
        if (message == null || !HypixelPackets.inSkyblock) {
            original.call(message, tinted);
            return;
        }

        //Hide True Defense
        if (LegacySkyblockConfig.hideTrueDefense) {
            message = replace(message, "§f.*?§f❂ True Defense");
        }

        //Hide Defense
        if (LegacySkyblockConfig.hideDefense) {
            message = replace(message, "§a.*?§a❈ Defense");
        }

        //Hide Florid Zombie Sword's charges
        if (LegacySkyblockConfig.hideFloridZombieSwordsCharges) {
            message = replace(message, "§e§lⓩⓩⓩⓩⓩ§6§l");
            message = replace(message, "§e§lⓩⓩⓩⓩ§6§lⓄ");
            message = replace(message, "§e§lⓩⓩⓩ§6§lⓄⓄ");
            message = replace(message, "§e§lⓩⓩ§6§lⓄⓄⓄ");
            message = replace(message, "§e§lⓩ§6§lⓄⓄⓄⓄ");
            message = replace(message, "§e§l§6§lⓄⓄⓄⓄⓄ");
        }

        original.call(message, tinted);
    }

    private static Text replace(Text message, String replace) {
        String blank = "     ";
        message = Text.of(message.getString().replaceAll(replace + blank, ""));
        message = Text.of(message.getString().replaceAll(blank + replace, ""));
        message = Text.of(message.getString().replaceAll(replace, ""));
        return message;
    }
}