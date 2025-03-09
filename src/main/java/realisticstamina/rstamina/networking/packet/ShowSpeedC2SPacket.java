package realisticstamina.rstamina.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import realisticstamina.rstamina.RStaminaMod;
import realisticstamina.rstamina.RStaminaPlayerState;
import realisticstamina.rstamina.ServerState;
import realisticstamina.rstamina.networking.NetworkingPackets;

public class ShowSpeedC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        // Get the player's state
        RStaminaPlayerState playerState = ServerState.getPlayerState(player);
        
        // Check if speed multiplier feature is enabled
        if (RStaminaMod.config.enableSpeedMultiplier) {
            // Calculate speed multiplier based on max stamina
            double baseStamina = RStaminaMod.config.totalStamina;
            double maxStamina = RStaminaMod.config.fitnessStaminaLimit;
            double currentTotalStamina = playerState.totalStamina;
            
            // Calculate a multiplier between 1.0 and maxSpeedMultiplier based on stamina
            double staminaProgress = Math.min(1.0, Math.max(0.0, (currentTotalStamina - baseStamina) / (maxStamina - baseStamina)));
            double speedMultiplier = 1.0 + (staminaProgress * (RStaminaMod.config.maxSpeedMultiplier - 1.0));
            
            // Send a message to the player
            player.sendMessage(Text.literal(String.format("§bCurrent speed multiplier: §f%.2fx", speedMultiplier)), false);
            
            // Send a packet to the client to show the speed multiplier for 30 seconds (600 ticks)
            PacketByteBuf sendingData = PacketByteBufs.create();
            sendingData.writeInt(600); // 30 seconds * 20 ticks per second
            ServerPlayNetworking.send(player, NetworkingPackets.SEND_SHOW_SPEED_S2C_PACKET_ID, sendingData);
        } else {
            // Send a message to the player that the feature is disabled
            player.sendMessage(Text.literal("§cSpeed multiplier feature is disabled in the configuration."), false);
        }
    }
} 