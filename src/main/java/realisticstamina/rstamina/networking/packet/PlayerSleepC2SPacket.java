package realisticstamina.rstamina.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import realisticstamina.rstamina.RStaminaMod;
import realisticstamina.rstamina.RStaminaPlayerState;
import realisticstamina.rstamina.ServerState;

public class PlayerSleepC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        ServerState serverState = ServerState.getServerState(server);
        RStaminaPlayerState playerState = ServerState.getPlayerState(player);

        if (RStaminaMod.config.fitnessSystem) {

            double ue = playerState.usedEnergy;

            if (ue < RStaminaMod.config.fitnessUsedEnergyToKeep && ue != 0) {
                if (playerState.totalStamina > RStaminaMod.config.totalStamina) {
                    playerState.totalStamina -= RStaminaMod.config.fitnessStaminaChange;
                    playerState.gainedStamina -= RStaminaMod.config.fitnessStaminaChange;
                    player.sendMessageToClient(Text.literal("§c-" + RStaminaMod.config.fitnessStaminaChange + " Total Stamina"), false);
                    if (playerState.totalStamina < RStaminaMod.config.totalStamina) {
                        playerState.totalStamina = RStaminaMod.config.totalStamina;
                        playerState.gainedStamina = 0.0;
                    }
                }
            } else if (ue > RStaminaMod.config.fitnessUsedEnergyToGain) {
                if (playerState.totalStamina < RStaminaMod.config.fitnessStaminaLimit) {
                    playerState.totalStamina += RStaminaMod.config.fitnessStaminaChange;
                    playerState.gainedStamina += RStaminaMod.config.fitnessStaminaChange;
                    player.sendMessageToClient(Text.literal("§a+" + RStaminaMod.config.fitnessStaminaChange + " Total Stamina"), false);
                    if (playerState.totalStamina > RStaminaMod.config.fitnessStaminaLimit) {
                        playerState.totalStamina = RStaminaMod.config.fitnessStaminaLimit;
                        playerState.gainedStamina = RStaminaMod.config.fitnessStaminaLimit - playerState.totalStamina;
                    }
                }
            }
        }

        playerState.energy = 100.0;
        playerState.usedEnergy = 0.0;
        playerState.maxStamina = (playerState.totalStamina * (playerState.energy / 100));
        playerState.energyFromResting = 0.0;
        serverState.markDirty();

    }

}
