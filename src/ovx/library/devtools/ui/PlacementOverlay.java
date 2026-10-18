package ovx.library.devtools.ui;

import arc.input.KeyCode;
import arc.scene.Element;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.event.Touchable;

public class PlacementOverlay extends Element {
    public PlacementOverlay(Runnable onPlace, Runnable onCancel) {
        setFillParent(true);
        setTouchable(Touchable.enabled);

        addListener(new InputListener() {
            // v8 uses KeyCode for the button parameter
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button) {
                if (button == KeyCode.mouseLeft) { onPlace.run(); remove(); return true; }
                if (button == KeyCode.mouseRight) { onCancel.run(); remove(); return true; }
                return false;
            }
            @Override public boolean keyDown(InputEvent event, KeyCode keycode) {
                if (keycode == KeyCode.escape) { onCancel.run(); remove(); return true; }
                return false;
            }
        });
    }
}
