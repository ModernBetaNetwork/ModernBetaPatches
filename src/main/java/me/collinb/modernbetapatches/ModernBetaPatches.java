package me.collinb.modernbetapatches;

import me.collinb.modernbetapatches.manager.CapeManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModernBetaPatches implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("modernbetapatches");

    public static String currentServer = null;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, _, minecraftClient) -> {
            if (minecraftClient.getCurrentServer() != null) {
                currentServer = minecraftClient.getCurrentServer().ip;
                if (isModernBeta()) {
                    CapeManager.fetchCape(clientPlayNetworkHandler.getLocalGameProfile().id());
                }
            } else {
                currentServer = null;
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register(((_, _) -> currentServer = null));

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (!isModernBeta()) return;
            if (client.level == null) return;

            for (net.minecraft.world.entity.Entity entity : client.level.entitiesForRendering()) {
                if (entity instanceof ArmorStand armorStandEntity) {

                    // Check if paper is on armor stand head
                    var headItem = armorStandEntity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);

                    if (headItem.is(net.minecraft.world.item.Items.PAPER)) {
                        armorStandEntity.discard();
                    }
                }
            }
        });
    }

    public static boolean isModernBeta() {
        return currentServer != null && currentServer.contains(".modernbeta.org");
    }
}
