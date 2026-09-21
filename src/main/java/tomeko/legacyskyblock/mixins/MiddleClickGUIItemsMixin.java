package tomeko.legacyskyblock.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if >= 26.3 {
/*import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.location.HypixelPackets;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class MiddleClickGUIItemsMixin {
    private static final String[] EXCLUDE_GUIS_EQUALS = {"Brewing Stand", "Runic Pedestal", "Rune Removal", "Exp Sharing", "Offer Pets", "Quiver", "Time Pocket", "Beacon", "Pet Sitter", "Builder's Ruler", "Builder's Wand", "Basket of Seeds", "Nether Wart Pouch", "View Stash", "Change all to same color!", "Fishing Rod Parts", "Fast Travel", "Reclaim Menu"};
    private static final String[] EXCLUDE_GUIS_STARTSWITH = {"You ", "Personal ", "The Hex", "Auctions:", "Reclaim Wood Singularity", "Gemstone Grinder"};
    private static final String[] EXCLUDE_GUIS_ENDSWITH = {"Warps", "Armor Sets", "Equipment Sets"};
    private static final String[] EXCLUDE_GUIS_CONTAINS = {"Chest", "Storage", "Backpack", "Anvil", "Minion", "Bag", "Sack", "Trap", "Item"};

    @WrapOperation(
            method = "mouseClicked",
            at = @At(
                    value = "INVOKE",
                    target = //? if >= 26.3 {
                            //"Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;ILnet/minecraft/client/input/MouseButtonEvent;Lnet/minecraft/world/inventory/ContainerInput;)V"
                            //?} else {
                            "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;slotClicked(Lnet/minecraft/world/inventory/Slot;IILnet/minecraft/world/inventory/ContainerInput;)V"
                    //?}
            )
    )
    private void legacyskyblock$useMiddleClick(
            AbstractContainerScreen instance,
            Slot slot,
            int slotId,
            //? if >= 26.3 {
            //MouseButtonEvent event,
            //?} else {
            int buttonNum,
            //?}
            ContainerInput containerInput,
            Operation<Void> original
    ) {
        //? if >= 26.3 {
        /*int buttonNum = switch (event.button()) {
            case 1 -> 0;
            case 3 -> 1;
            default -> event.button();
        };
        *///?}
        if (legacyskyblock$shouldCallOriginal(instance, slot, buttonNum, containerInput)) {
            original.call(
                    instance,
                    slot,
                    slotId,
                    //? if >= 26.3 {
                    //event,
                    //?} else {
                    buttonNum,
                    //?}
                    containerInput
            );
            return;
        }

        original.call(
                instance,
                slot,
                slotId,
                //? if >= 26.3 {
                //new MouseButtonEvent(event.x(), event.y(), new MouseButtonInfo(2, event.modifiers())),
                //?} else {
                2,
                //?}
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
                        || !LegacySkyblockConfig.INSTANCE.getMiddleClickGUIItemsEnabled()
                        || !(instance.getMenu() instanceof ChestMenu)
                        || !HypixelPackets.INSTANCE.getInSkyblock()
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