package tomeko.legacyskyblock.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class AutoRefill {
    private static String lastServerName = "";
    private static boolean shouldScanInventory = false;
    private static int GFSTickDelay = 0;
    private static final Queue<String> GFSQueue = new LinkedList<>();
    private static int scanTickDelay = 0;
    private static int scanBlockTime = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(AutoRefill::manageRefill);
        ClientTickEvents.END_CLIENT_TICK.register(AutoRefill::manageGFS);
        ClientTickEvents.END_CLIENT_TICK.register(AutoRefill::scanInventory);
    }

    private static void manageRefill(MinecraftClient client) {
        if (!HypixelPackets.inDungeons || lastServerName.equals(HypixelPackets.currentServerName)) {
            return;
        }

        lastServerName = HypixelPackets.currentServerName;
        shouldScanInventory = true;
        GFSTickDelay = 80;
        scanTickDelay = 20;
    }

    private static void scanInventory(MinecraftClient client) {
        if (!shouldScanInventory || client.player == null) {
            return;
        }

        if (scanTickDelay > 0) {
            scanTickDelay--;
            return;
        }

        PlayerInventory inventory = client.player.getInventory();
        if (inventory == null) {
            return;
        }

        scanBlockTime++;
        if (scanBlockTime > 1000) {
            scanBlockTime = 0;
            shouldScanInventory = false;
            return;
        }

        int[] refillCounter = new int[LegacySkyblockConfig.refillItems.length];
        Arrays.fill(refillCounter, 0);
        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) {
                continue;
            }

            shouldScanInventory = false;
            for (int i = 0; i < LegacySkyblockConfig.refillItems.length; i++) {
                if (stack.getName().getString().equals(LegacySkyblockConfig.refillItems[i].name)) {
                    refillCounter[i] += stack.getCount();
                }
            }
        }

        if (shouldScanInventory) {
            return;
        }

        if (scanTickDelay > 0) {
            shouldScanInventory = true;
            scanTickDelay--;
            return;
        }

        for (int i = 0; i < LegacySkyblockConfig.refillItems.length; i++) {
            if (LegacySkyblockConfig.refillEnabled.get(LegacySkyblockConfig.refillItems[i].id) && LegacySkyblockConfig.refillItems[i].maxStackSize - refillCounter[i] > 0) {
                GFSQueue.add("gfs " + LegacySkyblockConfig.refillItems[i].id + " " + (LegacySkyblockConfig.refillItems[i].maxStackSize - refillCounter[i]));
            }
        }
    }

    private static void manageGFS(MinecraftClient client) {
        if (GFSTickDelay > 0) {
            GFSTickDelay--;
            return;
        }
        if (GFSQueue.isEmpty() || client.player == null) {
            return;
        }

        client.player.networkHandler.sendChatCommand(GFSQueue.peek());
        GFSQueue.poll();
        GFSTickDelay = 40;
    }
}
