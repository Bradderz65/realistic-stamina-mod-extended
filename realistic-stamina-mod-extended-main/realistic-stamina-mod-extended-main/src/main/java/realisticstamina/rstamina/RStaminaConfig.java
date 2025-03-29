package realisticstamina.rstamina;

import me.lortseam.completeconfig.api.ConfigEntry;
import me.lortseam.completeconfig.data.Config;
import me.lortseam.completeconfig.data.ConfigOptions;

public class RStaminaConfig extends Config {

    // ===== GENERAL STAMINA SETTINGS =====
    @ConfigEntry(comment = "Maximum base stamina of players. (Default: 64.0)")
    public double totalStamina = 64.0;
    
    @ConfigEntry(comment = "Maximum stamina players can reach with the fitness system. (Default: 128.0)")
    public double fitnessStaminaLimit = 128.0;
    
    // ===== STAMINA REGENERATION SETTINGS =====
    @ConfigEntry(comment = "Amount of stamina you gain per tick while standing still. (Default: 0.1875)")
    public double staminaGainRate = 0.1875;
    
    @ConfigEntry(comment = "Whether to enable stamina regeneration while walking. Only applies if walking stamina drain is disabled. (Default: true)")
    public boolean enableWalkingStaminaRegen = true;
    
    @ConfigEntry(comment = "How much slower stamina regenerates while walking compared to standing still. Higher values = slower regeneration. (Default: 3.0)")
    public double walkingStaminaRegenMultiplier = 3.0;
    
    // ===== STAMINA DRAIN SETTINGS =====
    @ConfigEntry(comment = "Amount of stamina you lose per tick while running. (Default: 0.25)")
    public double staminaLossRate = 0.25;
    
    @ConfigEntry(comment = "Whether to enable stamina drain while walking. (Default: true)")
    public boolean enableWalkingStaminaDrain = true;
    
    @ConfigEntry(comment = "Amount of stamina you lose per tick while walking. (Default: 0.1)")
    public double walkingStaminaLossRate = 0.1;
    
    @ConfigEntry(comment = "Amount of stamina you lose per tick while swimming. (Default: 0.05)")
    public double swimmingStaminaCost = 0.05;
    
    @ConfigEntry(comment = "Amount of stamina you lose per tick while climbing. (Default: 0.04)")
    public double climbingStaminaCost = 0.04;
    
    @ConfigEntry(comment = "Amount of stamina you lose when jumping. (Default: 3.0)")
    public double jumpStaminaCost = 3.0;
    
    @ConfigEntry(comment = "Amount of stamina you lose when breaking solid blocks. Only applies if breakingBlocksUsesStamina is enabled. (Default: 2.0)")
    public double blockBreakStaminaCost = 2.0;
    
    @ConfigEntry(comment = "Whether or not block breaking uses your stamina. Using a tool with efficiency will still stop stamina from being used. (Default: true)")
    public boolean breakingBlocksUsesStamina = true;
    
    // ===== MINING STAMINA GAIN SETTINGS =====
    @ConfigEntry(comment = "Whether to enable max stamina gain from mining blocks. (Default: true)")
    public boolean enableMiningStaminaGain = true;
    
    @ConfigEntry(comment = "Number of blocks needed to mine to gain max stamina. (Default: 100)")
    public int blocksToMineForStaminaGain = 100;
    
    @ConfigEntry(comment = "Amount of max stamina gained after mining the required number of blocks. (Default: 5.0)")
    public double miningStaminaGainAmount = 5.0;
    
    // ===== ENERGY SYSTEM SETTINGS =====
    @ConfigEntry(comment = "Enables or disables the energy system. Energy affects maximum stamina and fitness. (Default: true)")
    public boolean enableEnergySystem = true;
    
    @ConfigEntry(comment = "Base amount of energy you lose per tick while running. May be modified by speed if speedBasedEnergyDrain is enabled. (Default: 0.004)")
    public double energyLossRate = 0.004;
    
