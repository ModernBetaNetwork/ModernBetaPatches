package me.collinb.modernbetapatches.mixin;

import me.collinb.modernbetapatches.manager.CapeManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "isPartVisible", at = @At("HEAD"), cancellable = true)
    private void forceEnableCape(PlayerModelPart modelPart, CallbackInfoReturnable<Boolean> cir) {
        if (modelPart == PlayerModelPart.CAPE) {
            AbstractClientPlayerEntity clientPlayer = (AbstractClientPlayerEntity)(Object)this;
            SkinTextures textures = clientPlayer.getSkinTextures();
            Identifier cape = textures.capeTexture();
            if (CapeManager.isModernBetaCape(cape)) {
                cir.setReturnValue(true);
            }
        }
    }
}
