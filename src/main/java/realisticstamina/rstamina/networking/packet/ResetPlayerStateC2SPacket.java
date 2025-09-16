package realisticstamina.rstamina.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import realisticstamina.rstamina.RStaminaPlayerState;
import realisticstamina.rstamina.RStaminaMod;
import realisticstamina.rstamina.ServerState;

public class ResetPlayerStateC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        ServerState serverState = ServerState.getServerState(server);
        RStaminaPlayerState playerstate = ServerState.getPlayerState(player);

        if (player.getInventory().getMainHandStack().getItem() == RStaminaMod.TEST_ITEM) {

            playerstate.stamina = RStaminaMod.config.totalStamina;
            playerstate.maxStamina = RStaminaMod.config.totalStamina;
            playerstate.totalStamina = RStaminaMod.config.totalStamina;
            playerstate.energy = 100.0;
            playerstate.edited = false;
            playerstate.staminaRegenCooldown = 0;
            playerstate.staminaLossRate = 0.25;
            playerstate.staminaGainRate = 0.125;
            playerstate.energyLossRate = 0.004;
            playerstate.energyGainRate = RStaminaMod.config.restingEnergyGainTick;
            serverState.markDirty();

        }

    }

}
