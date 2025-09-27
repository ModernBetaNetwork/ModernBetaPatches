package me.collinb.modernbetapatches.manager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CapeManager {
    private static final Map<UUID, Identifier> capes = new HashMap<>();

    public static void fetchCape(UUID uuid) {
        String url = "https://cape.modernbeta.org/cape/" + uuid;

        CompletableFuture.runAsync(() -> {
            try (InputStream stream = URI.create(url).toURL().openStream()) {
                NativeImage image = NativeImage.read(stream);

                NativeImage reformatted = new NativeImage(NativeImage.Format.RGBA, 64, 32, true);
                reformatted.fillRect(0, 0, 64, 32, 0x00000000);

                for (int y = 0; y < image.getHeight(); ++y) {
                    for (int x = 0; x < image.getWidth(); ++x) {
                        int color = image.getColorArgb(x, y);
                        reformatted.setColorArgb(x, y, color);
                    }
                }

                Identifier id = Identifier.of("modernbetacapes", uuid.toString());

                MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient.getInstance().getTextureManager()
                            .registerTexture(id, new NativeImageBackedTexture(reformatted));
                    capes.put(uuid, id);
                });
            } catch (IOException ignored) {
                // Cape not found, remove identifier
                capes.remove(uuid);
            }
        });
    }

    public static Identifier getCape(UUID uuid) {
        return capes.get(uuid);
    }

    public static boolean isModernBetaCape(Identifier id) {
        return id != null && "modernbetacapes".equals(id.getNamespace());
    }
}
