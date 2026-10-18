package ovx.library.devtools.ui;

import arc.input.Buttons;
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
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Buttons.left) { onPlace.run(); remove(); return true; }
                if (button == Buttons.right) { onCancel.run(); remove(); return true; }
                return false;
            }
            @Override public boolean keyDown(InputEvent event, KeyCode keycode) {
                if (keycode == KeyCode.escape) { onCancel.run(); remove(); return true; }
                return false;
            }
        });
    }
}
