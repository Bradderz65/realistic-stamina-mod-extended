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

    // Bar dimensions
    private static final int BAR_WIDTH = 72;
    private static final int BAR_HEIGHT = 4;
    private static final int SPACING = 3;
    private static final int CORNER_RADIUS = 0;
    
    // Store previous values for smooth animation
    private static double lastStaminaPercentage = 1.0;
    private static double lastEnergyPercentage = 1.0;
    
    // Animation speed (lower = smoother but slower)
    private static final double ANIMATION_SPEED = 0.15;

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        int hudAnchorX = RStaminaMod.config.hudX;
        int hudAnchorY = RStaminaMod.config.hudY;
        float globalScale = (float) RStaminaMod.config.hudScale;

        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null && RStaminaClient.showingStaminaTicks > 0) {
            float alpha = Math.min(1.0f, RStaminaClient.showingStaminaTicks / 20.0f);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

            drawContext.getMatrices().push();
            drawContext.getMatrices().translate(hudAnchorX, hudAnchorY, 0);
            drawContext.getMatrices().scale(globalScale, globalScale, 1.0f);

            // All drawing from now on is relative to (0,0) in the scaled matrix
            int x = 0; 
            int y = 0;

            TextRenderer textRenderer = client.textRenderer;
            float textScale = 0.75f; // Defined here for broader scope
            
            // Calculate current values
            double stamina = RStaminaClient.clientStoredStamina;
            double maxStamina = RStaminaClient.clientStoredMaxStamina;
            double targetStaminaPercentage = Math.min(1.0, Math.max(0.0, stamina / maxStamina));
            double energy = RStaminaClient.clientStoredEnergy;
            double targetEnergyPercentage = Math.min(1.0, Math.max(0.0, energy / 100.0));
            
            // Animate smoothly
            lastStaminaPercentage += (targetStaminaPercentage - lastStaminaPercentage) * ANIMATION_SPEED;
            lastEnergyPercentage += (targetEnergyPercentage - lastEnergyPercentage) * ANIMATION_SPEED;
            
            // ----- DRAW STAMINA BAR -----
            
            // Determine stamina bar color based on percentage
            int staminaColor;
            if (lastStaminaPercentage > 0.6) {
                staminaColor = 0xFF59FF59; // Light green
            } else if (lastStaminaPercentage > 0.3) {
                staminaColor = 0xFFFFE359; // Light yellow
            } else {
                staminaColor = 0xFFFF5959; // Light red
            }
            
            // Draw stamina bar background
            drawFilledRect(drawContext, x, y, x + BAR_WIDTH, y + BAR_HEIGHT, 0x80000000);
            
            // Draw filled stamina bar
            int fillWidth = (int) (BAR_WIDTH * lastStaminaPercentage);
            if (fillWidth > 0) {
                drawFilledRect(drawContext, x, y, x + fillWidth, y + BAR_HEIGHT, staminaColor);
            }
            
            // ----- DRAW ENERGY BAR -----
            
            // Draw energy bar below stamina bar
            int energyBarY = y + BAR_HEIGHT + SPACING;
            
            // Draw energy bar background
            drawFilledRect(drawContext, x, energyBarY, x + BAR_WIDTH, energyBarY + BAR_HEIGHT, 0x80000000);
            
            // Determine energy bar color
            int energyColor = 0xFF5B9BFF; // Light blue
            
            // Draw filled energy bar
            fillWidth = (int) (BAR_WIDTH * lastEnergyPercentage);
            if (fillWidth > 0) {
                drawFilledRect(drawContext, x, energyBarY, x + fillWidth, energyBarY + BAR_HEIGHT, energyColor);
            }
            
            // ----- TEXT VALUES (Conditional based on HUD Style) -----
            if (RStaminaMod.config.hudStyle == realisticstamina.rstamina.RStaminaConfig.HudStyle.DETAILED) {
                int textX = BAR_WIDTH + SPACING; // Position relative to bar end

                // Calculate Y positions for vertically centered scaled text
                float scaledTextHeight = textRenderer.fontHeight * textScale;
                int staminaTextY = (int) (y + (BAR_HEIGHT - scaledTextHeight) / 2.0f);
                int energyTextY = (int) (energyBarY + (BAR_HEIGHT - scaledTextHeight) / 2.0f);

                // Stamina Value
                String staminaValue = String.format("%.0f/%.0f", stamina, maxStamina);
                drawContext.getMatrices().push();
                drawContext.getMatrices().translate(textX, staminaTextY, 0);
                drawContext.getMatrices().scale(textScale, textScale, 1.0f);
                drawContext.drawTextWithShadow(textRenderer, staminaValue, 0, 0, staminaColor);
                drawContext.getMatrices().pop(); // Pops the text-specific matrix

                // Energy Value (simplified format)
                String energyValue = String.format("%.0f", energy);
                drawContext.getMatrices().push();
                drawContext.getMatrices().translate(textX, energyTextY, 0);
                drawContext.getMatrices().scale(textScale, textScale, 1.0f); // Apply local text scale
                drawContext.drawTextWithShadow(textRenderer, energyValue, 0, 0, energyColor);
                drawContext.getMatrices().pop(); // Pops the text-specific matrix
            }
            
            // ----- DISPLAY SPEED MULTIPLIER -----
            
            // Only display speed multiplier when showSpeedMultiplierTicks is active
            if (RStaminaClient.showSpeedMultiplierTicks > 0) {
                float speedAlpha = Math.min(1.0f, RStaminaClient.showSpeedMultiplierTicks / 20.0f);
                
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, speedAlpha);
                // Only show speed if it's greater than 1.0
                if (RStaminaClient.clientStoredSpeedMultiplier > 1.01) {
                    String speedText = String.format("×%.1f", RStaminaClient.clientStoredSpeedMultiplier);
                    
                    // Calculate Y position for speed text (below energy bar with a bit more spacing)
                    int speedY = energyBarY + BAR_HEIGHT + SPACING + 2; // Added 2px more spacing

                    drawContext.getMatrices().push();
                    // Center the text horizontally under the bars
                    float scaledTextWidth = textRenderer.getWidth(speedText) * textScale;
                    float centeredTextX = x + (BAR_WIDTH - scaledTextWidth) / 2.0f;
                    drawContext.getMatrices().translate(centeredTextX, speedY, 0);
                    drawContext.getMatrices().scale(textScale, textScale, 1.0f);
                    drawContext.drawTextWithShadow(textRenderer, speedText, 0, 0, 0xFF59FFFF); // Cyan-ish color
                    drawContext.getMatrices().pop();
                }
            }

            drawContext.getMatrices().pop(); // Pops the global scale matrix

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
    
    /**
     * Draws a filled rectangle.
     */
    private void drawFilledRect(DrawContext drawContext, int left, int top, int right, int bottom, int color) {
        drawContext.fill(left, top, right, bottom, color);
    }
}
