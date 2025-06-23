package me.collinb.modernbetapatches.mixin;

import com.mojang.authlib.GameProfile;
import me.collinb.modernbetapatches.ModernBetaPatches;
import me.collinb.modernbetapatches.manager.CapeManager;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {

    @Shadow
    @Final
    private GameProfile profile;

    @Shadow
    private boolean texturesLoaded;

    @Inject(method = "loadTextures", at = @At("HEAD"))
    private void loadModernBetaCape(CallbackInfo ci) {
        if (ModernBetaPatches.isModernBeta() && !texturesLoaded) {
            UUID uuid = profile.getId();
            CapeManager.fetchCape(uuid);
        }
    }

    @Inject(method = "getCapeTexture", at = @At("TAIL"), cancellable = true)
    private void overrideCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        if (ModernBetaPatches.isModernBeta()) {
            Identifier modernBetaCape = CapeManager.getCape(profile.getId());
            if (modernBetaCape != null) {
                cir.setReturnValue(modernBetaCape);
            }
        }
    }
}
