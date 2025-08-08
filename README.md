# Realistic Stamina - Extended

This is an enhanced version of the [Realistic Stamina mod](https://modrinth.com/mod/realistic-stamina) originally created by sparkierkan7. This modified version includes additional features and improvements while maintaining the core functionality of the original mod.

## New Features & Improvements

This extended version adds the following features to the original mod:
- Configurable jump stamina cost
- Configurable mining fatigue cooldown
- Improved jump detection logic
- Added mining fatigue effect
- Speed multiplier system that links max stamina to sprint speed
- Mining stamina gain system (gain max stamina by mining blocks)
- Modern, minimal UI for stamina and energy display
- Thread safety improvements for multiplayer stability
- Various optimizations and improvements

### Speed Multiplier System
- Players with higher max stamina can sprint faster (up to 2x speed at max stamina)
- Speed increases gradually as max stamina increases through the fitness system
- Use the `/showspeed` command to see your current speed multiplier for 30 seconds
- Can be disabled in the configuration if desired

### Mining Stamina Gain System
- Gain permanent max stamina by mining blocks
- Progress tracked per player
- Configurable number of blocks required and amount of stamina gained
- Can be disabled in the configuration

### Modern UI
- Clean, minimal progress bars for stamina and energy.
- **Two HUD Styles:**
    - `DETAILED`: Shows bars with numeric values (e.g., "50/100") displayed next to them.
    - `MINIMAL`: Shows only the bars for a less intrusive display.
- **Scalable HUD:** The overall size of the HUD can be adjusted using the `hudScale` config option.
- Bars are color-coded based on current levels (stamina: green/yellow/red, energy: blue).
- Speed multiplier is displayed below the bars when active (e.g., via `/showspeed` command).
- Smooth animations when values change.

### Multiplayer Improvements
- Thread-safe implementation for better stability in multiplayer
- Fixed concurrent modification exceptions
- Better null safety and error handling

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
- Mining stamina gain settings:
  - `enableMiningStaminaGain`: Enable/disable mining stamina gain (default: false)
  - `miningBlocksPerStaminaGain`: Number of blocks to mine for stamina gain (default: 100)
  - `miningStaminaGainAmount`: Amount of max stamina gained (default: 1.0)
- HUD settings:
  - `hudX`: X coordinate of stamina and energy HUD (default: 10)
  - `hudY`: Y coordinate of stamina and energy HUD (default: 25)
  - `hudStyle`: Style of the HUD (`DETAILED` or `MINIMAL`, default: `DETAILED`)
  - `hudScale`: Overall scale of the HUD (double, default: 1.0)

## Commands
- `/showspeed`: Shows your current speed multiplier for 30 seconds (available to all players)
- `/setTotalStamina <value> <player>`: Sets a player's total stamina (requires op)
- `/resetStaminaStats <player>`: Resets a player's stamina stats (requires op)
- `/setStaminaLossRate <value> <player>`: Sets a player's stamina loss rate (requires op)
- `/setStaminaGainRate <value> <player>`: Sets a player's stamina gain rate (requires op)

## Links
- [Original Mod on Modrinth](https://modrinth.com/mod/realistic-stamina)
- [Modified Version Source Code](https://github.com/Bradderz65/realistic-stamina-mod-extended)
