# Changelog

## Version 1.4.4.0

### Changes
- Performed clean build and code optimization
- Fixed compatibility issues with latest Minecraft version
- Improved performance and stability
- Added speed-based energy drain system
- Enhanced walking stamina regeneration

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