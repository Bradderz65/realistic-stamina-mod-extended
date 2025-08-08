package realisticstamina.rstamina;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import realisticstamina.rstamina.client.StaminaHudOverlay;
import realisticstamina.rstamina.networking.NetworkingPackets;

import java.util.Objects;

import static net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send;

public class RStaminaClient implements ClientModInitializer {

    //these numbers are only used to display stats on the client. they do nothing else
    public static int clientStoredTestPlayerData = 34;

    public static double clientStoredStamina = 400.0;
    public static double clientStoredMaxStamina = 551.0;
    public static double clientStoredEnergy = 10.0;
    public static double clientStoredTotalStamina = 112.0;
    public static double clientStoredSpeedMultiplier = 1.0;

    public static int showingStaminaTicks = 0;
    // Timer for showing speed multiplier (in ticks, 20 ticks = 1 second)
    public static int showSpeedMultiplierTicks = 0;

    @Override
    public void onInitializeClient() {



        //networking
        NetworkingPackets.registerS2CPackets();

        //hud
        HudRenderCallback.EVENT.register(new StaminaHudOverlay());

        //tick
        ClientTickEvents.START_CLIENT_TICK.register((client) -> {
            if (client.world != null && client.world.isClient() && client.player != null) { // Added client.player != null check
                boolean canUpdate = client.isInSingleplayer() ? !client.isPaused() : true;
                if (canUpdate) {
                    handleClientTickUpdates(client);
                }
            }
        });

        EntitySleepEvents.STOP_SLEEPING.register((entity, blockPos) -> {
            if (entity.isPlayer()) {
                if (entity.getWorld().isClient()) {
                    MinecraftClient mcClient = MinecraftClient.getInstance(); // Renamed to avoid conflict
                    if (mcClient != null && mcClient.player != null) { // Added mcClient.player != null check
                        if (Objects.equals(entity.getName().getString(), mcClient.player.getName().getString())) {
                            send(NetworkingPackets.PLAYER_SLEEP_C2S_PACKET_ID, PacketByteBufs.create());
                        }
                    }
                }
            }
        });

    }

    private static void handleClientTickUpdates(MinecraftClient client) {
        if (clientStoredStamina < clientStoredMaxStamina) {
            showingStaminaTicks = 20;
        } else if (clientStoredStamina == clientStoredMaxStamina && showingStaminaTicks > 0) {
            showingStaminaTicks -= 1;
        }

        // Decrement speed multiplier display timer if active
        if (showSpeedMultiplierTicks > 0) {
            showSpeedMultiplierTicks -= 1;
        }

        ClientPlayNetworking.send(NetworkingPackets.UPDATE_STAMINA_C2S_PACKET_ID, PacketByteBufs.create());
        if (client.player != null && client.player.getVehicle() != null) { // Ensured client.player is not null before getVehicle()
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(client.player.getVehicle().getName().getString());
            buf.writeBoolean(client.player.isRiding());
            ClientPlayNetworking.send(NetworkingPackets.RIDING_C2S_PACKET_ID, buf);
        }
    }

}
