package realisticstamina.rstamina.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

        // Keep player rates in sync with current config every tick so GUI changes apply immediately
        playerstate.staminaLossRate = RStaminaMod.config.staminaLossRate;
        playerstate.staminaGainRate = RStaminaMod.config.staminaGainRate;
        playerstate.energyLossRate = RStaminaMod.config.energyLossRate;
        playerstate.energyGainRate = RStaminaMod.config.restingEnergyGainTick;
        playerstate.walkingStaminaLossRate = RStaminaMod.config.walkingStaminaLossRate;
        playerstate.walkingEnergyLossRate = RStaminaMod.config.walkingEnergyLossRate;

        // Handle cooldowns
        if (playerstate.staminaRegenCooldown > 0) {
            playerstate.staminaRegenCooldown -= 1;
        }

        if (playerstate.miningFatigueCooldown > 0) {
            playerstate.miningFatigueCooldown -= 1;
        }

        // Handle exhaustion rest timer
        if (playerstate.isExhausted) {
            boolean isResting = !player.isSprinting() && !player.isSwimming() && !player.isClimbing() && player.isOnGround();
            if (isResting) {
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
        boolean energyUpdated = false;

        // Helper function to update energy
        Runnable updateEnergy = () -> {
            if (RStaminaMod.config.enableEnergySystem) {
                playerstate.updateMaxStamina();
            }
        };

        // Initialize last position if unset
        if (playerstate.lastX == 0 && playerstate.lastZ == 0) {
            playerstate.lastX = player.getX();
            playerstate.lastZ = player.getZ();
        }

        // Check for jump (was on ground last tick, not on ground now, and moving upward)
        if (playerstate.wasOnGround && !player.isOnGround() && !player.isTouchingWater() 
            && !player.isCreative() && !player.isSpectator() 
            && player.getVelocity().y > 0) {  // Only count as jump if moving upward
            
            performedStaminaAction = true;
            playerstate.stamina -= RStaminaMod.config.jumpStaminaCost;
            
            if (RStaminaMod.config.enableEnergySystem) {
                playerstate.energy -= 0.03;
                playerstate.usedEnergy += 0.03;
                energyUpdated = true;
            }
            
            playerstate.staminaRegenCooldown = 20;
            serverState.markDirty();
        }
        playerstate.wasOnGround = player.isOnGround();

        // Handle sprinting
        if (player.isSprinting() && !player.isCreative() && !player.isSpectator()) {
            performedStaminaAction = true;
            playerstate.stamina -= playerstate.staminaLossRate;
            
            if (RStaminaMod.config.enableEnergySystem) {
                // Calculate speed-based energy drain if enabled
                double energyDrainRate = playerstate.energyLossRate;
                if (RStaminaMod.config.speedBasedEnergyDrain) {
                    // Get player velocity magnitude
                    double velocityX = player.getVelocity().x;
                    double velocityZ = player.getVelocity().z;
                    double velocityMagnitude = Math.sqrt(velocityX * velocityX + velocityZ * velocityZ);
                    
                    // Calculate drain multiplier based on speed
                    double speedMultiplier = calculateSpeedEnergyDrainMultiplier(player, playerstate, velocityMagnitude);
                    energyDrainRate *= speedMultiplier;
                }
                
                playerstate.energy -= energyDrainRate;
                playerstate.usedEnergy += energyDrainRate;
                energyUpdated = true;
            }
            
            serverState.markDirty();
        } 
        // Handle walking or standing
        else if (!player.isSprinting() && !player.isSwimming() && !player.isClimbing()) {
            // Movement detection using horizontal position delta (server-reliable)
            double dx = player.getX() - playerstate.lastX;
            double dz = player.getZ() - playerstate.lastZ;
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            boolean isMoving = horizontalDistance > 0.0015;
            
            // Set the walking flag
            playerstate.wasWalking = isMoving && player.isOnGround() && !player.isSprinting() && 
                                  !player.isSwimming() && !player.isClimbing() && !player.hasVehicle();
            
            // Handle walking stamina drain
            if (playerstate.wasWalking && RStaminaMod.config.enableWalkingStaminaDrain 
                && !player.isCreative() && !player.isSpectator()) {
                
                performedStaminaAction = true;
                playerstate.staminaRegenCooldown = 10;
                playerstate.stamina -= playerstate.walkingStaminaLossRate;
                
                if (RStaminaMod.config.enableEnergySystem) {
                    // Calculate speed-based energy drain if enabled
                    double energyDrainRate = playerstate.walkingEnergyLossRate;
                    if (RStaminaMod.config.speedBasedEnergyDrain) {
                        // Calculate drain multiplier based on distance moved
                        double speedMultiplier = calculateSpeedEnergyDrainMultiplier(player, playerstate, horizontalDistance);
                        energyDrainRate *= speedMultiplier;
                    }
                    
                    playerstate.energy -= energyDrainRate;
                    playerstate.usedEnergy += energyDrainRate;
                    energyUpdated = true;
                }
                
                serverState.markDirty();
            }
            
            // Apply stamina regeneration if conditions are met
            if (playerstate.staminaRegenCooldown <= 0 && !playerstate.isExhausted && 
                playerstate.stamina < playerstate.maxStamina && 
                (!playerstate.wasWalking || (playerstate.wasWalking && RStaminaMod.config.enableWalkingStaminaRegen && !RStaminaMod.config.enableWalkingStaminaDrain))) {
                
                double regenerationRate = playerstate.staminaGainRate;
                
                // Adjust regeneration rate for walking if applicable
                if (playerstate.wasWalking && RStaminaMod.config.enableWalkingStaminaRegen) {
                    double walkingMultiplier = Math.max(1.0, RStaminaMod.config.walkingStaminaRegenMultiplier);
                    regenerationRate /= walkingMultiplier;
                }
                
                // Boost regeneration when sneaking ONLY if not moving
                if (player.isSneaking() && !playerstate.wasWalking) {
                    regenerationRate = 0.21875; // Hardcoded value from original
                }
                
                playerstate.stamina += regenerationRate;
                if (playerstate.stamina > playerstate.maxStamina) {
                    playerstate.stamina = playerstate.maxStamina;
                }
                
                serverState.markDirty();
            }
        }

        // If energy was updated, update maxStamina
        if (energyUpdated) {
            updateEnergy.run();
        }

        // Check if player should enter exhaustion state
        if (isLowStamina && performedStaminaAction && !playerstate.isExhausted) {
            playerstate.isExhausted = true;
            playerstate.exhaustionRestTimer = RStaminaMod.config.exhaustionRecoveryTicks;
            if (RStaminaMod.config.showExhaustionWarning) {
                player.sendMessage(Text.literal("§c⚠ You're exhausted! Rest for a moment to recover."), true);
            }
        }

        // Apply status effects - consolidated logic
        applyStatusEffects(player, playerstate);

        // Ensure stamina stays within bounds
        if (playerstate.stamina > playerstate.maxStamina) {
            playerstate.stamina = playerstate.maxStamina;
        } else if (playerstate.stamina < 0) {
            playerstate.stamina = 0.0;
        }

        // Calculate speed multiplier based on max stamina (if enabled)
        updateSpeedMultiplier(player, playerstate);

        // Send updated state to client
        sendStateToClient(player, playerstate);

        // Update last known position for next tick
        playerstate.lastX = player.getX();
        playerstate.lastZ = player.getZ();
    }
    
    private static void applyStatusEffects(ServerPlayerEntity player, RStaminaPlayerState playerstate) {
        // Apply appropriate status effects based on stamina level
        if (playerstate.stamina <= 0.0) {
            // Severe effects at zero stamina
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 80, 5, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 80, 2, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 80, 2, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 80, 0, true, false));
            playerstate.miningFatigueCooldown = RStaminaMod.config.miningFatigueCooldownTicks;
        } else if (playerstate.stamina <= 5.0) {
            // Strong negative effects
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 4, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 60, 2, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, 1, true, false));
            playerstate.miningFatigueCooldown = RStaminaMod.config.miningFatigueCooldownTicks;
        } else if (playerstate.stamina <= 12.0) {
            // Moderate negative effects
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 40, 4, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 40, 0, true, false));
        } else if (playerstate.stamina <= 24.0) {
            // Mild negative effects
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 1, true, false));
        } else if (playerstate.stamina > 5.0 && playerstate.miningFatigueCooldown > 0) {
            // Keep some effects during cooldown period
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20, 1, true, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 1, true, false));
        }
    }
    
    private static void updateSpeedMultiplier(ServerPlayerEntity player, RStaminaPlayerState playerstate) {
        if (RStaminaMod.config.enableSpeedMultiplier) {
            double baseStamina = RStaminaMod.config.totalStamina;
            double maxStamina = RStaminaMod.config.fitnessStaminaLimit;
            double currentTotalStamina = playerstate.getTotalStamina();
            
            // Calculate a multiplier between 1.0 and maxSpeedMultiplier based on stamina
            double staminaProgress = Math.min(1.0, Math.max(0.0, (currentTotalStamina - baseStamina) / (maxStamina - baseStamina)));
            double speedMultiplier = 1.0 + (staminaProgress * (RStaminaMod.config.maxSpeedMultiplier - 1.0));
            
            // Update the speed multiplier if it has changed
            if (Math.abs(playerstate.speedMultiplier - speedMultiplier) > 0.01) {
                playerstate.speedMultiplier = speedMultiplier;
                // Send a debug message to the player only if the feature is enabled and multiplier > 1.0
                if (speedMultiplier > 1.01) {
                    player.sendMessage(Text.literal(String.format("§bSpeed multiplier: §f%.2fx", speedMultiplier)), true);
                }
            }
        }
    }
    
    private static void sendStateToClient(ServerPlayerEntity player, RStaminaPlayerState playerstate) {
        PacketByteBuf sendingdata = PacketByteBufs.create();
        sendingdata.writeDouble(playerstate.stamina);
        sendingdata.writeDouble(playerstate.maxStamina);
        sendingdata.writeDouble(playerstate.energy);
        sendingdata.writeDouble(playerstate.totalStamina);
        sendingdata.writeDouble(playerstate.speedMultiplier);

        ServerPlayNetworking.send(player, NetworkingPackets.SEND_PLAYERSTATE_S2C_PACKET_ID, sendingdata);
    }

    /**
     * Calculates the energy drain multiplier based on the player's movement speed.
     * Takes into account both the player's velocity and their speed multiplier from fitness.
     *
     * @param player The player entity
     * @param playerstate The player's stamina state
     * @param velocityMagnitude The magnitude of the player's velocity
     * @return A multiplier to apply to the base energy drain rate
     */
    private static double calculateSpeedEnergyDrainMultiplier(ServerPlayerEntity player, RStaminaPlayerState playerstate, double velocityMagnitude) {
        // Base multiplier starts at 1.0
        double multiplier = 1.0;
        
        // Only apply speed-based drain if the feature is enabled
        if (RStaminaMod.config.speedBasedEnergyDrain) {
            // Factor 1: Speed multiplier from fitness affects energy drain
            // Players with higher fitness move faster, but also drain energy faster
            if (RStaminaMod.config.enableSpeedMultiplier && playerstate.speedMultiplier > 1.0) {
                double speedFactor = Math.pow(playerstate.speedMultiplier, RStaminaMod.config.speedMultiplierEnergyDrainFactor);
                multiplier *= speedFactor;
            }
            
            // Factor 2: Actual movement velocity
            // Faster movement (higher velocity) drains more energy
            // Normalize velocity - typical walking speed is around 0.1, sprinting around 0.13-0.2
            double normalizedVelocity = Math.min(1.0, velocityMagnitude * 5.0);
            if (normalizedVelocity > 0.1) {
                // Apply non-linear scaling for higher speeds
                double velocityFactor = 1.0 + (normalizedVelocity - 0.1) * 10.0 
                    * ((RStaminaMod.config.maxSpeedEnergyDrainMultiplier - 1.0) / 0.9);
                
                // Cap the multiplier at the config maximum
                velocityFactor = Math.min(velocityFactor, RStaminaMod.config.maxSpeedEnergyDrainMultiplier);
                
                multiplier *= velocityFactor;
            }
        }
        
        return multiplier;
    }
}
