package ovx.library.devtools.ui;

import arc.input.Input;
import arc.scene.Element;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;

public class PlacementOverlay extends Element {
    public PlacementOverlay(Runnable onPlace, Runnable onCancel) {
        setFillParent(true);
        setTouchable(Touchable.enabled);

        addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (button == Input.Buttons.left) { onPlace.run(); remove(); return true; }
                if (button == Input.Buttons.right) { onCancel.run(); remove(); return true; }
                return false;
            }
            @Override public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) { onCancel.run(); remove(); return true; }
                return false;
            }
        });
    }
}