package ovx.library.api;

import java.util.ArrayList;
import java.util.List;

public final class OvxBootstrap {
    private static final List<OvxModule> modules = new ArrayList<>();
    private static boolean initialized = false;

    private OvxBootstrap() {}

    public static void register(OvxModule module) {
        if (initialized) return;
        modules.add(module);
    }

    public static void init() {
        if (initialized) return;
        initialized = true;
        if (modules.isEmpty()) return;

        for (OvxModule m : modules) {
            try { m.onInit(); } catch (Throwable t) { OvxLog.err("bootstrap", "Module failed: " + t); }
        }
        for (OvxModule m : modules) {
            try { m.onPostInit(); } catch (Throwable t) { OvxLog.err("bootstrap", "Module failed: " + t); }
        }
    }
}
