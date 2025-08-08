package realisticstamina.rstamina;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

@Environment(EnvType.CLIENT)
public class RStaminaModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (Screen parent) -> {
            // Use pre-snapshotted defaults captured at mod init time
            double defStaminaGainRate = RStaminaMod.Defaults.staminaGainRate;
            double defStaminaLossRate = RStaminaMod.Defaults.staminaLossRate;
            double defJumpCost = RStaminaMod.Defaults.jumpStaminaCost;

            boolean defEnableWalkDrain = RStaminaMod.Defaults.enableWalkingStaminaDrain;
            double defWalkDrainRate = RStaminaMod.Defaults.walkingStaminaLossRate;
            boolean defEnableWalkRegen = RStaminaMod.Defaults.enableWalkingStaminaRegen;
            double defWalkRegenMult = RStaminaMod.Defaults.walkingStaminaRegenMultiplier;

            boolean defEnableEnergy = RStaminaMod.Defaults.enableEnergySystem;
            boolean defSpeedBasedEnergy = RStaminaMod.Defaults.speedBasedEnergyDrain;
            double defRunEnergyDrain = RStaminaMod.Defaults.energyLossRate;
            double defWalkEnergyDrain = RStaminaMod.Defaults.walkingEnergyLossRate;

            double defExhaustThreshold = RStaminaMod.Defaults.exhaustionThreshold;
            int defExhaustRecovery = RStaminaMod.Defaults.exhaustionRecoveryTicks;
            boolean defShowExhaustWarn = RStaminaMod.Defaults.showExhaustionWarning;

            boolean defEnableSpeedMult = RStaminaMod.Defaults.enableSpeedMultiplier;
            double defMaxSpeedMult = RStaminaMod.Defaults.maxSpeedMultiplier;

            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Realistic Stamina - Config"));

            builder.setSavingRunnable(() -> {
                try {
                    // Persist config
                    RStaminaMod.config.save();
                } catch (Throwable ignored) {
                }
            });

            ConfigEntryBuilder eb = builder.entryBuilder();

            ConfigCategory staminaCat = builder.getOrCreateCategory(Text.literal("Stamina"));
            staminaCat.addEntry(eb.startDoubleField(Text.literal("Standing Regen Rate"), RStaminaMod.config.staminaGainRate)
                .setMin(0.0)
                .setDefaultValue(defStaminaGainRate)
                .setSaveConsumer(v -> RStaminaMod.config.staminaGainRate = v)
                .build());
            staminaCat.addEntry(eb.startDoubleField(Text.literal("Sprint Drain Rate"), RStaminaMod.config.staminaLossRate)
                .setMin(0.0)
                .setDefaultValue(defStaminaLossRate)
                .setSaveConsumer(v -> RStaminaMod.config.staminaLossRate = v)
                .build());
            staminaCat.addEntry(eb.startDoubleField(Text.literal("Jump Cost"), RStaminaMod.config.jumpStaminaCost)
                .setMin(0.0)
                .setDefaultValue(defJumpCost)
                .setSaveConsumer(v -> RStaminaMod.config.jumpStaminaCost = v)
                .build());

            ConfigCategory walkingCat = builder.getOrCreateCategory(Text.literal("Walking"));
            walkingCat.addEntry(eb.startBooleanToggle(Text.literal("Enable Walking Drain"), RStaminaMod.config.enableWalkingStaminaDrain)
                .setDefaultValue(defEnableWalkDrain)
                .setSaveConsumer(v -> RStaminaMod.config.enableWalkingStaminaDrain = v)
                .build());
            walkingCat.addEntry(eb.startDoubleField(Text.literal("Walking Drain Rate"), RStaminaMod.config.walkingStaminaLossRate)
                .setMin(0.0)
                .setDefaultValue(defWalkDrainRate)
                .setSaveConsumer(v -> RStaminaMod.config.walkingStaminaLossRate = v)
                .build());
            walkingCat.addEntry(eb.startBooleanToggle(Text.literal("Enable Walking Regen"), RStaminaMod.config.enableWalkingStaminaRegen)
                .setDefaultValue(defEnableWalkRegen)
                .setSaveConsumer(v -> RStaminaMod.config.enableWalkingStaminaRegen = v)
                .build());
            walkingCat.addEntry(eb.startDoubleField(Text.literal("Walking Regen Multiplier"), RStaminaMod.config.walkingStaminaRegenMultiplier)
                .setMin(1.0)
                .setDefaultValue(defWalkRegenMult)
                .setSaveConsumer(v -> RStaminaMod.config.walkingStaminaRegenMultiplier = v)
                .build());

