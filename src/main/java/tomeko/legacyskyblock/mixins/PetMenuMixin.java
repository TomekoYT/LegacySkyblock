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
import tomeko.legacyskyblock.utils.Debug;
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

        Component component = legacyskyblock$removeFavoriteAndSkinStars(slot.getItem().getHoverName());
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

        if (canDespawn && !legacyskyblock$isTogglingFavorite(buttonNum, containerInput)) {
            PetDisplay.Companion.setTickCooldown();
            PetDisplay.Companion.resetAll();
            return;
        }

        if (!canSummon || buttonNum == 1 || legacyskyblock$isTogglingFavorite(buttonNum, containerInput)) return;

        String level = nameLineMatcher.group(1);
        String name = nameLineMatcher.group(2);

        PetDisplay.Companion.setTickCooldown();
        PetDisplay.Companion.setPetName(name);
        PetDisplay.Companion.setPetLevel(level);
        PetDisplay.Companion.setPetRarity(PetDisplay.Companion.getRarityFromComponentColor(component.getSiblings().get(1).getStyle().getColor().getValue()));

        legacyskyblock$searchForPetItem(tooltip);
        legacyskyblock$searchForPetXP(tooltip);
    }

    private static void legacyskyblock$searchForPetItem(List<Component> tooltip) {
        for (Component line : tooltip) {
            Pattern pattern = Pattern.compile("^Held Item: (.*)$");
            Matcher matcher = pattern.matcher(line.getString());
            if (matcher.find()) {
                PetDisplay.Companion.setPetItem(matcher.group(1));
                PetDisplay.Companion.setPetItemRarity(PetDisplay.Companion.getRarityFromComponentColor(line.getSiblings().get(1).getStyle().getColor().getValue()));
                return;
            }
        }

        PetDisplay.Companion.resetItem();
    }

    private static void legacyskyblock$searchForPetXP(List<Component> tooltip) {
        for (Component line : tooltip) {
            Pattern pattern = Pattern.compile("^ {26}(.*)$");
            Matcher matcher = pattern.matcher(line.getString());
            if (matcher.find()) {
                MutableComponent copy = line.copy();
                for (int i = 0; i < 3; i++) {
                    if (copy.getSiblings().getFirst().getString().equals("0")) break;

                    copy.getSiblings().removeFirst();
                }
                PetDisplay.Companion.setPetXPLeft(copy.getSiblings().getFirst().getString());
                PetDisplay.Companion.setPetXPRight(copy.getSiblings().get(2).getString());
                return;
            }
        }

        PetDisplay.Companion.resetXP();
    }

    private static Component legacyskyblock$removeFavoriteAndSkinStars(Component name) {
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

    private static boolean legacyskyblock$isTogglingFavorite(int buttonNum, ContainerInput containerInput) {
        return buttonNum == 0 && containerInput == ContainerInput.QUICK_MOVE;
    }
}