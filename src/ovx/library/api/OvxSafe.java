package ovx.library.api;

import arc.Core;
import arc.Events;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.game.EventType.WorldLoadEvent;

public final class OvxSafe {
    private OvxSafe() {}

    public static <T> T cast(Class<T> type, Object o) { return type.isInstance(o) ? type.cast(o) : null; }
    public static boolean isInWorld() { return Vars.state.isGame() && Vars.world != null; }
    public static void onMainThread(Runnable r) { Core.app.post(r); }

    public static void whenLoaded(Runnable r) {
        if (isInWorld()) { r.run(); return; }
        Events.on(WorldLoadEvent.class, e -> r.run());
    }

    public static void schedule(float delay, Runnable r) {
        Timer.Task task = Timer.schedule(r, delay);
        Events.on(WorldLoadEvent.class, e -> task.cancel());
    }

    public static void scheduleRepeating(float interval, Runnable r) {
        Timer.Task task = Timer.schedule(r, interval, interval);
        Events.on(WorldLoadEvent.class, e -> task.cancel());
    }
}
