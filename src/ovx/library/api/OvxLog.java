package ovx.library.api;

import arc.util.Log;

public final class OvxLog {
    private OvxLog() {}

    public static void info(String tag, String msg) { Log.info("[ovx/" + tag + "] " + msg); }
    public static void warn(String tag, String msg) { Log.warn("[ovx/" + tag + "] " + msg); }
    public static void err (String tag, String msg) { Log.err ("[ovx/" + tag + "] " + msg); }
}