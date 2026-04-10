package me.collinb.modernbetapatches.mixin;

import me.collinb.modernbetapatches.manager.CapeManager;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class AvatarRendererMixin {

    @Inject(method = "extractRenderState*", at = @At("TAIL"))
    private void forceCapeVisibility(Avatar entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        if (CapeManager.hasModernBetaCape(entity.getUUID())) {
            state.showCape = true;
        }
    }
}
