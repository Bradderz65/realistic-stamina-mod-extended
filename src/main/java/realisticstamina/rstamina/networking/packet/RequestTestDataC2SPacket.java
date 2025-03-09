package realisticstamina.rstamina.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import realisticstamina.rstamina.RStaminaMod;
import realisticstamina.rstamina.ServerState;
import realisticstamina.rstamina.networking.NetworkingPackets;

public class RequestTestDataC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        PacketByteBuf sendingdata = PacketByteBufs.create();
        sendingdata.writeInt(ServerState.getPlayerState(player).testplayerdata);
        RStaminaMod.LOGGER.info("REEE: " + sendingdata.getInt(0));

        ServerPlayNetworking.send(player, NetworkingPackets.SEND_TESTDATA_PACKET_ID, sendingdata);

    }

}
