package realisticstamina.rstamina.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class TestC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        EntityType.COW.spawn(((ServerWorld) player.getWorld()), null, null);

    }

}
