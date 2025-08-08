# Changelog

## Version 1.6.0 (2025-08-09)

### New Features
- **In-Game Configuration:** Added a complete in-game configuration system to adjust mod settings without editing config files.

### Fixes
- **Stamina Regeneration:** Fixed slow stamina regeneration when walking.

## Version 1.5.0

### New Features
- **HUD Style Configuration:** Added a new option (`hudStyle`) to choose between `DETAILED` (bars and text values) and `MINIMAL` (bars only) HUD.
- **HUD Scale Configuration:** Added a new option (`hudScale`) to adjust the overall size of the stamina and energy HUD.

### Improvements & Refactoring
- **HUD Rendering:**
    - Bars now have sharp corners instead of rounded ones.
    - Text values are now displayed next to the bars (if `DETAILED` HUD style is selected) instead of inside them.
    - Removed mouse hover functionality for showing detailed values; details are now part of the `DETAILED` style.
    - Speed multiplier text is now scaled and centered below the bars.
- **Client-Side Logic:**
    - Refactored client tick handling in `RStaminaClient.java` for better organization and added null checks to prevent potential errors.
- **Command Registration:**
    - Refactored server-side command registration in `RStaminaMod.java` using a helper method for conciseness, affecting commands like `setStaminaLossRate`, `setStaminaGainRate`, etc.
- **Minor Code Optimizations:**
    - Added an early exit in the block-breaking efficiency enchantment check.
    - Minor refactor in `RStaminaPlayerState.updateMaxStamina()`.

### Configuration Updates
- Added `hudStyle` (enum: `DETAILED`, `MINIMAL`, default: `DETAILED`) to `RStaminaConfig.java`.
- Added `hudScale` (double, default: `1.0`) to `RStaminaConfig.java`.

## Version 1.4.4.0

### New Features
- Enhanced speed-based energy drain system
  - Energy drain now scales with movement speed
  - Configurable maximum drain multiplier
  - Speed multiplier affects energy consumption
- Improved walking stamina mechanics
  - Added walking stamina regeneration options
  - Configurable regeneration rates while walking
  - Better balance between movement and regeneration
- Added configurable mining fatigue cooldown
- Added detailed configuration options for all systems

### Improvements
- Reorganized configuration file with clear categories
- Enhanced stamina management system
- Added comprehensive comments to all configuration options
- Improved performance and stability
- Fixed compatibility issues with latest Minecraft version
- Performed clean build and code optimization

### Configuration Updates
- Added speed-based energy drain settings:
  - `speedBasedEnergyDrain` (default: true)
  - `maxSpeedEnergyDrainMultiplier` (default: 2.0)
  - `speedMultiplierEnergyDrainFactor` (default: 2.0)
- Added walking regeneration settings:
  - `enableWalkingStaminaRegen` (default: true)
  - `walkingStaminaRegenMultiplier` (default: 3.0)
- Enhanced fitness system configuration
- Improved exhaustion system settings

## Version 1.4.3.0

### New Features
- Added walking stamina drain system
  - Players now lose stamina and energy while walking
  - Rate of drain is configurable and can be disabled
  - Walking drain is less intense than sprint drain

### Changes
- Added new configuration option `enableWalkingStaminaDrain` (default: true)
- Added new configuration option `walkingStaminaLossRate` (default: 0.05)
- Added new configuration option `walkingEnergyLossRate` (default: 0.001)

## Version 1.4.2.0

### New Features
- Added speed multiplier system that links maximum stamina to sprint speed
  - Players with higher max stamina can sprint faster (up to 2x speed at max stamina)
  - Speed increases gradually as max stamina increases through the fitness system
  - Added configuration option to enable/disable the speed multiplier feature
  - Added `/showspeed` command that any player can use to see their current speed multiplier for 30 seconds

### Changes
- Added new configuration option `enableSpeedMultiplier` (default: true)
- Added new configuration option `maxSpeedMultiplier` (default: 2.0)
- Added HUD display for current speed multiplier when using the `/showspeed` command
- Speed multiplier is calculated based on max stamina progress between base stamina and fitness limit

### Technical Changes
- Added PlayerEntityMixin to modify player movement speed
- Added ShowSpeedC2SPacket for handling the showspeed command
- Updated networking system to transmit speed multiplier information
- Modified HUD overlay to display speed multiplier information

## Previous Versions

### Version 1.4.1.3
- Original version with stamina and energy systems
- Added mining fatigue and exhaustion features 