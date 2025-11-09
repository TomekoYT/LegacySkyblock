package tomeko.legacyskyblock.misc;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

public class NoDeathAnimation {
    public static void register() {
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (!LegacySkyblockConfig.noDeathAnimationEnabled) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.isPaused()) return;

            for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity livingEntity && livingEntity.isDead()) {
                    entity.discard();
                }
            }
        });
    }
}