            ConfigCategory energyCat = builder.getOrCreateCategory(Text.literal("Energy"));
            energyCat.addEntry(eb.startBooleanToggle(Text.literal("Enable Energy System"), RStaminaMod.config.enableEnergySystem)
                .setDefaultValue(defEnableEnergy)
                .setSaveConsumer(v -> RStaminaMod.config.enableEnergySystem = v)
                .build());
            energyCat.addEntry(eb.startBooleanToggle(Text.literal("Speed-Based Energy Drain"), RStaminaMod.config.speedBasedEnergyDrain)
                .setDefaultValue(defSpeedBasedEnergy)
                .setSaveConsumer(v -> RStaminaMod.config.speedBasedEnergyDrain = v)
                .build());
            energyCat.addEntry(eb.startDoubleField(Text.literal("Run Energy Drain"), RStaminaMod.config.energyLossRate)
                .setMin(0.0)
                .setDefaultValue(defRunEnergyDrain)
                .setSaveConsumer(v -> RStaminaMod.config.energyLossRate = v)
                .build());
            energyCat.addEntry(eb.startDoubleField(Text.literal("Walk Energy Drain"), RStaminaMod.config.walkingEnergyLossRate)
                .setMin(0.0)
                .setDefaultValue(defWalkEnergyDrain)
                .setSaveConsumer(v -> RStaminaMod.config.walkingEnergyLossRate = v)
                .build());

            ConfigCategory exhaustionCat = builder.getOrCreateCategory(Text.literal("Exhaustion"));
            exhaustionCat.addEntry(eb.startDoubleField(Text.literal("Exhaustion Threshold"), RStaminaMod.config.exhaustionThreshold)
                .setMin(0.0)
                .setDefaultValue(defExhaustThreshold)
                .setSaveConsumer(v -> RStaminaMod.config.exhaustionThreshold = v)
                .build());
            exhaustionCat.addEntry(eb.startIntField(Text.literal("Recovery Ticks"), RStaminaMod.config.exhaustionRecoveryTicks)
                .setMin(0)
                .setDefaultValue(defExhaustRecovery)
                .setSaveConsumer(v -> RStaminaMod.config.exhaustionRecoveryTicks = v)
                .build());
            exhaustionCat.addEntry(eb.startBooleanToggle(Text.literal("Show Warning"), RStaminaMod.config.showExhaustionWarning)
                .setDefaultValue(defShowExhaustWarn)
                .setSaveConsumer(v -> RStaminaMod.config.showExhaustionWarning = v)
                .build());

            ConfigCategory speedCat = builder.getOrCreateCategory(Text.literal("Speed Multiplier"));
            speedCat.addEntry(eb.startBooleanToggle(Text.literal("Enable Speed Multiplier"), RStaminaMod.config.enableSpeedMultiplier)
                .setDefaultValue(defEnableSpeedMult)
                .setSaveConsumer(v -> RStaminaMod.config.enableSpeedMultiplier = v)
                .build());
            speedCat.addEntry(eb.startDoubleField(Text.literal("Max Speed Multiplier"), RStaminaMod.config.maxSpeedMultiplier)
                .setMin(1.0)
                .setDefaultValue(defMaxSpeedMult)
                .setSaveConsumer(v -> RStaminaMod.config.maxSpeedMultiplier = v)
                .build());

            return builder.build();
        };
    }
} 