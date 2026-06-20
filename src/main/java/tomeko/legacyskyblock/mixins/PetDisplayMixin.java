package tomeko.legacyskyblock.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
public abstract class PetDisplayMixin {
    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void detectClickOnPet(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
        AbstractContainerScreen<?> instance = (AbstractContainerScreen<?>) (Object) this;
        if (!HypixelPackets.inSkyblock
                || !(instance.getMenu() instanceof ChestMenu)
                || !instance.getTitle().getString().endsWith("Pets")
        ) return;

        Component component = removeFavoriteAndSkinStars(slot.getItem().getHoverName());
        Pattern nameLinePattern = Pattern.compile("^\\[Lvl (\\d+)] (.*)$");
        Matcher nameLineMatcher = nameLinePattern.matcher(component.getString());
        if (!nameLineMatcher.find()) return;

        List<Component> tooltip = slot.getItem().getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
        boolean canSummon = false;
        boolean canDespawn = false;
        for (Component line : tooltip) {
            if (line.getString().equals("Left-click to summon!")) canSummon = true;
            else if (line.getString().equals("Click to despawn!")) canDespawn = true;
        }

        if (canDespawn && !isTogglingFavorite(buttonNum, containerInput)) {
            PetDisplay.Companion.resetAll();
            return;
        }

        if (!canSummon || buttonNum == 1 || isTogglingFavorite(buttonNum, containerInput)) return;

        int level = Integer.parseInt(nameLineMatcher.group(1));
        String name = nameLineMatcher.group(2);

        PetDisplay.Companion.setFormattedPetNameLine(component);
        PetDisplay.Companion.setPetName(name);
        PetDisplay.Companion.setPetLevel(level);

        searchForPetItem(tooltip);
        searchForPetXP(tooltip);
    }

    private static void searchForPetItem(List<Component> tooltip) {
        for (Component line : tooltip) {
            Pattern pattern = Pattern.compile("^Held Item: (.*)$");
            Matcher matcher = pattern.matcher(line.getString());
            if (matcher.find()) {
                MutableComponent copy = line.copy();
                copy.getSiblings().removeFirst();
                PetDisplay.Companion.setFormattedPetItemLine(copy);
                PetDisplay.Companion.setPetItem(matcher.group(1));
                return;
            }
        }

        PetDisplay.Companion.resetItem();
    }

    private static void searchForPetXP(List<Component> tooltip) {
        for (Component line : tooltip) {
            Pattern pattern = Pattern.compile("^ {26}(.*)$");
            Matcher matcher = pattern.matcher(line.getString());
            if (matcher.find()) {
                MutableComponent copy = line.copy();
                for (int i = 0; i < 4; i++) {
                    copy.getSiblings().removeFirst();
                }
                PetDisplay.Companion.setFormattedPetXPLine(copy);
                return;
            }
        }

        PetDisplay.Companion.resetXP();
    }

    private static Component removeFavoriteAndSkinStars(Component name) {
        if (name.getSiblings().isEmpty()) return name;

        MutableComponent copy = name.copy();

        Component first = copy.getSiblings().getFirst();
        if (first.getString().startsWith("⭐ ")) {
            copy.getSiblings().removeFirst();
        }

        if (copy.getSiblings().isEmpty()) return copy;

        Component last = copy.getSiblings().getLast();
        if (last.getString().endsWith(" ✦")) {
            copy.getSiblings().removeLast();
        }

        return copy;
    }

    private static boolean isTogglingFavorite(int buttonNum, ContainerInput containerInput) {
        return buttonNum == 0 && containerInput == ContainerInput.QUICK_MOVE;
    }
}