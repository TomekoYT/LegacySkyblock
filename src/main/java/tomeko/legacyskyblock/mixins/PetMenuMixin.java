package tomeko.legacyskyblock.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.legacyskyblock.hud.PetDisplay;
import tomeko.legacyskyblock.utils.HypixelPackets;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(AbstractContainerScreen.class)
public abstract class PetMenuMixin {
    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void legacyskyblock$detectClickOnPet(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
        AbstractContainerScreen<?> instance = (AbstractContainerScreen<?>) (Object) this;
        if (!HypixelPackets.inSkyblock
                || !(instance.getMenu() instanceof ChestMenu)
                || !instance.getTitle().getString().endsWith("Pets")
        ) return;

        Component component = PetDisplay.removeFavoriteAndSkinStar(slot.getItem().getHoverName());
        Pattern pattern = Pattern.compile("^\\[Lvl (\\d+)] (.*)$");
        Matcher matcher = pattern.matcher(component.getString());
        if (!matcher.find()) return;

        List<Component> tooltip = slot.getItem().getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
        boolean canSummon = false;
        boolean canDespawn = false;
        for (Component line : tooltip) {
            if (line.getString().equals("Left-click to summon!")) canSummon = true;
            else if (line.getString().equals("Click to despawn!")) canDespawn = true;
        }

        if (canDespawn && !legacyskyblock$isTogglingFavorite(buttonNum, containerInput)) {
            PetDisplay.setTickCooldown();
            PetDisplay.resetAll();
            return;
        }

        if (!canSummon || buttonNum == 1 || legacyskyblock$isTogglingFavorite(buttonNum, containerInput)) return;

        int level = Integer.parseInt(matcher.group(1));
        String name = matcher.group(2);

        PetDisplay.setTickCooldown();
        PetDisplay.setPetNameCache(name);
        PetDisplay.setPetLevelCache(level);
        PetDisplay.setPetRarityCache(PetDisplay.getRarityFromComponentColor(component.getSiblings().get(1).getStyle().getColor().getValue()));

        PetDisplay.searchForPetItemInTooltip(tooltip);
        PetDisplay.setPetXPLineCache(null);
    }

    private static boolean legacyskyblock$isTogglingFavorite(int buttonNum, ContainerInput containerInput) {
        return buttonNum == 0 && containerInput == ContainerInput.QUICK_MOVE;
    }
}