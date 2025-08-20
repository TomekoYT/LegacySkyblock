package tomeko.legacyskyblock.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

import java.util.Arrays;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    private static final String[] EXCLUDE_GUIS = {"Chest", "Large Chest", "Anvil", "Storage", "Enchant Item", "Drill Anvil", "Runic Pedestal", "Reforge Anvil", "Rune Removal", "Reforge Item", "Exp Sharing", "Offer Pets", "Upgrade Item", "Convert to Dungeon Item", "Hunting Toolkit"};

    //Middle Click GUI Items
    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V"))
    private void middleClick(HandledScreen instance, Slot slot, int slotId, int button, SlotActionType actionType, Operation<Void> original) {
        if (button != 0
                || actionType != SlotActionType.PICKUP
                || Arrays.asList(EXCLUDE_GUIS).contains(instance.getTitle().getString())
                || !LegacySkyblockConfig.middleClickGUIEnabled
                || !HypixelPackets.onHypixel
                || (!LegacySkyblockConfig.middleClickGUIOutsideSkyblock && !HypixelPackets.inSkyblock)
        ) {
            original.call(instance, slot, slotId, button, actionType);
            return;
        }

        if (!(instance.getScreenHandler() instanceof GenericContainerScreenHandler handler)) {
            original.call(instance, slot, slotId, button, actionType);
            return;
        }

        MinecraftClient.getInstance().interactionManager.clickSlot(handler.syncId, slotId, 2, SlotActionType.CLONE, MinecraftClient.getInstance().player);
    }
}