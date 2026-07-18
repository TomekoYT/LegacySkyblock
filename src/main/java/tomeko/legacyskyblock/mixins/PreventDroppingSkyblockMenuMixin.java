package tomeko.legacyskyblock.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

public abstract class PreventDroppingSkyblockMenuMixin {
    @WrapOperation(
            method = "slotClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;handleContainerInput(IIILnet/minecraft/world/inventory/ContainerInput;Lnet/minecraft/world/entity/player/Player;)V"
            )
    )
    private void preventDrop(
            MultiPlayerGameMode gameMode,
            int containerId,
            int slotId,
            int button,
            ContainerInput action,
            Player player,
            Operation<Void> original
    ) {
        int originalSlot = -1;

        if (action != ContainerInput.THROW
                || !HypixelPackets.inSkyblock
                || !LegacySkyblockConfig.preventDroppingSkyblockMenu
                || !(player instanceof LocalPlayer localPlayer)) return;

        ItemStack sourceItem = localPlayer.containerMenu.slots.get(slotId).getItem();

        if (localPlayer.getMainHandItem().getHoverName().getString().equals("SkyBlock Menu (Click)")
                && !sourceItem.isEmpty()
                && !sourceItem.getHoverName().getString().equals("SkyBlock Menu (Click)")) {

            Inventory inventory = localPlayer.getInventory();
            originalSlot = inventory.getSelectedSlot();

            Integer temporarySlot = null;
            Integer firstNonMenuSlot = null;

            for (int offset = 1; offset < 9; offset++) {
                int candidate = (originalSlot + offset) % 9;
                ItemStack item = inventory.getItem(candidate);

                if (item.isEmpty()) {
                    temporarySlot = candidate;
                    break;
                }

                if (firstNonMenuSlot == null && !item.getHoverName().getString().equals("SkyBlock Menu (Click)")) {
                    firstNonMenuSlot = candidate;
                }
            }

            if (temporarySlot == null) {
                temporarySlot = firstNonMenuSlot;
            }

            if (temporarySlot != null) {
                localPlayer.connection.send(new ServerboundSetCarriedItemPacket(temporarySlot));
            } else {
                localPlayer = null;
            }
        }

        try {
            original.call(gameMode, containerId, slotId, button, action, player);
        } finally {
            if (localPlayer != null) {
                localPlayer.connection.send(new ServerboundSetCarriedItemPacket(originalSlot));
            }
        }
    }
}
