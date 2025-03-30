# Realistic Stamina - Extended

This is an enhanced version of the [Realistic Stamina mod](https://modrinth.com/mod/realistic-stamina) originally created by sparkierkan7. This modified version includes additional features and improvements while maintaining the core functionality of the original mod.

## New Features & Improvements

This extended version adds the following features to the original mod:
- Configurable jump stamina cost
- Configurable mining fatigue cooldown
- Improved jump detection logic
- Added mining fatigue effect
- Speed multiplier system that links max stamina to sprint speed
- Various optimizations and improvements

### Speed Multiplier System
- Players with higher max stamina can sprint faster (up to 2x speed at max stamina)
- Speed increases gradually as max stamina increases through the fitness system
- Use the `/showspeed` command to see your current speed multiplier for 30 seconds
- Can be disabled in the configuration if desired

## Credits
- Original mod by [sparkierkan7](https://modrinth.com/user/sparkierkan7)
- Original mod source: [Realistic Stamina](https://modrinth.com/mod/realistic-stamina)
- Modified version by Bradderz65

## Requirements
- Minecraft 1.20.1
- Fabric Loader >=0.14.21
- Fabric API
- CompleteConfig >=2.4.0

## License
This modification is distributed under the MIT License, maintaining the same license as the original mod.

## Installation
1. Install Fabric for Minecraft 1.20.1
2. Install Fabric API
3. Install CompleteConfig
4. Download and place the mod jar file in your mods folder

## Configuration
The mod includes all original configuration options plus new settings for:
- Jump stamina cost
- Mining fatigue cooldown time
- Mining fatigue effect settings
- Speed multiplier settings:
  - `maxSpeedMultiplier`: Maximum speed multiplier at max stamina (default: 2.0)
  - `enableSpeedMultiplier`: Enable/disable the speed multiplier feature (default: true)

## Commands
- `/showspeed`: Shows your current speed multiplier for 30 seconds (available to all players)
- `/setTotalStamina <value> <player>`: Sets a player's total stamina (requires op)
- `/resetStaminaStats <player>`: Resets a player's stamina stats (requires op)
- `/setStaminaLossRate <value> <player>`: Sets a player's stamina loss rate (requires op)
- `/setStaminaGainRate <value> <player>`: Sets a player's stamina gain rate (requires op)

## Links
- [Original Mod on Modrinth](https://modrinth.com/mod/realistic-stamina)
- [Modified Version Source Code](https://github.com/Bradderz65/realistic-stamina-mod)
