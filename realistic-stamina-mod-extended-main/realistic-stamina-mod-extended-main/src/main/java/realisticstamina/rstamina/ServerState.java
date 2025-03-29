package realisticstamina.rstamina;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;

public class ServerState extends PersistentState {

    int testInt = 0;
    public String worldVersion = RStaminaMod.rStaminaModVersion;

    public HashMap<UUID, RStaminaPlayerState> players = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtCompound playersNbtCompound = new NbtCompound();
        players.forEach((UUID, playerSate) -> {
            NbtCompound playerStateNbt = new NbtCompound();

            playerStateNbt.putInt("testplayerdata", playerSate.testplayerdata);
            playerStateNbt.putDouble("stamina", playerSate.stamina);
            playerStateNbt.putDouble("maxStamina", playerSate.maxStamina);
            playerStateNbt.putDouble("gainedStamina", playerSate.gainedStamina);
            playerStateNbt.putDouble("totalStamina", playerSate.totalStamina);
            playerStateNbt.putDouble("energy", playerSate.energy);
            playerStateNbt.putDouble("usedEnergy", playerSate.usedEnergy);
            playerStateNbt.putDouble("energyFromResting", playerSate.energyFromResting);
            playerStateNbt.putBoolean("edited", playerSate.edited);
            playerStateNbt.putInt("staminaRegenCooldown", playerSate.staminaRegenCooldown);
            playerStateNbt.putInt("miningFatigueCooldown", playerSate.miningFatigueCooldown);
            playerStateNbt.putInt("exhaustionRestTimer", playerSate.exhaustionRestTimer);
            playerStateNbt.putBoolean("isExhausted", playerSate.isExhausted);
            playerStateNbt.putDouble("staminaLossRate", playerSate.staminaLossRate);
            playerStateNbt.putDouble("staminaGainRate", playerSate.staminaGainRate);
            playerStateNbt.putDouble("energyLossRate", playerSate.energyLossRate);
            playerStateNbt.putDouble("energyGainRate", playerSate.energyGainRate);
            playerStateNbt.putBoolean("wasOnGround", playerSate.wasOnGround);
            playerStateNbt.putInt("blocksMined", playerSate.blocksMined);

            playersNbtCompound.put(String.valueOf(UUID), playerStateNbt);
        });
        nbt.put("players", playersNbtCompound);

        nbt.putInt("testInt", testInt);
        nbt.putString("worldVersion", worldVersion);
        return nbt;
    }

    public static ServerState createFromNbt(NbtCompound tag) {

        ServerState serverState = new ServerState();

        NbtCompound playersTag = tag.getCompound("players");
        playersTag.getKeys().forEach(key -> {
            RStaminaPlayerState playerState = new RStaminaPlayerState();

            playerState.testplayerdata = playersTag.getCompound(key).getInt("testplayerdata");
            playerState.stamina = playersTag.getCompound(key).getDouble("stamina");
            playerState.maxStamina = playersTag.getCompound(key).getDouble("maxStamina");
            
            // Check if the gainedStamina field exists in the NBT data (for backwards compatibility)
            if (playersTag.getCompound(key).contains("gainedStamina")) {
                playerState.gainedStamina = playersTag.getCompound(key).getDouble("gainedStamina");
            }
            
            playerState.totalStamina = playersTag.getCompound(key).getDouble("totalStamina");
            playerState.energy = playersTag.getCompound(key).getDouble("energy");
            playerState.usedEnergy = playersTag.getCompound(key).getDouble("usedEnergy");
            playerState.energyFromResting = playersTag.getCompound(key).getDouble("energyFromResting");
            playerState.edited = playersTag.getCompound(key).getBoolean("edited");
            playerState.staminaRegenCooldown = playersTag.getCompound(key).getInt("staminaRegenCooldown");
            playerState.miningFatigueCooldown = playersTag.getCompound(key).getInt("miningFatigueCooldown");
            playerState.exhaustionRestTimer = playersTag.getCompound(key).getInt("exhaustionRestTimer");
            playerState.isExhausted = playersTag.getCompound(key).getBoolean("isExhausted");
            playerState.staminaLossRate = playersTag.getCompound(key).getDouble("staminaLossRate");
            playerState.staminaGainRate = playersTag.getCompound(key).getDouble("staminaGainRate");
            playerState.energyLossRate = playersTag.getCompound(key).getDouble("energyLossRate");
            playerState.energyGainRate = playersTag.getCompound(key).getDouble("energyGainRate");
            playerState.wasOnGround = playersTag.getCompound(key).getBoolean("wasOnGround");
            
            // Check if the blocksMined field exists in the NBT data (for backwards compatibility)
            if (playersTag.getCompound(key).contains("blocksMined")) {
                playerState.blocksMined = playersTag.getCompound(key).getInt("blocksMined");
            }

            UUID uuid = UUID.fromString(key);
            serverState.players.put(uuid, playerState);
        });

        serverState.testInt = tag.getInt("testInt");
        serverState.worldVersion = tag.getString("worldVersion");

        return serverState;
    }

    public static ServerState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server
                .getWorld(World.OVERWORLD).getPersistentStateManager();

        ServerState serverState = persistentStateManager.getOrCreate(
                ServerState::createFromNbt,
                ServerState::new,
                RStaminaMod.modid);

        return serverState;
    }

    public static RStaminaPlayerState getPlayerState(LivingEntity player) {
        ServerState serverState = getServerState(player.getWorld().getServer());

        RStaminaPlayerState playerState = serverState.players.computeIfAbsent(player.getUuid(), uuid -> new RStaminaPlayerState());

        return playerState;
    }
}