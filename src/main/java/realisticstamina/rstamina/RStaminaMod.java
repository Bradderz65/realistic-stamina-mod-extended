package realisticstamina.rstamina;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.lortseam.completeconfig.data.ConfigOptions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import realisticstamina.rstamina.item.EnergyDrinkItem;
import realisticstamina.rstamina.item.TestItem;
import realisticstamina.rstamina.networking.NetworkingPackets;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class RStaminaMod implements ModInitializer {

	public static final String rStaminaModVersion = "1.4.4.1";
	public static final String modid = "rstamina";
	public static final Logger LOGGER = LoggerFactory.getLogger(modid);

	public static final RStaminaConfig config = new RStaminaConfig();

	//items
	public static final TestItem TEST_ITEM = new TestItem(new FabricItemSettings());
	public static final EnergyDrinkItem ENERGY_DRINK_ITEM = new EnergyDrinkItem(new FabricItemSettings().maxCount(16));


	@Override
	public void onInitialize() {

		//config
		config.load();
		ConfigOptions.mod(modid).branch(new String[]{"branch", "config"});

		//items
		Registry.register(Registries.ITEM, new Identifier("rstamina", "test_item"), TEST_ITEM);
		Registry.register(Registries.ITEM, new Identifier("rstamina", "energy_drink"), ENERGY_DRINK_ITEM);

		//item groups
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
			content.add(ENERGY_DRINK_ITEM);
		});

		//networking
		NetworkingPackets.registerC2SPackets();

		//events
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

			ServerState serverState = ServerState.getServerState(handler.player.getWorld().getServer());
			RStaminaPlayerState playerState = ServerState.getPlayerState(handler.player);

			// Check and update mod version
			if (!Objects.equals(serverState.worldVersion, rStaminaModVersion)) {
				serverState.worldVersion = rStaminaModVersion;
				serverState.markDirty();
			}

			// If player state hasn't been customized, use config defaults
			if (!playerState.edited) {
				// Use helper method to sync player state values with config
				syncPlayerStateWithConfig(playerState);
				
				// Update calculated values
				playerState.updateMaxStamina();
				playerState.staminaRegenCooldown = 0;
				
				// Mark as dirty to save changes
				serverState.markDirty();
			}
		});

		PlayerBlockBreakEvents.BEFORE.register((world, player, blockPos, state, be) -> {

			ServerState serverState = ServerState.getServerState(player.getWorld().getServer());
			RStaminaPlayerState playerState = ServerState.getPlayerState(player);
			ItemStack mainStack = player.getMainHandStack();
			boolean hasEfficiency = false;

			for (int i = 0; i < mainStack.getEnchantments().size(); i++) {
				if (Objects.equals(mainStack.getEnchantments().getCompound(i).getString("id"), "minecraft:efficiency")) {
					hasEfficiency = true;
				}
			}
			if (!player.isCreative() && !hasEfficiency && config.breakingBlocksUsesStamina) {
				if (world.getBlockState(blockPos).isSolid()) {
					playerState.stamina -= config.blockBreakStaminaCost;
					if (RStaminaMod.config.enableEnergySystem) {
						playerState.energy -= 0.03;
						playerState.usedEnergy += 0.03;
						playerState.updateMaxStamina();
					}
					playerState.staminaRegenCooldown = 20;
					serverState.markDirty();
				}
			}

			return true;
		});

		// Register listener for AFTER block breaking (for mining stamina gain)
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			// Check if the feature is enabled in config and player is in survival/adventure
			if (config.enableMiningStaminaGain && !player.isCreative() && !player.isSpectator()) {
				// Ensure this runs on the server side
				if (!world.isClient()) {
					ServerState serverState = ServerState.getServerState(player.getServer());
					RStaminaPlayerState playerState = ServerState.getPlayerState(player);

					// Increment block break progress
					playerState.miningBlockProgress++;

					// Check if progress meets the threshold
					if (playerState.miningBlockProgress >= config.miningBlocksPerStaminaGain) {
						// Grant bonus stamina
						playerState.miningBonusStamina += config.miningStaminaGainAmount;
						
						// Reset progress
						playerState.miningBlockProgress = 0;

						// Recalculate total and max stamina immediately
						playerState.updateMaxStamina();
						
						// Mark state as dirty to save changes
						serverState.markDirty();

						// Optional: Send feedback message to player
						player.sendMessage(Text.literal(String.format("§aIncreased max stamina by %.1f! (Total bonus: %.1f)", config.miningStaminaGainAmount, playerState.miningBonusStamina)), false);
					} else {
						// If threshold not met, still mark dirty to save progress counter
						serverState.markDirty();
					}
				}
			}
		});

		//commands
		//setTotalStamina command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setTotalStamina").requires(source -> source.hasPermissionLevel(4))
				.then(argument("value", IntegerArgumentType.integer())
					.then(argument("player", EntityArgumentType.player())
						.executes(context -> {

							ServerState serverState = ServerState.getServerState(EntityArgumentType.getPlayer(context, "player").getWorld().getServer());
							RStaminaPlayerState playerState = ServerState.getPlayerState(EntityArgumentType.getPlayer(context, "player"));

							// Use the new setter method for custom total stamina
							double newValue = IntegerArgumentType.getInteger(context, "value");
							playerState.setCustomTotalStamina(newValue);
							playerState.maxStamina = (playerState.totalStamina * (playerState.energy / 100));
							playerState.stamina = playerState.maxStamina;
							serverState.markDirty();

							context.getSource().sendMessage(Text.literal("Set " + EntityArgumentType.getPlayer(context, "player").getName().getString() + "'s total stamina to " + newValue));

							return 1;
						})))));

		//resetStaminaStats command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("resetStaminaStats").requires(source -> source.hasPermissionLevel(4))
							.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									ServerState serverState = ServerState.getServerState(EntityArgumentType.getPlayer(context, "player").getWorld().getServer());
									RStaminaPlayerState playerState = ServerState.getPlayerState(EntityArgumentType.getPlayer(context, "player"));

									// Reset using the new method
									playerState.resetToConfigDefaults();
									
									// Reset all other values
									playerState.stamina = RStaminaMod.config.totalStamina;
									playerState.gainedStamina = 0.0;
									playerState.miningBonusStamina = 0.0;
									playerState.miningBlockProgress = 0;
									playerState.energy = 100.0;
									playerState.usedEnergy = 0.0;
									playerState.staminaRegenCooldown = 0;
									
									// Update calculated values
									playerState.updateMaxStamina();
									
									// Set rates to config defaults
									syncPlayerStateWithConfig(playerState);

									serverState.markDirty();

									context.getSource().sendMessage(Text.literal("Reset " + EntityArgumentType.getPlayer(context, "player").getName().getString() + "'s stamina stats."));

									return 1;
								}))));

		//setStaminaLossRate
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setStaminaLossRate").requires(source -> source.hasPermissionLevel(4))
				.then(argument("value", DoubleArgumentType.doubleArg())
						.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									ServerState serverState = ServerState.getServerState(EntityArgumentType.getPlayer(context, "player").getWorld().getServer());
									RStaminaPlayerState playerState = ServerState.getPlayerState(EntityArgumentType.getPlayer(context, "player"));

									playerState.staminaLossRate = DoubleArgumentType.getDouble(context, "value");
									playerState.edited = true;
									serverState.markDirty();

									context.getSource().sendMessage(Text.literal("Set " + EntityArgumentType.getPlayer(context, "player").getName().getString() + "'s stamina loss rate to " + DoubleArgumentType.getDouble(context, "value")));

									return 1;
								})))));

		// Register showspeed command (available to all players)
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("showspeed")
				.executes(context -> {
					ServerPlayerEntity player = context.getSource().getPlayer();
					if (player != null) {
						// Check if speed multiplier feature is enabled
						if (RStaminaMod.config.enableSpeedMultiplier) {
							// Get the player's state
							RStaminaPlayerState playerState = ServerState.getPlayerState(player);
							
							// Calculate speed multiplier based on max stamina
							double baseStamina = RStaminaMod.config.totalStamina;
							double maxStamina = RStaminaMod.config.fitnessStaminaLimit;
							double currentTotalStamina = playerState.totalStamina;
							
							// Calculate a multiplier between 1.0 and maxSpeedMultiplier based on stamina
							double staminaProgress = Math.min(1.0, Math.max(0.0, (currentTotalStamina - baseStamina) / (maxStamina - baseStamina)));
							double speedMultiplier = 1.0 + (staminaProgress * (RStaminaMod.config.maxSpeedMultiplier - 1.0));
							
							// Send a message to the player
							player.sendMessage(Text.literal(String.format("§bCurrent speed multiplier: §f%.2fx", speedMultiplier)), false);
							
							// Send a packet to the client to show the speed multiplier for 30 seconds (600 ticks)
							PacketByteBuf sendingData = PacketByteBufs.create();
							sendingData.writeInt(600); // 30 seconds * 20 ticks per second
							ServerPlayNetworking.send(player, NetworkingPackets.SEND_SHOW_SPEED_S2C_PACKET_ID, sendingData);
						} else {
							// Send a message to the player that the feature is disabled
							player.sendMessage(Text.literal("§cSpeed multiplier feature is disabled in the configuration."), false);
						}
					}
					return 1;
				})));

		//setStaminaGainRate
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setStaminaGainRate").requires(source -> source.hasPermissionLevel(4))
				.then(argument("value", DoubleArgumentType.doubleArg())
						.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									ServerState serverState = ServerState.getServerState(EntityArgumentType.getPlayer(context, "player").getWorld().getServer());
									RStaminaPlayerState playerState = ServerState.getPlayerState(EntityArgumentType.getPlayer(context, "player"));

									playerState.staminaGainRate = DoubleArgumentType.getDouble(context, "value");
									playerState.edited = true;
									serverState.markDirty();

									context.getSource().sendMessage(Text.literal("Set " + EntityArgumentType.getPlayer(context, "player").getName().getString() + "'s stamina gain rate to " + DoubleArgumentType.getDouble(context, "value")));

									return 1;
								})))));

		//setEnergyLossRate
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setEnergyLossRate").requires(source -> source.hasPermissionLevel(4))
				.then(argument("value", DoubleArgumentType.doubleArg())
						.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									ServerState serverState = ServerState.getServerState(EntityArgumentType.getPlayer(context, "player").getWorld().getServer());
									RStaminaPlayerState playerState = ServerState.getPlayerState(EntityArgumentType.getPlayer(context, "player"));

									playerState.energyLossRate = DoubleArgumentType.getDouble(context, "value");
									playerState.edited = true;
									serverState.markDirty();

									context.getSource().sendMessage(Text.literal("Set " + EntityArgumentType.getPlayer(context, "player").getName().getString() + "'s energy loss rate to " + DoubleArgumentType.getDouble(context, "value")));

									return 1;
								})))));

		//setEnergyGainRate
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setEnergyGainRate").requires(source -> source.hasPermissionLevel(4))
				.then(argument("value", DoubleArgumentType.doubleArg())
						.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									ServerState serverState = ServerState.getServerState(EntityArgumentType.getPlayer(context, "player").getWorld().getServer());
									RStaminaPlayerState playerState = ServerState.getPlayerState(EntityArgumentType.getPlayer(context, "player"));

									playerState.energyGainRate = DoubleArgumentType.getDouble(context, "value");
									playerState.edited = true;
									serverState.markDirty();

									context.getSource().sendMessage(Text.literal("Set " + EntityArgumentType.getPlayer(context, "player").getName().getString() + "'s energy gain rate to " + DoubleArgumentType.getDouble(context, "value")));

									return 1;
								})))));
	}
	
	/**
	 * Helper method to synchronize player state values with config defaults
	 * @param playerState The player state to update
	 */
	private static void syncPlayerStateWithConfig(RStaminaPlayerState playerState) {
		// Map of player state fields to config values using a functional approach
		playerState.energyGainRate = config.restingEnergyGainTick;
		playerState.energyLossRate = config.energyLossRate;
		playerState.staminaLossRate = config.staminaLossRate;
		playerState.staminaGainRate = config.staminaGainRate;
		playerState.walkingStaminaLossRate = config.walkingStaminaLossRate;
		playerState.walkingEnergyLossRate = config.walkingEnergyLossRate;
	}
}