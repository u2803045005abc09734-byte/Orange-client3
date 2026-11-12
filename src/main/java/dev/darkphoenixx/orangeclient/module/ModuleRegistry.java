package dev.darkphoenixx.orangeclient.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ModuleRegistry {
    private static final List<ClientModule> MODULES = new ArrayList<>();
    private static boolean registered;

    private ModuleRegistry() {}

    public static void registerDefaults() {
        if (registered) return;
        registered = true;
        MODULES.add(new ClientModule("Motion Blur", "Client-side motion blur effect", false));
        MODULES.add(new ClientModule("CPS", "Displays left and right clicks per second", true));
        MODULES.add(new ClientModule("Keystrokes", "Displays WASD, Space and Shift input", true));
        MODULES.add(new ClientModule("Armor HUD", "Displays equipped item durability", true));
        MODULES.add(new ClientModule("Item Scale", "Customize held, dropped and offhand item size", false));
        MODULES.add(new ClientModule("Smooth Zoom", "Configurable smooth client-side zoom", false));
        MODULES.add(new ClientModule("Custom Crosshair", "Customize the crosshair appearance", false));
        MODULES.add(new ClientModule("Block Outline", "Customize targeted block outline visuals", false));
        MODULES.add(new ClientModule("Time & Weather", "Client-side time and weather visuals", false));
        MODULES.add(new ClientModule("Direction HUD", "Displays facing direction", false));
        MODULES.add(new ClientModule("Durability HUD", "Shows selected equipment durability", false));
        MODULES.add(new ClientModule("Optimizer", "Client-side performance preferences", false));
    }

    public static List<ClientModule> all() {
        return Collections.unmodifiableList(MODULES);
    }

    public static boolean isEnabled(String name) {
        return MODULES.stream().anyMatch(module -> module.name().equals(name) && module.enabled());
    }
}
