package ovx.library.api;

import java.util.*;

public final class OvxBootstrap {
    private static final List<OvxModule> modules = new ArrayList<>();
    private static boolean initialized = false;

    private OvxBootstrap() {}

    public static void register(OvxModule module) {
        if (initialized) {
            throw new IllegalStateException(
                "[ovx/bootstrap] Cannot register '" + module.name() +
                "' after bootstrap init. Register modules in Mod#init(), " +
                "and ensure '!ovx-library' is in your mod.hjson dependencies.");
        }
        modules.add(module);
        OvxLog.info("bootstrap", "Registered module: " + module.name());
    }

    public static void init() {
        if (initialized) return;
        initialized = true;

        if (modules.isEmpty()) {
            OvxLog.info("bootstrap", "No modules registered.");
            return;
        }

        List<OvxModule> sorted = ovx.library.internal.ModuleSorter.sort(modules);

        OvxLog.info("bootstrap", "Phase 1: onInit (" + sorted.size() + " modules)");
        for (OvxModule m : sorted) {
            try { m.onInit(); }
            catch (Throwable t) { OvxLog.err("bootstrap", "Module '" + m.name() + "' failed onInit: " + t); }
        }

        OvxLog.info("bootstrap", "Phase 2: onPostInit");
        for (OvxModule m : sorted) {
            try { m.onPostInit(); }
            catch (Throwable t) { OvxLog.err("bootstrap", "Module '" + m.name() + "' failed onPostInit: " + t); }
        }

        OvxLog.info("bootstrap", "Bootstrap complete.");
    }

    public static boolean isInitialized() { return initialized; }
}