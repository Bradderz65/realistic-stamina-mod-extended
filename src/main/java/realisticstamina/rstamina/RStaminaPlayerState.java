package realisticstamina.rstamina;

public class RStaminaPlayerState {
    public double stamina = RStaminaMod.config.totalStamina;
    public double maxStamina = RStaminaMod.config.totalStamina;
    public double gainedStamina = 0.0;

    // Declare mining-related fields BEFORE totalStamina
    public int miningBlockProgress = 0;
    public double miningBonusStamina = 0.0;

    // Track if a custom total stamina value has been set
    public double customTotalStamina = -1.0; // -1 means no custom value is set

    // Replace field with getter method
    private double _cachedTotalStamina = RStaminaMod.config.totalStamina;
    
    public double getTotalStamina() {
        // If the player has been edited and a custom totalStamina is set, use that as base instead of config value
        if (edited && customTotalStamina >= 0) {
            _cachedTotalStamina = customTotalStamina + gainedStamina + miningBonusStamina;
        } else {
            _cachedTotalStamina = RStaminaMod.config.totalStamina + gainedStamina + miningBonusStamina;
        }
        return _cachedTotalStamina;
    }
    
    // For backward compatibility
    public double totalStamina = _cachedTotalStamina;

    public double energy = 100.0;
    public double usedEnergy = 0.0;
    public double energyFromResting = 0.0;
    public boolean edited = false;

    public int staminaRegenCooldown = 0;
    public int miningFatigueCooldown = 0;  // Cooldown before removing mining fatigue effect
    public int exhaustionRestTimer = 0;     // Timer for required rest when exhausted
    public boolean isExhausted = false;     // Whether player is in exhaustion state

    // Speed multiplier based on max stamina
    public double speedMultiplier = 1.0;

    //rates
    public double staminaLossRate = RStaminaMod.config.staminaLossRate;
    public double staminaGainRate = RStaminaMod.config.staminaGainRate;

    public double energyLossRate = RStaminaMod.config.energyLossRate;
    public double energyGainRate = RStaminaMod.config.restingEnergyGainTick;
    
    public double walkingStaminaLossRate = RStaminaMod.config.walkingStaminaLossRate;
    public double walkingEnergyLossRate = RStaminaMod.config.walkingEnergyLossRate;

    public boolean wasOnGround = true;
    
    // Position tracking for movement detection
    public double lastX = 0;
    public double lastZ = 0;
    public int positionCheckCooldown = 0;
    
    // Flag to track if player was walking in the last tick
    public boolean wasWalking = false;
    
    // Update max stamina based on current energy
    public void updateMaxStamina() {
        maxStamina = getTotalStamina() * (energy / 100.0);
    }
    
    // Set a custom total stamina value
    public void setCustomTotalStamina(double value) {
        customTotalStamina = value;
        totalStamina = value;
        edited = true;
    }
    
    // Reset to use config values
    public void resetToConfigDefaults() {
        customTotalStamina = -1.0;
        edited = false;
    }
}
