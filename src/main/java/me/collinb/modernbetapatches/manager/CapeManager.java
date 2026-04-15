package me.collinb.modernbetapatches.manager;

import com.mojang.blaze3d.platform.NativeImage;
import me.collinb.modernbetapatches.ModernBetaPatches;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class CapeManager {
    private static final Set<UUID> capes = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void fetchCape(@NonNull UUID uuid) {
        String path = uuid.toString().toLowerCase(Locale.ROOT);
        String formattedPath = "textures/" + path + ".png";
        String url = "https://cape.modernbeta.org/cape/" + uuid;

        Identifier id = Identifier.fromNamespaceAndPath("modernbetacapes", formattedPath);

        CompletableFuture.runAsync(() -> {
            try (InputStream stream = URI.create(url).toURL().openStream()) {
                NativeImage image = NativeImage.read(stream);

                NativeImage reformatted = getReformatted(image);

                image.close();

                Minecraft.getInstance().execute(() -> {
                    Minecraft.getInstance().getTextureManager().register(id, new DynamicTexture(id::toString, reformatted));
                    capes.add(uuid);
                    ModernBetaPatches.LOGGER.info("Registered cape {}", formattedPath);
                });
            } catch (Exception e) {
                capes.remove(uuid);
            }
        });
    }

    private static @NonNull NativeImage getReformatted(@NonNull NativeImage image) {
        NativeImage reformatted = new NativeImage(NativeImage.Format.RGBA, 64, 32, true);
        reformatted.fillRect(0, 0, 64, 32, 0x00000000);

        int[] sourcePixels = image.getPixelsABGR();
        int width = Math.min(image.getWidth(), 64);
        int height = Math.min(image.getHeight(), 32);

        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int color = sourcePixels[x + y * width];
                reformatted.setPixelABGR(x, y, color);
            }
        }
        return reformatted;
    }

    public static ClientAsset.@Nullable Texture getCape(UUID uuid) {
        if (hasModernBetaCape(uuid)) {
            String path = uuid.toString().toLowerCase(Locale.ROOT);
            Identifier capeIdentifier = Identifier.fromNamespaceAndPath("modernbetacapes", path);
            return new ClientAsset.ResourceTexture(capeIdentifier);
        }
        return null;
    }

    public static boolean hasModernBetaCape(UUID uuid) {
        return capes.contains(uuid);
    }
}
