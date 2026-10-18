package ovx.library.devtools;

import arc.Events;
import mindustry.game.EventType.WorldLoadEvent;
import ovx.library.api.OvxGate;
import ovx.library.devtools.ui.GearButton;

public final class DevTools {
    private DevTools() {}

    public static void init() {
        Events.on(WorldLoadEvent.class, e -> {
            if (OvxGate.enabled()) GearButton.spawn();
        });
    }
}