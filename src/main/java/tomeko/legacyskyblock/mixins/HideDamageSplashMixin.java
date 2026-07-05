package tomeko.legacyskyblock.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;
import tomeko.legacyskyblock.utils.HypixelPackets;
import tomeko.legacyskyblock.utils.StringHelperKt;

@Mixin(Entity.class)
public abstract class HideDamageSplashMixin {
    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    private void legacyskyblock$discard(CallbackInfoReturnable<Component> cir) {
        Entity entity = (Entity) (Object) this;

        if (HypixelPackets.getCurrentIsland() == null
                || !LegacySkyblockConfig.getHideDamageSplashEnabledIslands()[HypixelPackets.getCurrentIsland().ordinal()]
                || !(entity instanceof ArmorStand armorStand)
                || armorStand.getCustomName() == null
        ) return;

        String name = StringHelperKt.removeFormatting(armorStand.getCustomName().getString());
        if (name.endsWith("❤")) name = name.substring(0, name.length() - 1);
        if (name.endsWith("+")) name = name.substring(0, name.length() - 1);
        if (name.endsWith("✧")) name = name.substring(0, name.length() - 1);
        if (name.startsWith("✧")) name = name.substring(1);

        if (StringHelperKt.parseNumber(name) == null) return;

        cir.setReturnValue(Component.empty());
        entity.discard();
    }
}