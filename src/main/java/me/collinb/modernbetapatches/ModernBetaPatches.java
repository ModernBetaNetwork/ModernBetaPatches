package me.collinb.modernbetapatches;

import me.collinb.modernbetapatches.manager.CapeManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModernBetaPatches implements ClientModInitializer {
    public static String currentServer = null;

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((clientPlayNetworkHandler, packetSender, minecraftClient) -> {
            if (minecraftClient.getCurrentServerEntry() != null) {
                currentServer = minecraftClient.getCurrentServerEntry().address;
                if (isModernBeta()) {
                    CapeManager.fetchCape(clientPlayNetworkHandler.getProfile().getId());
                }
            } else {
                currentServer = null;
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register(((clientPlayNetworkHandler, minecraftClient) -> currentServer = null));

        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (!isModernBeta()) return;
            if (client.world == null) return;

            for (Entity entity : client.world.getEntities()) {
                if (entity instanceof ArmorStandEntity armorStandEntity) {

                    // Check if paper is on armor stand head
                    if (!armorStandEntity.hasStackEquipped(EquipmentSlot.HEAD)) continue;
                    ItemStack headItem = armorStandEntity.getEquippedStack(EquipmentSlot.HEAD);

                    if (headItem.isOf(Items.PAPER)) {
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
