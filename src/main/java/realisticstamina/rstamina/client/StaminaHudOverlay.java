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
    private static final int BAR_HEIGHT = 8;
    private static final int SPACING = 3;
    private static final int CORNER_RADIUS = 2;
    
    // Store previous values for smooth animation
    private static double lastStaminaPercentage = 1.0;
    private static double lastEnergyPercentage = 1.0;
    
    // Animation speed (lower = smoother but slower)
    private static final double ANIMATION_SPEED = 0.15;

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        int x = RStaminaMod.config.hudX;
        int y = RStaminaMod.config.hudY;

        MinecraftClient client = MinecraftClient.getInstance();

        if (client != null && RStaminaClient.showingStaminaTicks > 0) {
            float alpha = Math.min(1.0f, RStaminaClient.showingStaminaTicks / 20.0f);
            RenderSystem.enableBlend();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

            TextRenderer textRenderer = client.textRenderer;
            
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
            
            // Draw rounded stamina bar background
            drawRoundedRect(drawContext, x, y, x + BAR_WIDTH, y + BAR_HEIGHT, CORNER_RADIUS, 0x80000000);
            
            // Draw filled stamina bar
            int fillWidth = (int) (BAR_WIDTH * lastStaminaPercentage);
            if (fillWidth > 0) {
                drawRoundedRect(drawContext, x, y, x + fillWidth, y + BAR_HEIGHT, CORNER_RADIUS, staminaColor);
            }
            
            // Draw percentage inside the stamina bar
            String staminaPercentText = String.format("%d%% (%.0f)", (int)(lastStaminaPercentage * 100), maxStamina);
            int textWidth = textRenderer.getWidth(staminaPercentText);
            int textX = x + (BAR_WIDTH - textWidth) / 2;
            int textY = y + (BAR_HEIGHT - 7) / 2; // Center text vertically
            
            // Only hide the percentage if the bar is completely empty
            if (fillWidth > 0) {
                // Use a shadow for better visibility
                drawContext.drawText(textRenderer, staminaPercentText, textX + 1, textY + 1, 0xFF000000, false);
                drawContext.drawText(textRenderer, staminaPercentText, textX, textY, 0xFFFFFFFF, false);
            }
            
            // ----- DRAW ENERGY BAR -----
            
            // Draw energy bar below stamina bar
            int energyY = y + BAR_HEIGHT + SPACING;
            
            // Draw energy bar background
            drawRoundedRect(drawContext, x, energyY, x + BAR_WIDTH, energyY + BAR_HEIGHT, CORNER_RADIUS, 0x80000000);
            
            // Determine energy bar color
            int energyColor = 0xFF5B9BFF; // Light blue
            
            // Draw filled energy bar
            fillWidth = (int) (BAR_WIDTH * lastEnergyPercentage);
            if (fillWidth > 0) {
                drawRoundedRect(drawContext, x, energyY, x + fillWidth, energyY + BAR_HEIGHT, CORNER_RADIUS, energyColor);
            }
            
            // Draw percentage inside the energy bar
            String energyPercentText = String.format("%.1f%%", energy);
            textWidth = textRenderer.getWidth(energyPercentText);
            textX = x + (BAR_WIDTH - textWidth) / 2;
            textY = energyY + (BAR_HEIGHT - 7) / 2; // Center text vertically
            
            // Only hide the percentage if the bar is completely empty
            if (fillWidth > 0) {
                // Use a shadow for better visibility
                drawContext.drawText(textRenderer, energyPercentText, textX + 1, textY + 1, 0xFF000000, false);
                drawContext.drawText(textRenderer, energyPercentText, textX, textY, 0xFFFFFFFF, false);
            }
            
            // ----- SHOW DETAILED VALUES WHEN HOVERING -----
            
            // Get mouse position
            int mouseX = (int)(client.mouse.getX() * client.getWindow().getScaledWidth() / client.getWindow().getWidth());
            int mouseY = (int)(client.mouse.getY() * client.getWindow().getScaledHeight() / client.getWindow().getHeight());
            
            // Check if mouse is hovering over either bar
            boolean hoveringOverBars = mouseX >= x && mouseX <= x + BAR_WIDTH && 
                                      mouseY >= y && mouseY <= energyY + BAR_HEIGHT;
                                      
            if (hoveringOverBars) {
                // Show numeric values with small font when hovering
                String staminaValue = String.format("%.0f/%.0f", stamina, maxStamina);
                
                drawContext.drawTextWithShadow(textRenderer, 
                                              staminaValue, 
                                              x + BAR_WIDTH + 4, 
                                              y, 
                                              staminaColor);
            }
            
            // ----- DISPLAY SPEED MULTIPLIER -----
            
            // Only display speed multiplier when showSpeedMultiplierTicks is active
            if (RStaminaClient.showSpeedMultiplierTicks > 0) {
                // Calculate alpha for speed display based on remaining time
                float speedAlpha = Math.min(1.0f, RStaminaClient.showSpeedMultiplierTicks / 20.0f);
                
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, speedAlpha);
                // Only show speed if it's greater than 1.0
                if (RStaminaClient.clientStoredSpeedMultiplier > 1.01) {
                    String speedText = String.format("×%.1f", RStaminaClient.clientStoredSpeedMultiplier);
                    int speedX = x + 4;
                    int speedY = energyY + BAR_HEIGHT + SPACING;
                    drawContext.drawTextWithShadow(textRenderer, speedText, speedX, speedY, 0xFF59FFFF);
                }
            }

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.disableBlend();
        }
    }
    
    /**
     * Draws a rectangle with rounded corners
     */
    private void drawRoundedRect(DrawContext drawContext, int left, int top, int right, int bottom, int radius, int color) {
        // Main rectangle (excluding corners)
        drawContext.fill(left + radius, top, right - radius, bottom, color);
        drawContext.fill(left, top + radius, right, bottom - radius, color);
        
        // Draw corners
        fillCircleQuarter(drawContext, left + radius, top + radius, radius, 0, color);
        fillCircleQuarter(drawContext, right - radius, top + radius, radius, 1, color);
        fillCircleQuarter(drawContext, right - radius, bottom - radius, radius, 2, color);
        fillCircleQuarter(drawContext, left + radius, bottom - radius, radius, 3, color);
    }
    
    /**
     * Draws a quarter of a circle
     * @param quadrant 0=top-left, 1=top-right, 2=bottom-right, 3=bottom-left
     */
    private void fillCircleQuarter(DrawContext drawContext, int centerX, int centerY, int radius, int quadrant, int color) {
        for (int x = 0; x <= radius; x++) {
            for (int y = 0; y <= radius; y++) {
                // Check if point is inside circle
                if (x*x + y*y <= radius*radius) {
                    // Adjust coordinates based on quadrant
                    int drawX = centerX + (quadrant == 1 || quadrant == 2 ? x : -x);
                    int drawY = centerY + (quadrant == 2 || quadrant == 3 ? y : -y);
                    
                    drawContext.fill(drawX, drawY, drawX + 1, drawY + 1, color);
                }
            }
        }
    }
}
