package ovx.library.api;

import java.util.List;

public interface OvxModule {
    String name();
    default List<String> dependencies() { return List.of(); }
    void onInit();
    default void onPostInit() {}
}