package tomeko.legacyskyblock.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class MiddleClickGUIItemsMixin {
    private static final String[] EXCLUDE_GUIS_EQUALS = {"Runic Pedestal", "Rune Removal", "Exp Sharing", "Offer Pets", "Quiver", "Time Pocket", "Beacon", "Pet Sitter", "Builder's Ruler", "Builder's Wand", "Basket of Seeds", "Nether Wart Pouch", "View Stash", "Change all to same color!", "Fishing Rod Parts", "Fast Travel"};
    private static final String[] EXCLUDE_GUIS_STARTSWITH = {"You ", "Personal ", "The Hex", "Auctions:", "Reclaim Wood Singularity", "Gemstone Grinder"};
    private static final String[] EXCLUDE_GUIS_ENDSWITH = {"Warps", "Armor Sets", "Equipment Sets"};
    private static final String[] EXCLUDE_GUIS_CONTAINS = {"Chest", "Storage", "Backpack", "Anvil", "Minion", "Bag", "Sack", "Trap", "Item"};

    @WrapOperation(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V"
            )
    )
    private void legacyskyblock$useMiddleClick(
            AbstractContainerScreen instance,
            Slot slot,
            int slotId,
            int buttonNum,
            ContainerInput containerInput,
            Operation<Void> original
    ) {
        if (legacyskyblock$shouldCallOriginal(instance, slot, buttonNum, containerInput)) {
            original.call(instance, slot, slotId, buttonNum, containerInput);
            return;
        }

        original.call(
                instance,
                slot,
                slotId,
                2,
                ContainerInput.CLONE
        );
    }

    private static boolean legacyskyblock$shouldCallOriginal(
            AbstractContainerScreen instance,
            Slot slot,
            int buttonNum,
            ContainerInput containerInput
    ) {
        if (
                buttonNum != 0
                        || containerInput != ContainerInput.PICKUP
                        || !LegacySkyblockConfig.middleClickGUIItemsEnabled
                        || !(instance.getMenu() instanceof ChestMenu)
                        || !HypixelPackets.inSkyblock
                        || slot == null
        ) return true;

        List<Component> tooltip = slot.getItem().getTooltipLines(Item.TooltipContext.EMPTY, Minecraft.getInstance().player, TooltipFlag.NORMAL);
        for (Component line : tooltip) {
            if (legacyskyblock$moreThanOneButton(line.getString())) {
                return true;
            }
        }

        for (String excluded : EXCLUDE_GUIS_EQUALS) {
            if (instance.getTitle().getString().equals(excluded)) {
                return true;
            }
        }
        for (String excluded : EXCLUDE_GUIS_STARTSWITH) {
            if (instance.getTitle().getString().startsWith(excluded)) {
                return true;
            }
        }
        for (String excluded : EXCLUDE_GUIS_ENDSWITH) {
            if (instance.getTitle().getString().endsWith(excluded)) {
                return true;
            }
        }
        for (String excluded : EXCLUDE_GUIS_CONTAINS) {
            if (instance.getTitle().getString().contains(excluded)) {
                return true;
            }
        }

        return false;
    }

    private static boolean legacyskyblock$moreThanOneButton(String text) {
        text = text.toLowerCase();

        return text.contains("right-click")
                || text.contains("right click")
                || text.contains("left-click")
                || text.contains("left click");
    }
}