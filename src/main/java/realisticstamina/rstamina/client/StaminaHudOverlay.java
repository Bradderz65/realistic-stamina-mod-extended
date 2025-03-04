package realisticstamina.rstamina.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import realisticstamina.rstamina.RStaminaClient;
import realisticstamina.rstamina.RStaminaMod;

public class StaminaHudOverlay implements HudRenderCallback {



    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {

        //RStaminaMod.LOGGER.info("renderkajsd;flakjsdf");

        int x = RStaminaMod.config.hudX;
        int y = RStaminaMod.config.hudY;

        MinecraftClient client = MinecraftClient.getInstance();

        //RenderSystem.setShader(GameRenderer::getPositionTexShader);
        //RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
        //RenderSystem.setShaderTexture(0, )

        if (client != null && RStaminaClient.showingStaminaTicks > 0) {

            TextRenderer textRenderer = client.textRenderer;

            if ((Math.round(RStaminaClient.clientStoredStamina)) > 24.0 && (Math.round(RStaminaClient.clientStoredStamina)) == (Math.round(RStaminaClient.clientStoredMaxStamina))) {
                drawContext.drawTextWithShadow(textRenderer, Text.literal("§aStamina: §a" + (Math.round(RStaminaClient.clientStoredStamina)) + "§7/" + (Math.round(RStaminaClient.clientStoredMaxStamina))), x, y, 16777215);
            } else if ((Math.round(RStaminaClient.clientStoredStamina)) >= 24.0 && (Math.round(RStaminaClient.clientStoredStamina)) < (Math.round(RStaminaClient.clientStoredMaxStamina))) {
                drawContext.drawTextWithShadow(textRenderer, Text.literal("§2Stamina: §a" + (Math.round(RStaminaClient.clientStoredStamina)) + "§7/" + (Math.round(RStaminaClient.clientStoredMaxStamina))), x, y, 16777215);
            } else if ((Math.round(RStaminaClient.clientStoredStamina)) < 24.0 && (Math.round(RStaminaClient.clientStoredStamina)) > 12) {
                drawContext.drawTextWithShadow(textRenderer, Text.literal("§2Stamina: §e" + (Math.round(RStaminaClient.clientStoredStamina)) + "§7/" + (Math.round(RStaminaClient.clientStoredMaxStamina))), x, y, 16777215);
            } else if ((Math.round(RStaminaClient.clientStoredStamina)) <= 12 && (Math.round(RStaminaClient.clientStoredStamina)) > 0) {
                drawContext.drawTextWithShadow(textRenderer, Text.literal("§2Stamina: §6" + (Math.round(RStaminaClient.clientStoredStamina)) + "§7/" + (Math.round(RStaminaClient.clientStoredMaxStamina))), x, y, 16777215);
            } else if ((Math.round(RStaminaClient.clientStoredStamina)) <= 0) {
                drawContext.drawTextWithShadow(textRenderer, Text.literal("§2Stamina: §c" + (Math.round(RStaminaClient.clientStoredStamina)) + "§7/" + (Math.round(RStaminaClient.clientStoredMaxStamina))), x, y, 16777215);
            }

            drawContext.drawTextWithShadow(textRenderer, Text.literal("§eEnergy: §f" + ((float)RStaminaClient.clientStoredEnergy) + "%"), x, y + 10, 16777215);

        }



    }
}
