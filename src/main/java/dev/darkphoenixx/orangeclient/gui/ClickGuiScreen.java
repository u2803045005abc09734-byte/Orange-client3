package dev.darkphoenixx.orangeclient.gui;

import dev.darkphoenixx.orangeclient.module.ClientModule;
import dev.darkphoenixx.orangeclient.module.ModuleRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public final class ClickGuiScreen extends Screen {
    private static final int BACKGROUND = 0xF20D1016;
    private static final int PANEL = 0xF2181D27;
    private static final int PANEL_ALT = 0xF2222834;
    private static final int ORANGE = 0xFFFF8A24;
    private static final int TEXT = 0xFFF2F4F8;
    private static final int MUTED = 0xFF9AA4B2;

    private int panelX;
    private int panelY;
    private int panelWidth = 430;
    private int panelHeight = 300;
    private int scroll;

    public ClickGuiScreen() {
        super(Text.literal("Orange Client"));
    }

    @Override
    protected void init() {
        panelX = (width - panelWidth) / 2;
        panelY = (height - panelHeight) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, 0x99000000);
        context.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, BACKGROUND);
        context.fill(panelX, panelY, panelX + 3, panelY + panelHeight, ORANGE);
        context.fill(panelX + 16, panelY + 16, panelX + panelWidth - 16, panelY + 56, PANEL);
        context.drawText(textRenderer, "ORANGE CLIENT", panelX + 28, panelY + 25, TEXT, false);
        context.drawText(textRenderer, "CLIENT SETTINGS", panelX + 28, panelY + 41, MUTED, false);

        int top = panelY + 70 - scroll;
        int index = 0;
        for (ClientModule module : ModuleRegistry.all()) {
            int x = panelX + 16 + (index % 2) * 199;
            int y = top + (index / 2) * 42;
            if (y >= panelY + 66 && y + 34 <= panelY + panelHeight - 12) {
                context.fill(x, y, x + 190, y + 34, PANEL_ALT);
                context.drawText(textRenderer, module.name(), x + 10, y + 7, TEXT, false);
                context.drawText(textRenderer, module.enabled() ? "ON" : "OFF",
                        x + 157, y + 12, module.enabled() ? ORANGE : MUTED, false);
            }
            index++;
        }
        context.drawText(textRenderer, "Right Shift  •  Toggle modules  •  Esc  •  Close",
                panelX + 18, panelY + panelHeight - 18, MUTED, false);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int top = panelY + 70 - scroll;
            int index = 0;
            for (ClientModule module : ModuleRegistry.all()) {
                int x = panelX + 16 + (index % 2) * 199;
                int y = top + (index / 2) * 42;
                if (mouseX >= x && mouseX <= x + 190 && mouseY >= y && mouseY <= y + 34) {
                    module.toggle();
                    return true;
                }
                index++;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll = Math.max(0, Math.min(90, scroll - (int) (verticalAmount * 24)));
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
