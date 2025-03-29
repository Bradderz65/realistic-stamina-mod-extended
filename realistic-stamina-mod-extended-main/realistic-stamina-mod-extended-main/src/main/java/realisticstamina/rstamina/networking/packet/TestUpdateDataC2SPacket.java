package realisticstamina.rstamina.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import realisticstamina.rstamina.RStaminaPlayerState;
import realisticstamina.rstamina.RStaminaMod;
import realisticstamina.rstamina.ServerState;

public class TestUpdateDataC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        ServerState serverState = ServerState.getServerState(server);
        RStaminaPlayerState playerstate = ServerState.getPlayerState(player);

        if (player.isSprinting()) {

            playerstate.testplayerdata -= 1;
            serverState.markDirty();
            RStaminaMod.LOGGER.info("SPRINTING!!! + " + playerstate.testplayerdata);

        }

    }

}
