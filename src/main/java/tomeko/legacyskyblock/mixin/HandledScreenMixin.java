package tomeko.legacyskyblock.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    //Middle Click GUI Items
    private static final String[] EXCLUDE_GUIS_EQUALS = {"Chest", "Large Chest", "Anvil", "Storage", "Enchant Item", "Drill Anvil", "Runic Pedestal", "Reforge Anvil", "Rune Removal", "Reforge Item", "Exp Sharing", "Offer Pets", "Upgrade Item", "Convert to Dungeon Item", "Craft Item", "Fishing Bag", "Potion Bag", "Quiver", "Time Pocket", "Beacon", "Rift Transfer Chest", "Stats Tuning", "Salvage Items", "Pet Sitter", "New Year Cake Bag", "Carnival Mask Bag", "Builder's Ruler", "Builder's Wand", "Basket of Seeds", "Nether Wart Pouch", "Instasell Ignore List", "Trick or Treat Bag", "View Stash", "Island Time", "Garden Time"};
    private static final String[] EXCLUDE_GUIS_STARTSWITH = {"Ender Chest", "Wardrobe", "Accessory Bag (", "Museum", "Rift Storage", "Hunting Toolkit", "You ", "Hunting Box", "Personal ", "The Hex", "Auctions:", "Widgets", "Reclaim Wood Singularity", "Gemstone Grinder"};
    private static final String[] EXCLUDE_GUIS_CONTAINS = {"Backpack", "Minion", "Sack", "iphone", "Trap"};

    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V"))
    private void mouseClicked(HandledScreen instance, Slot slot, int slotId, int button, SlotActionType actionType, Operation<Void> original) {
        if (button != 0
                || actionType != SlotActionType.PICKUP
                || !LegacySkyblockConfig.middleClickGUIEnabled
                || !(instance.getScreenHandler() instanceof GenericContainerScreenHandler)
                || !HypixelPackets.onHypixel
                || (!LegacySkyblockConfig.middleClickGUIOutsideSkyblock && !HypixelPackets.inSkyblock)
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

        original.call(instance, slot, slotId, 2, SlotActionType.CLONE);
    }
}