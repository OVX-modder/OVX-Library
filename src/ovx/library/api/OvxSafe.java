package ovx.library.api;

import arc.Core;
import arc.Events;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.game.EventType.WorldLoadEvent;

public final class OvxSafe {
    private OvxSafe() {}

    public static <T> T cast(Class<T> type, Object o) {
        return type.isInstance(o) ? type.cast(o) : null;
    }

    public static <T> T require(Class<T> type, Object o) {
        if (o == null) throw new IllegalArgumentException("Expected " + type.getSimpleName() + " but got null");
        if (!type.isInstance(o)) throw new IllegalArgumentException(
            "Expected " + type.getSimpleName() + " but got " + o.getClass().getSimpleName());
        return type.cast(o);
    }

    public static boolean isInWorld() { return Vars.state.isGame() && Vars.world != null; }
    public static boolean isGameActive() { return Vars.state.isPlaying(); }
    public static boolean isMultiplayer() { return Vars.net.client() || Vars.net.server(); }
    public static boolean isServer() { return Vars.net.server(); }
    public static boolean isClient() { return Vars.net.client(); }

    public static void whenLoaded(Runnable r) {
        if (isInWorld()) { r.run(); return; }
        Events.once(WorldLoadEvent.class, e -> r.run());
    }

    /** Always defers to main thread via Core.app.post — never calls isMainThread(). */
    public static void onMainThread(Runnable r) { Core.app.post(r); }

    public static void schedule(float delay, Runnable r) {
        Timer.Task task = Timer.schedule(r, delay);
        Events.once(WorldLoadEvent.class, e -> task.cancel());
    }

    public static void scheduleRepeating(float interval, Runnable r) {
        Timer.Task task = Timer.schedule(r, interval, interval);
        Events.once(WorldLoadEvent.class, e -> task.cancel());
    }
}