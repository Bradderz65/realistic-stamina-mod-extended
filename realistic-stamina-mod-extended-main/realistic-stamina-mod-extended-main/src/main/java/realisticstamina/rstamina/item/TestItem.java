package realisticstamina.rstamina.item;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import realisticstamina.rstamina.networking.NetworkingPackets;

public class TestItem extends Item {

    public TestItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {

        playerEntity.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);

        if (hand == Hand.MAIN_HAND && world.isClient) {

            //playerEntity.sendMessage(Text.literal("hello"));
            //ClientPlayNetworking.send(NetworkingPackets.TEST_PACKET_ID, PacketByteBufs.create());

            ClientPlayNetworking.send(NetworkingPackets.RESET_PLAYERSTATE_C2S_PACKET_ID, PacketByteBufs.create());
            //RStaminaMod.LOGGER.info(playerEntity.getName().getString() + " : " + RStaminaClient.clientStoredTestPlayerData);

        }

        /*if (hand == Hand.MAIN_HAND && !world.isClient) {

            ServerPlayNetworking.send(((ServerPlayerEntity) playerEntity), NetworkingPackets.TEST_S2C_PACKET_ID, PacketByteBufs.empty());

        }*/



        return TypedActionResult.success(playerEntity.getStackInHand(hand));
    }

}
