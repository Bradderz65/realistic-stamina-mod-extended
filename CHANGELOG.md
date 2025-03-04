# Changelog

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