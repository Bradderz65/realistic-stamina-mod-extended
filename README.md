# Realistic Stamina Mod

### License
This mod is available under the MIT license.

### Dependencies for Latest Version
[Fabric Api](https://modrinth.com/mod/fabric-api/version/0.83.1+1.20.1) >=0.83.1 for mc 1.20.1

[CompleteConfig](https://modrinth.com/mod/completeconfig/version/2.4.0) >=2.4.0 for mc 1.20.1

[Roughly Enough Items](https://modrinth.com/mod/rei/version/12.0.625+fabric) (Recommended)

Run "/resetStaminaStats @s" when updating to new version.

# Features
This mod adds a stamina and energy system to make getting around more interesting and challenging. You can no longer just sprint infinitely and have to pay attention to your stamina and energy. Its a good thing if you want to make the game more of a challenge while not changing it too much.

### Stamina System
The Stamina meter has a default max of 64 and will run out as you sprint. when it turns yellow you will be a bit slower, when it turns orange you will be a lot slower and when it reaches zero you will barely be able to move. It will regenerate if you stop sprinting but will not regenerate if you are in water or climbing.

### Energy System
Your energy is a percentage of your maximum stamina. You will slowly lose energy the more stamina you use.
Your energy will reset to 100% when you exit a bed so you do not have to skip the night to regenerate your energy. You can also slowly regenerate energy during the day by mounting something like a boat or a horse but  you can only regenerate up to 5% this way (Configurable), then you have to sleep.
You can drink an energy drink to regenerate 5% of energy.

### Fitness System
When enabled, upon sleeping players whose energy is less than 85% will gain a default of 0.25 total stamina.
And players whose energy is greater than 92% will lose a default of 0.25 total stamina IF they already have more than the default total.
By default, players can reach up to 128.0 stamina points with this system.

### Commands
- setTotalStamina - sets a players total stamina.
- resetStaminaStats - resets all a players stamina stats.
- setStaminaLossRate - sets a players stamina loss rate per tick.
- setStaminaGainRate - sets a players stamina gain rate per tick.
- setEnergyLossRate - sets a players energy loss rate per tick
- setEnergyGainRate - sets a players energy gain rate per tick.

### Config
Config file path is "config/rstamina.conf".
