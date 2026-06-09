package tomeko.legacyskyblock.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;

public class HideDamageSplash {
    private static final String SUBCATEGORY_HIDE_DAMAGE_SPLASH = "Hide Damage Splash";

    @Switch(
            title = "Enabled",
            category = LegacySkyblockConfig.CATEGORY_DUNGEONS,
            subcategory = SUBCATEGORY_HIDE_DAMAGE_SPLASH
    )
    public boolean enabled = false;

    @Switch(
            title = "Work Outside Dungeons",
            category = LegacySkyblockConfig.CATEGORY_DUNGEONS,
            subcategory = SUBCATEGORY_HIDE_DAMAGE_SPLASH
    )
    public boolean workOutsideDungeons = false;

    public HideDamageSplash() {
        ClientTickEvents.END_CLIENT_TICK.register(this::hideSplash);
    }

    private void hideSplash(Minecraft client) {
        if (!enabled
                || !HypixelPackets.inSkyblock
                || (!HypixelPackets.inDungeons && !workOutsideDungeons)
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
