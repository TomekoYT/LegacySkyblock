package tomeko.legacyskyblock.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.legacyskyblock.config.LegacySkyblockConfig;

@Mixin(LivingEntity.class)
@Environment(EnvType.CLIENT)
public abstract class LivingEntityMixin {
    //No Death Animation
    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onDeath(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!LegacySkyblockConfig.noDeathAnimationEnabled ||
                //#if MC >= 1.21.9
                //$$!self.getEntityWorld().isClient()
                //#else
                !self.getWorld().isClient
            //#endif
        ) {
            return;
        }

        self.discard();
    }
}