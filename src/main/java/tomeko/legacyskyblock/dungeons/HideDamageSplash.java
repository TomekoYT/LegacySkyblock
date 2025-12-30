package tomeko.legacyskyblock.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

public class HideDamageSplash {
    public static void register() {
        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (!LegacySkyblockConfig.hideDamageSplashEnabled
            || !HypixelPackets.inSkyblock
            || (!HypixelPackets.inDungeons && !LegacySkyblockConfig.hideDamageSplashWorkOutsideDungeons)
            ) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.isPaused()) return;

            for (Entity entity : world.getEntities()) {
                if (entity instanceof ArmorStandEntity armorStandEntity) {
                    String name = armorStandEntity.getName().getString().replace(",", "").replace(".","");

                    if (name.endsWith("k") || name.endsWith("M")) {
                        name = name.substring(0, name.length() - 1);
                    }

                    try {
                        Long.parseLong(name);
                        entity.discard();
                        continue;
                    } catch (NumberFormatException ignored) {
                    }

                    if (name.length() >= 2) {
                        name = name.substring(1, name.length() - 1);
                    }

                    if (name.endsWith("k") || name.endsWith("M")) {
                        name = name.substring(0, name.length() - 1);
                    }

                    try {
                        Long.parseLong(name);
                        entity.discard();
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        });
    }
}
