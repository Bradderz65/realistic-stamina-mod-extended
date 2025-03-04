package realisticstamina.rstamina.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import realisticstamina.rstamina.RStaminaClient;
import realisticstamina.rstamina.RStaminaMod;
import com.mojang.blaze3d.systems.RenderSystem;

public class StaminaHudOverlay implements HudRenderCallback {

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        int x = RStaminaMod.config.hudX;
        int y = RStaminaMod.config.hudY;

        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null && RStaminaClient.showingStaminaTicks > 0) {
            float alpha = RStaminaClient.showingStaminaTicks / 20.0f;
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

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

            // Only display speed multiplier when showSpeedMultiplierTicks is active
            if (RStaminaClient.showSpeedMultiplierTicks > 0) {
                // Calculate alpha for speed display based on remaining time
                float speedAlpha = Math.min(1.0f, RStaminaClient.showSpeedMultiplierTicks / 20.0f);
                
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, speedAlpha);
                String speedText = String.format("§bSpeed: §f%.1fx", RStaminaClient.clientStoredSpeedMultiplier);
                drawContext.drawTextWithShadow(textRenderer, Text.literal(speedText), x, y + 20, 16777215);
            }

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
}
