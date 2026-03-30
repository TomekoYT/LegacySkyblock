package tomeko.legacyskyblock.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

public class HideDamageSplash {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(HideDamageSplash::hideSplash);
    }

    private static void hideSplash(Minecraft client) {
        if (!LegacySkyblockConfig.hideDamageSplashEnabled
                || !HypixelPackets.inSkyblock
                || (!HypixelPackets.inDungeons && !LegacySkyblockConfig.hideDamageSplashWorkOutsideDungeons)
                || client.isPaused()
                || client.level == null
        ) return;

        Iterable<Entity> entities = client.level.entitiesForRendering();
        for (Entity entity : entities) {
            if (entity == null) continue;

            if (entity instanceof ArmorStand armorStandEntity) {
                String name = armorStandEntity.getName().getString().replace(",", "").replace(".", "");

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
    }
}