    @ConfigEntry(comment = "Base amount of energy you lose per tick while walking. May be modified by speed if speedBasedEnergyDrain is enabled. (Default: 0.001)")
    public double walkingEnergyLossRate = 0.001;
    
    @ConfigEntry(comment = "Whether energy drain should scale with movement speed. When enabled, moving faster will drain more energy. (Default: true)")
    public boolean speedBasedEnergyDrain = true;
    
    @ConfigEntry(comment = "Maximum multiplier for energy drain based on speed. Higher values mean faster movement drains more energy. (Default: 2.0)")
    public double maxSpeedEnergyDrainMultiplier = 2.0;
    
    @ConfigEntry(comment = "How much the player's speed multiplier affects energy drain. Higher values make players with higher stamina drain energy faster. (Default: 2.0)")
    public double speedMultiplierEnergyDrainFactor = 2.0;
    
    // ===== RESTING SETTINGS =====
    @ConfigEntry(comment = "If enabled you can gain energy by sitting on things. (Default: true)")
    public boolean enableResting = true;
    
    @ConfigEntry(comment = "Energy gained every tick of resting. (Default: 0.002)")
    public double restingEnergyGainTick = 0.002;
    
    @ConfigEntry(comment = "Whether or not you rest when riding a horse, donkey or mule. (Default: true)")
    public boolean restRidingHorse = true;
    
    @ConfigEntry(comment = "Whether or not you rest while moving your boat. (Default: false)")
    public boolean restWhileBoatMoving = false;
    
    @ConfigEntry(comment = "Maximum energy that you can gain from resting. Resets when you sleep. (Default: 5.0)")
    public double maxRestingEnergyGain = 5.0;
    
    // ===== FITNESS SYSTEM SETTINGS =====
    @ConfigEntry(comment = "When enabled, sleeping affects your maximum stamina based on energy usage. (Default: true)")
    public boolean fitnessSystem = true;
    
    @ConfigEntry(comment = "Amount of stamina players gain or lose when the fitness system is enabled. (Default: 0.25)")
    public double fitnessStaminaChange = 0.25;
    
    @ConfigEntry(comment = "Upon sleeping if you have used less energy than this you will lose max stamina. (Default: 8.0)")
    public double fitnessUsedEnergyToKeep = 8.0;
    
    @ConfigEntry(comment = "Upon sleeping if you have used more energy than this you will gain max stamina. (Default: 15.0)")
    public double fitnessUsedEnergyToGain = 15.0;
    
    // ===== EXHAUSTION SETTINGS =====
    @ConfigEntry(comment = "Stamina threshold below which exhaustion can occur. (Default: 5.0)")
    public double exhaustionThreshold = 5.0;
    
    @ConfigEntry(comment = "How long (in ticks) player must rest to recover from exhaustion. 20 ticks = 1 second. (Default: 60)")
    public int exhaustionRecoveryTicks = 60;
    
    @ConfigEntry(comment = "Whether to show a warning message when player becomes exhausted. (Default: true)")
    public boolean showExhaustionWarning = true;
    
    @ConfigEntry(comment = "How long (in ticks) mining fatigue persists after stamina recovers above threshold. 20 ticks = 1 second. (Default: 100)")
    public int miningFatigueCooldownTicks = 100;
    
    // ===== SPEED MULTIPLIER SETTINGS =====
    @ConfigEntry(comment = "Enable or disable the speed multiplier feature. When enabled, higher max stamina gives faster sprint speed. (Default: true)")
    public boolean enableSpeedMultiplier = true;
    
    @ConfigEntry(comment = "Maximum speed multiplier at max stamina level. (Default: 2.0)")
    public double maxSpeedMultiplier = 2.0;
    
    // ===== HUD SETTINGS =====
    @ConfigEntry(comment = "X coordinate of stamina and energy HUD. 0 is farthest left. (Default: 10)")
    public int hudX = 10;
    
    @ConfigEntry(comment = "Y coordinate of stamina and energy HUD. 0 is top of the screen. (Default: 25)")
    public int hudY = 25;

    public RStaminaConfig() {
        super(ConfigOptions.mod(RStaminaMod.modid));
    }
}
