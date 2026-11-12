package dev.darkphoenixx.orangeclient;

import dev.darkphoenixx.orangeclient.gui.ClickGuiScreen;
import dev.darkphoenixx.orangeclient.hud.OverlayHud;
import dev.darkphoenixx.orangeclient.module.ModuleRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class OrangeClient implements ClientModInitializer {
    public static final String MOD_ID = "orange_client";
    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ModuleRegistry.registerDefaults();
        OverlayHud.register();
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.orange_client.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.player != null && client.world != null
                        && client.currentScreen == null) {
                    client.setScreen(new ClickGuiScreen());
                }
            }
        });
    }
}
