package realisticstamina.rstamina.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import realisticstamina.rstamina.RStaminaPlayerState;
import realisticstamina.rstamina.ServerState;

public class EnergyDrinkItem extends Item {

    public EnergyDrinkItem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {

        PlayerEntity playerEntity;
        PlayerEntity playerEntity2 = playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;

        playerEntity.playSound(SoundEvents.ENTITY_WANDERING_TRADER_DRINK_POTION, 1.0F, 1.0F);
        Hand hand = playerEntity.getActiveHand();

        if (hand == Hand.MAIN_HAND && world.isClient) {

            //ClientPlayNetworking.send(ThermNetworkingPackets.DRINK_ICE_JUICE_C2S_PACKET_ID, PacketByteBufs.create());

        } else if (hand == Hand.MAIN_HAND && !world.isClient) {
            playerEntity.getStackInHand(hand).setCount(playerEntity.getStackInHand(hand).getCount() - 1);
            playerEntity.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));

            ServerState serverState = ServerState.getServerState(playerEntity.getWorld().getServer());
            RStaminaPlayerState playerState = ServerState.getPlayerState(playerEntity);

            playerState.energy += 10.0;
            if (playerState.energy > 100.0) { playerState.energy = 100.0; }
            playerState.maxStamina = (playerState.totalStamina * (playerState.energy / 100));
            serverState.markDirty();

        }

        return super.finishUsing(stack, world, user);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

}