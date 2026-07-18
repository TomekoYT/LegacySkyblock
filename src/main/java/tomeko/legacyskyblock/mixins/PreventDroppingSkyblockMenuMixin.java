package tomeko.legacyskyblock.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

@Mixin(AbstractContainerScreen.class)
public abstract class PreventDroppingSkyblockMenuMixin {

    private static LocalPlayer pendingPlayer;
    private static int pendingOriginalSlot = -1;

    @WrapOperation(
            method = "slotClicked",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;" +
                            "handleContainerInput(IIILnet/minecraft/world/inventory/ContainerInput;" +
                            "Lnet/minecraft/world/entity/player/Player;)V"
            )
    )
    private void legacyskyblock$preventDrop(
            MultiPlayerGameMode gameMode,
            int containerId,
            int slotId,
            int button,
            ContainerInput action,
            Player player,
            Operation<Void> original
    ) {
        if (action != ContainerInput.THROW
                || !HypixelPackets.inSkyblock
                || !LegacySkyblockConfig.preventDroppingSkyblockMenu
                || !(player instanceof LocalPlayer localPlayer)) {

            original.call(gameMode, containerId, slotId, button, action, player);
            return;
        }

        if (pendingPlayer == localPlayer) {
            try {
                original.call(gameMode, containerId, slotId, button, action, player);
            } finally {
                localPlayer.connection.send(
                        new ServerboundSetCarriedItemPacket(pendingOriginalSlot)
                );

                pendingPlayer = null;
                pendingOriginalSlot = -1;
            }
            return;
        }

        ItemStack heldItem = localPlayer.getMainHandItem();
        ItemStack sourceItem = localPlayer.containerMenu.slots.get(slotId).getItem();

        if (!heldItem.getHoverName().getString().equals("SkyBlock Menu (Click)")
                || sourceItem.isEmpty()
                || sourceItem.getHoverName().getString().equals("SkyBlock Menu (Click)")) {

            original.call(gameMode, containerId, slotId, button, action, player);
            return;
        }

        Inventory inventory = localPlayer.getInventory();
        int originalSlot = inventory.getSelectedSlot();

        Integer temporarySlot = null;
        Integer firstNonMenuSlot = null;

        for (int offset = 1; offset < 9; offset++) {
            int candidate = (originalSlot + offset) % 9;
            ItemStack stack = inventory.getItem(candidate);

            if (stack.isEmpty()) {
                temporarySlot = candidate;
                break;
            }

            if (firstNonMenuSlot == null
                    && !stack.getHoverName().getString().equals("SkyBlock Menu (Click)")) {
                firstNonMenuSlot = candidate;
            }
        }

        if (temporarySlot == null) {
            temporarySlot = firstNonMenuSlot;
        }

        if (temporarySlot == null) {
            original.call(gameMode, containerId, slotId, button, action, player);
            return;
        }

        localPlayer.connection.send(new ServerboundSetCarriedItemPacket(temporarySlot));

        pendingPlayer = localPlayer;
        pendingOriginalSlot = originalSlot;

        original.call(gameMode, containerId, slotId, button, action, player);
    }
}