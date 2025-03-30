package realisticstamina.rstamina.mixin;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import realisticstamina.rstamina.RStaminaMod;
import realisticstamina.rstamina.RStaminaPlayerState;
import realisticstamina.rstamina.ServerState;
import realisticstamina.rstamina.RStaminaClient;
import net.minecraft.client.MinecraftClient;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    
    /**
     * Modifies the player's movement speed based on their max stamina.
     * Higher max stamina results in faster sprint speed.
     */
    @Inject(method = "getMovementSpeed", at = @At("RETURN"), cancellable = true)
    private void getMovementSpeed(CallbackInfoReturnable<Float> cir) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        
        // Only apply speed boost when sprinting, in survival mode, and the feature is enabled
        if (player.isSprinting() && !player.isCreative() && !player.isSpectator() && RStaminaMod.config.enableSpeedMultiplier) {
            double speedMultiplier = 1.0;
            
            // Check if we're on server or client
            if (player.getWorld().isClient()) {
                // On client side, use the client-stored speed multiplier
                speedMultiplier = RStaminaClient.clientStoredSpeedMultiplier;
            } else {
                // On server side, calculate the speed multiplier
                // Get the player's state
                RStaminaPlayerState playerState = ServerState.getPlayerState(player);
                
                // Calculate speed multiplier based on max stamina
                // Default stamina is 64, max is 128 (with fitness system)
                double baseStamina = RStaminaMod.config.totalStamina;
                double maxStamina = RStaminaMod.config.fitnessStaminaLimit;
                double currentTotalStamina = playerState.totalStamina;
                
                // Calculate a multiplier between 1.0 and maxSpeedMultiplier based on stamina
                double staminaProgress = Math.min(1.0, Math.max(0.0, (currentTotalStamina - baseStamina) / (maxStamina - baseStamina)));
                speedMultiplier = 1.0 + (staminaProgress * (RStaminaMod.config.maxSpeedMultiplier - 1.0));
                
                // Store the calculated multiplier in player state
                playerState.speedMultiplier = speedMultiplier;
            }
            
            // Apply the speed multiplier
            float originalSpeed = cir.getReturnValue();
            cir.setReturnValue(originalSpeed * (float)speedMultiplier);
        }
    }
} 