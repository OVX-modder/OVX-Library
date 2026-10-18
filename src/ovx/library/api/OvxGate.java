package ovx.library.api;

import mindustry.Vars;
import mindustry.game.Gamemode;

public final class OvxGate {
    private OvxGate() {}

    public static boolean enabled() {
        if (Vars.headless) return false;
        if (Vars.state == null || !Vars.state.isGame()) return false;
        if (Vars.net.client()) return false;
        // ⚠️ VERIFY: mode() vs mode — check v8 source
        return Vars.state.rules.mode() == Gamemode.sandbox;
    }
}