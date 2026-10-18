package ovx.library.devtools.ui;

import arc.Core;
import arc.scene.event.InputEvent;
import arc.scene.event.InputListener;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Table;
import mindustry.ui.Styles;

public class GearButton extends Table {
    private float dragX, dragY;
    private boolean dragged;

    public static void spawn() {
        if (Core.scene == null) return;
        // Avoid duplicate
        for (arc.scene.Element e : Core.scene.root.getChildren()) {
            if (e instanceof GearButton) return;
        }
        GearButton btn = new GearButton();
        btn.setPosition(
            Core.settings.getFloat("ovxdev-x", Core.graphics.getWidth() - 64),
            Core.settings.getFloat("ovxdev-y", Core.graphics.getHeight() - 64)
        );
        Core.scene.root.addChild(btn);
    }

    public GearButton() {
        setBackground(Styles.black6);
        setSize(48, 48);
        add(new Image(Core.atlas.find("settings"))).size(32).pad(8);

        addListener(new InputListener() {
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dragX = x; dragY = y; dragged = false; return true;
            }
            @Override public void touchDragged(InputEvent event, float x, float y, int pointer) {
                GearButton.this.moveBy(x - dragX, y - dragY);
                dragged = true;
            }
            @Override public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!dragged) DevToolsPanel.toggle();
                else {
                    Core.settings.put("ovxdev-x", GearButton.this.x);
                    Core.settings.put("ovxdev-y", GearButton.this.y);
                }
            }
        });
    }
}