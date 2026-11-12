package dev.darkphoenixx.orangeclient.hud;

import dev.darkphoenixx.orangeclient.OrangeClient;
import dev.darkphoenixx.orangeclient.module.ModuleRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

public final class OverlayHud {
    private static final int ORANGE = 0xFFFF8A24;
    private static final int TEXT = 0xFFF2F4F8;
    private static final int MUTED = 0xFF9AA4B2;
    private static final int PANEL = 0xB80D1016;
    private static final Deque<Long> LEFT_CLICKS = new ArrayDeque<>();
    private static final Deque<Long> RIGHT_CLICKS = new ArrayDeque<>();

    private static boolean previousLeft;
    private static boolean previousRight;

    private OverlayHud() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(OverlayHud::tick);
        HudElementRegistry.addLast(
                Identifier.of(OrangeClient.MOD_ID, "overlay_hud"),
                (context, tickCounter) -> render(context)
        );
    }

    private static void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            LEFT_CLICKS.clear();
            RIGHT_CLICKS.clear();
            previousLeft = false;
            previousRight = false;
            return;
        }

        long window = client.getWindow().getHandle();
        long now = System.currentTimeMillis();
        boolean left = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS;
        boolean right = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_2) == GLFW.GLFW_PRESS;

        if (left && !previousLeft) LEFT_CLICKS.addLast(now);
        if (right && !previousRight) RIGHT_CLICKS.addLast(now);
        previousLeft = left;
        previousRight = right;

        prune(LEFT_CLICKS, now);
        prune(RIGHT_CLICKS, now);
    }

    private static void prune(Deque<Long> clicks, long now) {
        while (!clicks.isEmpty() && now - clicks.peekFirst() > 1000L) {
            clicks.removeFirst();
        }
    }

    private static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.options.hudHidden) return;

        if (ModuleRegistry.isEnabled("CPS")) {
            renderCps(context, client);
        }
        if (ModuleRegistry.isEnabled("Keystrokes")) {
            renderKeystrokes(context, client);
        }
        if (ModuleRegistry.isEnabled("Armor HUD") || ModuleRegistry.isEnabled("Durability HUD")) {
            renderArmor(context, client);
        }
    }

    private static void renderCps(DrawContext context, MinecraftClient client) {
        int x = client.getWindow().getScaledWidth() - 112;
        context.fill(x, 10, x + 102, 34, PANEL);
        context.fill(x, 10, x + 2, 34, ORANGE);
        context.drawText(client.textRenderer,
                "L " + LEFT_CLICKS.size() + "   R " + RIGHT_CLICKS.size(),
                x + 9, 18, TEXT, false);
    }

    private static void renderKeystrokes(DrawContext context, MinecraftClient client) {
        int x = client.getWindow().getScaledWidth() - 98;
        int y = 46;
        boolean f = client.options.forwardKey.isPressed();
        boolean b = client.options.backKey.isPressed();
        boolean l = client.options.leftKey.isPressed();
        boolean r = client.options.rightKey.isPressed();
        key(context, client, "W", x + 32, y, f);
        key(context, client, "A", x, y + 26, l);
        key(context, client, "S", x + 32, y + 26, b);
        key(context, client, "D", x + 64, y + 26, r);
        key(context, client, "SPACE", x, y + 52, client.options.jumpKey.isPressed(), 96);
        key(context, client, "SHIFT", x, y + 78, client.options.sneakKey.isPressed(), 46);
        key(context, client, "CTRL", x + 50, y + 78, client.options.sprintKey.isPressed(), 46);
    }

    private static void key(DrawContext context, MinecraftClient client, String label,
                            int x, int y, boolean pressed) {
        key(context, client, label, x, y, pressed, 30);
    }

    private static void key(DrawContext context, MinecraftClient client, String label,
                            int x, int y, boolean pressed, int width) {
        int background = pressed ? 0xE5B84E18 : PANEL;
        context.fill(x, y, x + width, y + 22, background);
        context.drawText(client.textRenderer, label,
                x + (width - client.textRenderer.getWidth(label)) / 2,
                y + 7, pressed ? 0xFFFFFFFF : MUTED, false);
    }

    private static void renderArmor(DrawContext context, MinecraftClient client) {
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };
        String[] labels = {"HELMET", "CHEST", "LEGS", "BOOTS"};
        int x = 10;
        int y = client.getWindow().getScaledHeight() - 86;
        context.fill(x, y, x + 112, y + 76, PANEL);
        context.fill(x, y, x + 2, y + 76, ORANGE);

        for (int i = 0; i < slots.length; i++) {
            ItemStack stack = client.player.getEquippedStack(slots[i]);
            String value;
            int color = MUTED;
            if (stack.isEmpty()) {
                value = labels[i] + "  --";
            } else if (stack.getMaxDamage() > 0) {
                int remaining = Math.max(0, stack.getMaxDamage() - stack.getDamage());
                int percent = Math.round(remaining * 100.0f / stack.getMaxDamage());
                value = labels[i] + "  " + percent + "%";
                color = percent <= 20 ? 0xFFFF6B6B : TEXT;
            } else {
                value = labels[i] + "  OK";
                color = TEXT;
            }
            context.drawText(client.textRenderer, value, x + 9, y + 7 + i * 16, color, false);
        }
    }
}
