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
    //Hide Defense
    //Hide True Defense
    //Hide Florid Zombie Sword's charges
    @WrapMethod(method = "Lnet/minecraft/client/gui/hud/InGameHud;setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    private void modifyActionbar(Text message, boolean tinted, Operation<Void> original) {
        if (message == null || !HypixelPackets.inSkyblock) {
            original.call(message, tinted);
            return;
        }

        if (LegacySkyblockConfig.hideTrueDefense) {
            message = Text.of(message.getString().replaceAll("§f.*?§f❂ True Defense     ", ""));
            message = Text.of(message.getString().replaceAll("     §f.*?§f❂ True Defense", ""));
            message = Text.of(message.getString().replaceAll("§f.*?§f❂ True Defense", ""));
        }
        if (LegacySkyblockConfig.hideDefense) {
            message = Text.of(message.getString().replaceAll("§a.*?§a❈ Defense     ", ""));
            message = Text.of(message.getString().replaceAll("     §a.*?§a❈ Defense", ""));
            message = Text.of(message.getString().replaceAll("§a.*?§a❈ Defense", ""));
        }
        if (LegacySkyblockConfig.hideFloridZombieSwordsCharges) {
            message = Text.of(message.getString().replaceAll("§e§lⓩⓩⓩⓩⓩ§6§l     ", ""));
            message = Text.of(message.getString().replaceAll("     §e§lⓩⓩⓩⓩⓩ§6§l", ""));
            message = Text.of(message.getString().replaceAll("§e§lⓩⓩⓩⓩⓩ§6§l", ""));
        }

        original.call(message, tinted);
    }
}