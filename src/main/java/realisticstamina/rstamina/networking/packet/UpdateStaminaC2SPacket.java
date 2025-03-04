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
import net.minecraft.text.Text;
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

        // Handle exhaustion rest timer
        if (playerstate.isExhausted) {
            if (!player.isSprinting() && !player.isSwimming() && !player.isClimbing() && player.isOnGround()) {
                playerstate.exhaustionRestTimer -= 1;
                if (playerstate.exhaustionRestTimer <= 0) {
                    playerstate.isExhausted = false;
                    if (RStaminaMod.config.showExhaustionWarning) {
                        player.sendMessage(Text.literal("§a✓ You've recovered from exhaustion."), true);
                    }
                }
            } else {
                // Reset timer if player moves
                playerstate.exhaustionRestTimer = RStaminaMod.config.exhaustionRecoveryTicks;
            }
        }

        // Check for stamina-consuming actions while at low stamina
        boolean isLowStamina = playerstate.stamina <= RStaminaMod.config.exhaustionThreshold;
        boolean performedStaminaAction = false;

        // Check for jump (was on ground last tick, not on ground now, and moving upward)
        if (playerstate.wasOnGround && !player.isOnGround() && !player.isTouchingWater() 
            && !player.isCreative() && !player.isSpectator() 
            && player.getVelocity().y > 0) {  // Only count as jump if moving upward
            performedStaminaAction = true;
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
                performedStaminaAction = true;
                playerstate.stamina -= playerstate.staminaLossRate;
                if (RStaminaMod.config.enableEnergySystem) {
                    playerstate.energy -= playerstate.energyLossRate;
                    playerstate.usedEnergy += playerstate.energyLossRate;
                    playerstate.maxStamina = (playerstate.totalStamina * (playerstate.energy / 100));
                }
                serverState.markDirty();
            }
        } else if (!player.isSprinting() && !player.isSwimming() && !player.isClimbing() && playerstate.stamina < playerstate.maxStamina) {
            if (playerstate.staminaRegenCooldown <= 0 && !playerstate.isExhausted) {
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

        // Check if player should enter exhaustion state
        if (isLowStamina && performedStaminaAction && !playerstate.isExhausted) {
            playerstate.isExhausted = true;
            playerstate.exhaustionRestTimer = RStaminaMod.config.exhaustionRecoveryTicks;
            if (RStaminaMod.config.showExhaustionWarning) {
                player.sendMessage(Text.literal("§c⚠ You're exhausted! Rest for a moment to recover."), true);
            }
        }

        // Apply status effects
        if (playerstate.stamina <= 24.0 && playerstate.stamina > 12.0) {

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 1, true, false));

        } else if (playerstate.stamina <= 12.0 && playerstate.stamina > 5.0) {

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 4, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 40, 0, true, false));

        } else if (playerstate.stamina <= 5.0 && playerstate.stamina > 0.0) {

            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 4, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 60, 2, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 1, true, false));
            playerstate.miningFatigueCooldown = RStaminaMod.config.miningFatigueCooldownTicks;

        } else if (playerstate.stamina <= 0.0) {

            playerstate.stamina = 0.0;
            serverState.markDirty();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 5, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 80, 2, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 2, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 80, 0, true, false));
            playerstate.miningFatigueCooldown = RStaminaMod.config.miningFatigueCooldownTicks;

        } else if (playerstate.stamina > 5.0 && playerstate.miningFatigueCooldown > 0) {
            // Keep mining fatigue effect during cooldown period
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 1, true, false));
        }

        if (playerstate.stamina > playerstate.maxStamina) {
            playerstate.stamina = playerstate.maxStamina;
        }

        // Calculate speed multiplier based on max stamina (if enabled)
        double speedMultiplier = 1.0;
        if (RStaminaMod.config.enableSpeedMultiplier) {
            double baseStamina = RStaminaMod.config.totalStamina;
            double maxStamina = RStaminaMod.config.fitnessStaminaLimit;
            double currentTotalStamina = playerstate.totalStamina;
            
            // Calculate a multiplier between 1.0 and maxSpeedMultiplier based on stamina
            double staminaProgress = Math.min(1.0, Math.max(0.0, (currentTotalStamina - baseStamina) / (maxStamina - baseStamina)));
            speedMultiplier = 1.0 + (staminaProgress * (RStaminaMod.config.maxSpeedMultiplier - 1.0));
        }
        
        // Update the speed multiplier if it has changed
        if (Math.abs(playerstate.speedMultiplier - speedMultiplier) > 0.01) {
            playerstate.speedMultiplier = speedMultiplier;
            // Send a debug message to the player only if the feature is enabled and multiplier > 1.0
            if (RStaminaMod.config.enableSpeedMultiplier && speedMultiplier > 1.01) {
                player.sendMessage(Text.literal(String.format("§bSpeed multiplier: §f%.2fx", speedMultiplier)), true);
            }
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
        sendingdata.writeDouble(ServerState.getPlayerState(player).energy); //energy
        sendingdata.writeDouble(ServerState.getPlayerState(player).totalStamina); //total stamina
        sendingdata.writeDouble(ServerState.getPlayerState(player).speedMultiplier); //speed multiplier

        ServerPlayNetworking.send(player, NetworkingPackets.SEND_PLAYERSTATE_S2C_PACKET_ID, sendingdata);

    }

}
