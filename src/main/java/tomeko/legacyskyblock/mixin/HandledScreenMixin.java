package tomeko.legacyskyblock.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

import java.util.Arrays;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {
    protected HandledScreenMixin(Text title) {
        super(title);
    }

    private static final String[] EXCLUDE_GUIS = {"Chest", "Large Chest", "Anvil", "Storage", "Enchant Item", "Drill Anvil", "Runic Pedestal", "Reforge Anvil", "Rune Removal", "Reforge Item", "Exp Sharing", "Offer Pets", "Upgrade Item", "Convert to Dungeon Item", "Hunting Toolkit"};
    MinecraftClient client = MinecraftClient.getInstance();

    //Middle Click GUI Items
    @Inject(method = "method_2383(Lnet/minecraft/class_1735;IILnet/minecraft/class_1713;)V", at = @At("HEAD"), cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo cir) {
        if (
                client.player == null
                        || button != 0
                        || actionType != SlotActionType.PICKUP
                        || Arrays.asList(EXCLUDE_GUIS).contains(this.getTitle().getString())
                        || !LegacySkyblockConfig.middleClickGUIEnabled
                        || !HypixelPackets.onHypixel
                        || (!LegacySkyblockConfig.middleClickGUIOutsideSkyblock && !HypixelPackets.inSkyblock)
        ) return;

        if (!(((HandledScreen<T>) (Object) this).getScreenHandler() instanceof GenericContainerScreenHandler handler))
            return;

        cir.cancel();
        client.interactionManager.clickSlot(handler.syncId, slotId, 2, SlotActionType.CLONE, client.player);
    }
}