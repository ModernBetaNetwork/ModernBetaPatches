package me.collinb.modernbetapatches.mixin;

import com.mojang.authlib.GameProfile;
import me.collinb.modernbetapatches.ModernBetaPatches;
import me.collinb.modernbetapatches.manager.CapeManager;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.core.ClientAsset;
import net.minecraft.world.entity.player.PlayerSkin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;
import java.util.function.Supplier;

@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {

    @Shadow
    @Final
    private GameProfile profile;

    @Inject(method = "createSkinLookup", at = @At("TAIL"))
    private static void loadModernBetaCape(GameProfile profile, CallbackInfoReturnable<Supplier<PlayerSkin>> cir) {
        if (ModernBetaPatches.isModernBeta()) {
            UUID uuid = profile.id();
            CapeManager.fetchCape(uuid);
        }
    }

    @Inject(method = "getSkin", at = @At("TAIL"), cancellable = true)
    private void overrideSkinTextures(CallbackInfoReturnable<PlayerSkin> cir) {
        if (ModernBetaPatches.isModernBeta()) {
            PlayerSkin originalSkinTextures = cir.getReturnValue();
            ClientAsset.Texture modernBetaCape = CapeManager.getCape(profile.id());
            PlayerSkin newSkinTextures = new PlayerSkin(
                    originalSkinTextures.body(),
                    (modernBetaCape == null ? originalSkinTextures.cape() : modernBetaCape),
                    originalSkinTextures.elytra(),
                    originalSkinTextures.model(),
                    originalSkinTextures.secure()
            );
            cir.setReturnValue(newSkinTextures);
        }
    }
}
