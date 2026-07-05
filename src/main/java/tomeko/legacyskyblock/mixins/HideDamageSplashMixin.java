package tomeko.legacyskyblock.mixins;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
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

@Mixin(EntityRenderer.class)
public abstract class HideDamageSplashMixin<T extends Entity, S extends EntityRenderState> {
    @Inject(method = "getNameTag", at = @At("HEAD"), cancellable = true)
    private void hideDamageSplash(
            T entity,
            CallbackInfoReturnable<Component> cir
    ) {
        if (HypixelPackets.getCurrentIsland() == null
                || !LegacySkyblockConfig.getHideDamageSplashEnabledIslands()[HypixelPackets.getCurrentIsland().ordinal()]
                || !(entity instanceof ArmorStand armorStand)
        ) return;

        String name = StringHelperKt.removeFormatting(armorStand.getName().getString()).replaceAll("\\.", "");
        if (name.endsWith("❤")) name = name.substring(0, name.length() - 1);
        if (name.endsWith("+")) name = name.substring(0, name.length() - 1);
        if (name.endsWith("✧")) name = name.substring(0, name.length() - 1);
        if (name.startsWith("✧")) name = name.substring(1);

        if (StringHelperKt.parseNumber(name) == null) return;

        cir.setReturnValue(null);
    }
}