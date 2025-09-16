package realisticstamina.rstamina;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Collections;

public class ServerState extends PersistentState {

    public String worldVersion = RStaminaMod.rStaminaModVersion;
    public Map<UUID, RStaminaPlayerState> players = new ConcurrentHashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound playersNbtCompound = new NbtCompound();
        
        Map<UUID, RStaminaPlayerState> playersCopy;
        synchronized (players) {
            playersCopy = new HashMap<>(players);
        }
        
        playersCopy.forEach((UUID, playerState) -> {
            NbtCompound playerStateNbt = new NbtCompound();

            playerStateNbt.putDouble("stamina", playerState.stamina);
            playerStateNbt.putDouble("maxStamina", playerState.maxStamina);
            playerStateNbt.putDouble("totalStamina", playerState.getTotalStamina());
            playerStateNbt.putDouble("energy", playerState.energy);
            playerStateNbt.putDouble("usedEnergy", playerState.usedEnergy);
            playerStateNbt.putDouble("energyFromResting", playerState.energyFromResting);
            playerStateNbt.putBoolean("edited", playerState.edited);
            playerStateNbt.putDouble("customTotalStamina", playerState.customTotalStamina);
            playerStateNbt.putInt("staminaRegenCooldown", playerState.staminaRegenCooldown);
            playerStateNbt.putInt("miningFatigueCooldown", playerState.miningFatigueCooldown);
            playerStateNbt.putInt("exhaustionRestTimer", playerState.exhaustionRestTimer);
            playerStateNbt.putBoolean("isExhausted", playerState.isExhausted);
            playerStateNbt.putDouble("staminaLossRate", playerState.staminaLossRate);
            playerStateNbt.putDouble("staminaGainRate", playerState.staminaGainRate);
            playerStateNbt.putDouble("energyLossRate", playerState.energyLossRate);
            playerStateNbt.putDouble("energyGainRate", playerState.energyGainRate);
            playerStateNbt.putBoolean("wasOnGround", playerState.wasOnGround);

            playerStateNbt.putInt("miningBlockProgress", playerState.miningBlockProgress);
            playerStateNbt.putDouble("miningBonusStamina", playerState.miningBonusStamina);
            playerStateNbt.putDouble("gainedStamina", playerState.gainedStamina);

            playersNbtCompound.put(String.valueOf(UUID), playerStateNbt);
        });
        nbt.put("players", playersNbtCompound);

        nbt.putString("worldVersion", worldVersion);
        return nbt;
    }

    public static ServerState createFromNbt(NbtCompound tag) {
        ServerState serverState = new ServerState();

        NbtCompound playersTag = tag.getCompound("players");
        playersTag.getKeys().forEach(key -> {
            RStaminaPlayerState playerState = new RStaminaPlayerState();

            playerState.stamina = playersTag.getCompound(key).getDouble("stamina");
            playerState.maxStamina = playersTag.getCompound(key).getDouble("maxStamina");
            playerState.totalStamina = playersTag.getCompound(key).getDouble("totalStamina");
            playerState.energy = playersTag.getCompound(key).getDouble("energy");
            playerState.usedEnergy = playersTag.getCompound(key).getDouble("usedEnergy");
            playerState.energyFromResting = playersTag.getCompound(key).getDouble("energyFromResting");
            playerState.edited = playersTag.getCompound(key).getBoolean("edited");
            if (playersTag.getCompound(key).contains("customTotalStamina")) {
                playerState.customTotalStamina = playersTag.getCompound(key).getDouble("customTotalStamina");
            } else {
                playerState.customTotalStamina = -1.0;
            }
            playerState.staminaRegenCooldown = playersTag.getCompound(key).getInt("staminaRegenCooldown");
            playerState.miningFatigueCooldown = playersTag.getCompound(key).getInt("miningFatigueCooldown");
            playerState.exhaustionRestTimer = playersTag.getCompound(key).getInt("exhaustionRestTimer");
            playerState.isExhausted = playersTag.getCompound(key).getBoolean("isExhausted");
            playerState.staminaLossRate = playersTag.getCompound(key).getDouble("staminaLossRate");
            playerState.staminaGainRate = playersTag.getCompound(key).getDouble("staminaGainRate");
            playerState.energyLossRate = playersTag.getCompound(key).getDouble("energyLossRate");
            playerState.energyGainRate = playersTag.getCompound(key).getDouble("energyGainRate");
            playerState.wasOnGround = playersTag.getCompound(key).getBoolean("wasOnGround");

            playerState.miningBlockProgress = playersTag.getCompound(key).getInt("miningBlockProgress");
            playerState.miningBonusStamina = playersTag.getCompound(key).getDouble("miningBonusStamina");
            
            if (playersTag.getCompound(key).contains("gainedStamina")) {
                playerState.gainedStamina = playersTag.getCompound(key).getDouble("gainedStamina");
            }

            UUID uuid = UUID.fromString(key);
            serverState.players.put(uuid, playerState);
        });

        serverState.worldVersion = tag.getString("worldVersion");

        return serverState;
    }

    public static ServerState getServerState(MinecraftServer server) {
        if (server == null || server.getWorld(World.OVERWORLD) == null) {
            return null;
        }
        
        PersistentStateManager persistentStateManager = server
                .getWorld(World.OVERWORLD).getPersistentStateManager();

        ServerState serverState = persistentStateManager.getOrCreate(
                ServerState::createFromNbt,
                ServerState::new,
                RStaminaMod.modid);

        return serverState;
    }

    public static RStaminaPlayerState getPlayerState(LivingEntity player) {
        if (player == null || player.getWorld() == null || player.getWorld().getServer() == null) {
            return new RStaminaPlayerState();
        }
        
        ServerState serverState = getServerState(player.getWorld().getServer());
        if (serverState == null) {
            return new RStaminaPlayerState();
        }

        return serverState.players.computeIfAbsent(player.getUuid(), uuid -> new RStaminaPlayerState());
    }
}