# Orange Client

A client-side customization and utility mod for Minecraft **1.21.11**, Fabric, Java 21.

## Current foundation
- Right Shift opens the ClickGUI while in a loaded world.
- Dark interface with orange accent.
- Basic module registry and toggle interaction.
- GitHub Actions build workflow.

## Planned modules
HUD, CPS, keystrokes, armor/durability HUD, item scale controls, smooth zoom, crosshair and block-outline customization, client-side time/weather visuals, and performance settings.

Integrations such as Media Player, cape browsing/editing, and Modrinth resource-pack browsing will be added as separate screens after the core build is verified.

## Build
Use JDK 21 and run:

```sh
gradle build
```

The resulting mod JAR is in `build/libs`.

Only client-side visual and informational features are in scope. No automated combat, reach modification, anti-cheat bypass, or packet manipulation is included.
