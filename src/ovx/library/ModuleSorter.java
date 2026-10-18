package ovx.library.internal;

import arc.util.Log;
import ovx.library.api.OvxModule;

import java.util.*;

public final class ModuleSorter {
    private ModuleSorter() {}

    public static List<OvxModule> sort(List<OvxModule> modules) {
        Map<String, OvxModule> byName = new LinkedHashMap<>();
        for (OvxModule m : modules) byName.put(m.name(), m);

        List<OvxModule> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();

        for (OvxModule m : modules) visit(m, byName, sorted, visited, visiting);
        return sorted;
    }

    private static void visit(OvxModule m, Map<String, OvxModule> byName,
                              List<OvxModule> sorted, Set<String> visited, Set<String> visiting) {
        if (visited.contains(m.name())) return;
        if (visiting.contains(m.name())) {
            Log.warn("[ovx/bootstrap] Dependency cycle at: " + m.name());
            return;
        }
        visiting.add(m.name());

        for (String dep : m.dependencies()) {
            OvxModule depModule = byName.get(dep);
            if (depModule == null) Log.warn("[ovx/bootstrap] Missing dep: " + dep + " (needed by " + m.name() + ")");
            else visit(depModule, byName, sorted, visited, visiting);
        }

        visiting.remove(m.name());
        visited.add(m.name());
        sorted.add(m);
    }
}