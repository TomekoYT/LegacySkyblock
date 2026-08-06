package tomeko.legacyskyblock.mixins;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.ChatHelper;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class PreventClosingHoppityCallMixin {
    private static boolean legacyskyblock$preventedClosingHoppity = false;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void legacyskyblock$preventClosingHoppityCall(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        AbstractContainerScreen<?> instance = (AbstractContainerScreen<?>) (Object) this;

        if (!LegacySkyblockConfig.preventClosingHoppityCall
                || keyEvent.key() != GLFW.GLFW_KEY_ESCAPE
                || !(instance.getMenu() instanceof ChestMenu chestMenu)
                || !instance.getTitle().getString().equals("Hoppity")
                || 49 >= chestMenu.slots.size()
                || !chestMenu.getSlot(49).getItem().getStyledHoverName().getString().equals("Close")
        ) {
            legacyskyblock$preventedClosingHoppity = false;
            return;
        }

        List<Component> tooltip = chestMenu.getSlot(22).getItem().getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
        for (Component line : tooltip) {
            if (line.getString().contains("You have already found")) {
                legacyskyblock$preventedClosingHoppity = false;
                return;
            }
        }

        if (legacyskyblock$preventedClosingHoppity) {
            legacyskyblock$preventedClosingHoppity = false;
            return;
        }

        legacyskyblock$preventedClosingHoppity = true;
        ChatHelper.addChatMessage(Component.literal("Detected new rabbit! Press ESC again to close.").withStyle(style -> style.withColor(ChatFormatting.RED)));
        cir.setReturnValue(true);
    }
}
