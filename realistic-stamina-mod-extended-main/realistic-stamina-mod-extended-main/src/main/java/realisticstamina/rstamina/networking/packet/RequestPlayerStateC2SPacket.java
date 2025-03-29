package realisticstamina.rstamina.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import realisticstamina.rstamina.ServerState;
import realisticstamina.rstamina.networking.NetworkingPackets;

public class RequestPlayerStateC2SPacket { //Unused

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        ServerState serverState = ServerState.getServerState(player.getWorld().getServer());

        PacketByteBuf sendingdata = PacketByteBufs.create();
        sendingdata.writeDouble(ServerState.getPlayerState(player).stamina); //stamina
        sendingdata.writeDouble(ServerState.getPlayerState(player).maxStamina); //max stamina
        sendingdata.writeDouble(ServerState.getPlayerState(player).energy); //total stamina
        sendingdata.writeDouble(ServerState.getPlayerState(player).totalStamina); //total stamina
        
        ServerPlayNetworking.send(player, NetworkingPackets.SEND_PLAYERSTATE_S2C_PACKET_ID, sendingdata);

    }

}
