package realisticstamina.rstamina.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import realisticstamina.rstamina.RStaminaClient;
import realisticstamina.rstamina.RStaminaMod;
import realisticstamina.rstamina.networking.packet.*;

public class NetworkingPackets {

    //C2S test
    public static final Identifier TEST_PACKET_ID = new Identifier(RStaminaMod.modid, "test_packet");
    public static final Identifier REQUEST_TESTDATA_PACKET_ID = new Identifier(RStaminaMod.modid, "request_testdata_packet");
    public static final Identifier TEST_UPDATE_DATA_PACKET_ID = new Identifier(RStaminaMod.modid, "test_update_data_packet");

    //C2S
    public static final Identifier UPDATE_STAMINA_C2S_PACKET_ID = new Identifier(RStaminaMod.modid, "update_stamina_c2s_packet");
    public static final Identifier REQUEST_PLAYERSTATE_C2S_PACKET_ID = new Identifier(RStaminaMod.modid, "request_playerstate_c2s_packet");
    public static final Identifier PLAYER_SLEEP_C2S_PACKET_ID = new Identifier(RStaminaMod.modid, "player_sleep_c2s_packet");
    public static final Identifier RESET_PLAYERSTATE_C2S_PACKET_ID = new Identifier(RStaminaMod.modid, "reset_playerstate_c2s_packet");
    public static final Identifier RIDING_C2S_PACKET_ID = new Identifier(RStaminaMod.modid, "riding_c2s_packet");

    //S2C test
    public static final Identifier TEST_S2C_PACKET_ID = new Identifier(RStaminaMod.modid, "test_s2c_packet");
    public static final Identifier SEND_TESTDATA_PACKET_ID = new Identifier(RStaminaMod.modid, "send_testdata_s2c_packet");

    //S2C
    public static final Identifier SEND_PLAYERSTATE_S2C_PACKET_ID = new Identifier(RStaminaMod.modid, "send_playerstate_s2c_packet");

    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(TEST_PACKET_ID, TestC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_TESTDATA_PACKET_ID, RequestTestDataC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(TEST_UPDATE_DATA_PACKET_ID, TestUpdateDataC2SPacket::receive);


        ServerPlayNetworking.registerGlobalReceiver(UPDATE_STAMINA_C2S_PACKET_ID, UpdateStaminaC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_PLAYERSTATE_C2S_PACKET_ID, RequestPlayerStateC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(PLAYER_SLEEP_C2S_PACKET_ID, PlayerSleepC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(RESET_PLAYERSTATE_C2S_PACKET_ID, ResetPlayerStateC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(RIDING_C2S_PACKET_ID, RidingC2SPacket::receive);
    }

    public static void registerS2CPackets() {

        ClientPlayNetworking.registerGlobalReceiver(TEST_S2C_PACKET_ID, (client, handler, buf, responseSender) -> {
            client.execute(() -> {

                Text name = client.player.getName();
                RStaminaMod.LOGGER.info("NAME: " + name.toString());

            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SEND_TESTDATA_PACKET_ID, (client, handler, buf, responseSender) -> {
            int i = buf.readInt();
            client.execute(() -> {

                RStaminaClient.clientStoredTestPlayerData = i;

            });
        });


        ClientPlayNetworking.registerGlobalReceiver(SEND_PLAYERSTATE_S2C_PACKET_ID, (client, handler, buf, responseSender) -> {
            double stamina = buf.readDouble();
            double maxStamina = buf.readDouble();
            double energy = buf.readDouble();
            double totalStamina = buf.readDouble();
            client.execute(() -> {

                RStaminaClient.clientStoredStamina = stamina;
                RStaminaClient.clientStoredMaxStamina = maxStamina;
                RStaminaClient.clientStoredEnergy = energy;
                RStaminaClient.clientStoredTotalStamina = totalStamina;

            });
        });

    }

}
