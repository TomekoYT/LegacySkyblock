package tomeko.legacyskyblock.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ChestMenu;
//? if >= 26.1 {
/*import net.minecraft.world.inventory.ContainerInput;
*///?} else {
import net.minecraft.world.inventory.ClickType;
//?}
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin {
    //Middle Click GUI Items
    private static final String[] EXCLUDE_GUIS_EQUALS = {"Chest", "Large Chest", "Anvil", "Storage", "Enchant Item", "Drill Anvil", "Runic Pedestal", "Reforge Anvil", "Rune Removal", "Reforge Item", "Exp Sharing", "Offer Pets", "Upgrade Item", "Convert to Dungeon Item", "Craft Item", "Fishing Bag", "Potion Bag", "Quiver", "Time Pocket", "Beacon", "Rift Transfer Chest", "Stats Tuning", "Salvage Items", "Pet Sitter", "New Year Cake Bag", "Carnival Mask Bag", "Builder's Ruler", "Builder's Wand", "Basket of Seeds", "Nether Wart Pouch", "Instasell Ignore List", "Trick or Treat Bag", "View Stash", "Island Time", "Garden Time", "Change all to same color!", "Heart of the Mountain", "Heart of the Forest"};
    private static final String[] EXCLUDE_GUIS_STARTSWITH = {"Ender Chest", "Wardrobe", "Accessory Bag (", "Museum", "Rift Storage", "Hunting Toolkit", "You ", "Hunting Box", "Personal ", "The Hex", "Auctions:", "Widgets", "Reclaim Wood Singularity", "Gemstone Grinder"};
    private static final String[] EXCLUDE_GUIS_CONTAINS = {"Backpack", "Minion", "Sack", "iphone", "Trap"};

    //? if >= 26.1 {
    
    /*private static final ContainerInput pickup = ContainerInput.PICKUP;
    private static final ContainerInput clone = ContainerInput.CLONE;
     
    *///?} else {
    private static final ClickType pickup = ClickType.PICKUP;
    private static final ClickType clone = ClickType.CLONE;
    //?}

    //? if >= 26.1 {
    
    /*@WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V"))
    private void mouseClicked(AbstractContainerScreen instance, Slot slot, int slotId, int button, ContainerInput actionType, Operation<Void> original) {
     
    *///?} else {
    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ClickType;)V"))
    private void mouseClicked(AbstractContainerScreen instance, Slot slot, int slotId, int button, ClickType actionType, Operation<Void> original) {
    //?}
        if (button != 0
                || actionType != pickup
                || !LegacySkyblockConfig.middleClickGUIEnabled
                || !(instance.getMenu() instanceof ChestMenu)
                || !HypixelPackets.onHypixel
                || (!LegacySkyblockConfig.middleClickGUIWorkOutsideSkyblock && !HypixelPackets.inSkyblock)
        ) {
            original.call(instance, slot, slotId, button, actionType);
            return;
        }
        for (String excluded : EXCLUDE_GUIS_EQUALS) {
            if (instance.getTitle().getString().equals(excluded)) {
                original.call(instance, slot, slotId, button, actionType);
                return;
            }
        }
        for (String excluded : EXCLUDE_GUIS_STARTSWITH) {
            if (instance.getTitle().getString().startsWith(excluded)) {
                original.call(instance, slot, slotId, button, actionType);
                return;
            }
        }
        for (String excluded : EXCLUDE_GUIS_CONTAINS) {
            if (instance.getTitle().getString().contains(excluded)) {
                original.call(instance, slot, slotId, button, actionType);
                return;
            }
        }

        original.call(instance, slot, slotId, 2, clone);
    }
}