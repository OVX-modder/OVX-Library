package ovx.library;

import mindustry.mod.Mod;
import ovx.library.api.OvxBootstrap;
import ovx.library.api.OvxLog;
import ovx.library.devtools.DevTools;

public class OvxLibrary extends Mod {
    @Override
    public void init() {
        OvxLog.info("lib", "ovx-library v1.0 loading");
        OvxBootstrap.init();
        DevTools.init();
    }

    @Override
    public void loadContent() {
        // Intentionally empty.
    }
}