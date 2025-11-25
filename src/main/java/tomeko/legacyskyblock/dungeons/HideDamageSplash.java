package tomeko.legacyskyblock.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

public class HideDamageSplash {
    public static void register() {
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (!LegacySkyblockConfig.hideDamageSplashEnabled) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.isPaused()) return;

            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStandEntity armorStandEntity) {
                    String name = armorStandEntity.getName().getString();
                    if (name.startsWith("✧") && name.endsWith("✧")) {
                        name = name.substring(1, name.length() - 1);
                    }

                    try {
                        Long.parseLong(name.replace(",", ""));
                        entity.discard();
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        });
    }
}
