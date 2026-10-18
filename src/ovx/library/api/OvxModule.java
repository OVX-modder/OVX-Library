package ovx.library.api;

import java.util.Collections;
import java.util.List;

public interface OvxModule {
    String name();
    default List<String> dependencies() { return Collections.emptyList(); }
    void onInit();
    default void onPostInit() {}
}
