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
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
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

	public static final String rStaminaModVersion = "1.4.1.3";
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

			if (!Objects.equals(serverState.worldVersion, rStaminaModVersion)) {
				serverState.worldVersion = rStaminaModVersion;
				/*serverState.players.forEach((p, s) -> {
					s.usedEnergy = 0.0;
				});*/
				serverState.markDirty();
			}

			if (!playerState.edited) { //if the playerstate wasn't edited then match with config
				if (playerState.energyGainRate != config.restingEnergyGainTick) {
					playerState.energyGainRate = config.restingEnergyGainTick;
				}
				if (playerState.energyLossRate != config.energyLossRate) {
					playerState.energyLossRate = config.energyLossRate;
				}
				if (playerState.totalStamina != config.totalStamina) {
					playerState.totalStamina = config.totalStamina+ playerState.gainedStamina;
					playerState.maxStamina = (playerState.totalStamina * (playerState.energy / 100));
				}
				if (playerState.staminaLossRate != config.staminaLossRate) {
					playerState.staminaLossRate = config.staminaLossRate;
				}
				if (playerState.staminaGainRate != config.staminaGainRate) {
					playerState.staminaGainRate = config.staminaGainRate;
				}
				serverState.markDirty();
			}

			if (playerState.staminaRegenCooldown != 0) {
				playerState.staminaRegenCooldown = 0;
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
						playerState.maxStamina = (playerState.totalStamina * (playerState.energy / 100));
					}
					playerState.staminaRegenCooldown = 20;
					serverState.markDirty();
				}
			}

			return true;
		});

		//commands
		//setTotalStamina command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("setTotalStamina").requires(source -> source.hasPermissionLevel(4))
				.then(argument("value", IntegerArgumentType.integer())
					.then(argument("player", EntityArgumentType.player())
						.executes(context -> {

							ServerState serverState = ServerState.getServerState(EntityArgumentType.getPlayer(context, "player").getWorld().getServer());
							RStaminaPlayerState playerState = ServerState.getPlayerState(EntityArgumentType.getPlayer(context, "player"));

							playerState.totalStamina = IntegerArgumentType.getInteger(context, "value");
							playerState.edited = true;
							playerState.maxStamina = (playerState.totalStamina * (playerState.energy / 100));
							playerState.stamina = playerState.maxStamina;
							serverState.markDirty();

							context.getSource().sendMessage(Text.literal("Set " + EntityArgumentType.getPlayer(context, "player").getName().getString() + "'s total stamina to " + IntegerArgumentType.getInteger(context, "value")));

							return 1;
						})))));

		//resetStaminaStats command
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("resetStaminaStats").requires(source -> source.hasPermissionLevel(4))
							.then(argument("player", EntityArgumentType.player())
								.executes(context -> {

									ServerState serverState = ServerState.getServerState(EntityArgumentType.getPlayer(context, "player").getWorld().getServer());
									RStaminaPlayerState playerstate = ServerState.getPlayerState(EntityArgumentType.getPlayer(context, "player"));

									playerstate.stamina = RStaminaMod.config.totalStamina;
									playerstate.maxStamina = RStaminaMod.config.totalStamina;
									playerstate.totalStamina = RStaminaMod.config.totalStamina;
									playerstate.energy = 100.0;
									playerstate.edited = false;
									playerstate.staminaRegenCooldown = 0;
									playerstate.staminaLossRate = config.staminaLossRate;
									playerstate.staminaGainRate = config.staminaGainRate;
									playerstate.energyLossRate = config.energyLossRate;
									playerstate.energyGainRate = RStaminaMod.config.restingEnergyGainTick;

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
}