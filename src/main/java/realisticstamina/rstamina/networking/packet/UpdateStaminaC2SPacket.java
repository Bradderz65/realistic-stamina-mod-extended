package realisticstamina.rstamina.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import realisticstamina.rstamina.RStaminaMod;
import realisticstamina.rstamina.RStaminaPlayerState;
import realisticstamina.rstamina.ServerState;
import realisticstamina.rstamina.networking.NetworkingPackets;

public class UpdateStaminaC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {

        ServerState serverState = ServerState.getServerState(server);
        RStaminaPlayerState playerstate = ServerState.getPlayerState(player);

        boolean waterLogged = false;

        if (player.getWorld().getBlockState(player.getBlockPos()) == Blocks.WATER.getDefaultState()) {
            waterLogged = true;
        }

        if (playerstate.staminaRegenCooldown > 0) {
            playerstate.staminaRegenCooldown -= 1;
        }

        if (playerstate.miningFatigueCooldown > 0) {
            playerstate.miningFatigueCooldown -= 1;
        }

        // Check for jump (was on ground last tick, not on ground now, and moving upward)
        if (playerstate.wasOnGround && !player.isOnGround() && !player.isTouchingWater() 
            && !player.isCreative() && !player.isSpectator() 
            && player.getVelocity().y > 0) {  // Only count as jump if moving upward
            playerstate.stamina -= RStaminaMod.config.jumpStaminaCost;
            if (RStaminaMod.config.enableEnergySystem) {
                playerstate.energy -= 0.03;
                playerstate.usedEnergy += 0.03;
                playerstate.maxStamina = (playerstate.totalStamina * (playerstate.energy / 100));
            }
            playerstate.staminaRegenCooldown = 20;
            serverState.markDirty();
        }
        playerstate.wasOnGround = player.isOnGround();

        if (player.isSprinting()) {

            if (!player.isCreative() && !player.isSpectator()) {

                playerstate.stamina -= playerstate.staminaLossRate;
                if (RStaminaMod.config.enableEnergySystem) {
                    playerstate.energy -= playerstate.energyLossRate;
                    playerstate.usedEnergy += playerstate.energyLossRate;
                    playerstate.maxStamina = (playerstate.totalStamina * (playerstate.energy / 100));
                }
                serverState.markDirty();
            }

        } else if (!player.isSprinting() && !player.isSwimming() && !player.isClimbing() && playerstate.stamina < playerstate.maxStamina) {

            if (playerstate.staminaRegenCooldown <= 0) {
                if (!player.isSneaking()) {
                    playerstate.stamina += playerstate.staminaGainRate;
                    if (playerstate.stamina > playerstate.maxStamina) {
                        playerstate.stamina = playerstate.maxStamina;
                    }
                } else if (player.isSneaking()) {
                    playerstate.stamina += 0.21875;
                    if (playerstate.stamina > playerstate.maxStamina) {
                        playerstate.stamina = playerstate.maxStamina;
                    }
                }
            }

        }

        if (playerstate.stamina <= 24.0 && playerstate.stamina > 12.0) {

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3, 1, true, false));

        } else if (playerstate.stamina <= 12.0 && playerstate.stamina > 5.0) {

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3, 4, true, false));

        } else if (playerstate.stamina <= 5.0 && playerstate.stamina > 0.0) {

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3, 4, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 3, 1, true, false));
            playerstate.miningFatigueCooldown = RStaminaMod.config.miningFatigueCooldownTicks;

        } else if (playerstate.stamina <= 0.0) {

            playerstate.stamina = 0.0;
            serverState.markDirty();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 3, 5, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 3, 2, true, false));
            playerstate.miningFatigueCooldown = RStaminaMod.config.miningFatigueCooldownTicks;

        } else if (playerstate.stamina > 5.0 && playerstate.miningFatigueCooldown > 0) {
            // Keep mining fatigue effect during cooldown period
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 3, 1, true, false));
        }

        if (playerstate.stamina > playerstate.maxStamina) {
            playerstate.stamina = playerstate.maxStamina;
        }

        // Commented out to make stamina and energy persist over death
        /*if (player.getHealth() <= 0.0) {
            playerstate.stamina = playerstate.maxStamina;
            playerstate.energy = 100.0;
            playerstate.energyFromResting = 0.0;
            playerstate.maxStamina = playerstate.totalStamina;
        }*/

        PacketByteBuf sendingdata = PacketByteBufs.create();
        sendingdata.writeDouble(ServerState.getPlayerState(player).stamina); //stamina
        sendingdata.writeDouble(ServerState.getPlayerState(player).maxStamina); //max stamina
        sendingdata.writeDouble(ServerState.getPlayerState(player).energy); //total stamina
        sendingdata.writeDouble(ServerState.getPlayerState(player).totalStamina); //total stamina

        ServerPlayNetworking.send(player, NetworkingPackets.SEND_PLAYERSTATE_S2C_PACKET_ID, sendingdata);

    }

}
