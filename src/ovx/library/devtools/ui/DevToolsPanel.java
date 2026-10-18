package ovx.library.devtools.ui;

import arc.Core;
import arc.graphics.Color;
import arc.math.geom.Vec2;
import arc.scene.ui.*;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;
import ovx.library.api.OvxGate;
import ovx.library.api.OvxLog;

import java.util.*;

public class DevToolsPanel extends Table {
    private static DevToolsPanel current;

    private TeamKind selectedTeam = TeamKind.SHARDED;
    private TabKind tab = TabKind.SPAWN;
    private SortMode sortMode = SortMode.ALPHA;
    private String search = "";

    private Table listTable;

    public static void toggle() {
        if (current != null) { current.remove(); current = null; return; }
        if (!OvxGate.enabled()) return;
        current = new DevToolsPanel();
        current.setFillParent(true);
        Core.scene.root.addChild(current);
    }

    private void rebuild() {
        clear();
        buildHeader(); row();
        buildTeamRow(); row();
        buildTabs(); row();
        buildSearchAndSort(); row();
        buildList(); row();
        buildActionButton();
    }

    private void removeSelf() { current = null; remove(); }

    private void buildHeader() {
        Table header = new Table();
        header.add("OVX DevTools").expandX().left().pad(8);
        TextButton close = new TextButton("X", Styles.defaultt);
        close.clicked(this::removeSelf);
        header.add(close).right().pad(8);
        add(header).growX();
    }

    private void buildTeamRow() {
        Table teams = new Table();
        teams.add("Team: ").pad(4);
        for (TeamKind t : TeamKind.values()) {
            TextButton b = new TextButton(t.label, Styles.defaultt);
            b.clicked(() -> { selectedTeam = t; rebuild(); });
            if (t == selectedTeam) b.setColor(t.color);
            teams.add(b).pad(4);
        }
        add(teams).left().pad(8);
    }

    private void buildTabs() {
        Table tabs = new Table();
        for (TabKind t : TabKind.values()) {
            TextButton b = new TextButton(t.label, Styles.defaultt);
            b.clicked(() -> { tab = t; rebuild(); });
            if (t == tab) b.setColor(Color.green);
            tabs.add(b).pad(4);
        }
        add(tabs).left().pad(8);
    }

    private void buildSearchAndSort() {
        Table bar = new Table();
        // Fixed to Styles.defaultField
        TextField searchField = new TextField(search, Styles.defaultField);
        searchField.setMessageText("Search...");
        searchField.changed(() -> { search = searchField.getText().toLowerCase(); rebuildList(); });
        bar.add(searchField).growX().pad(4);

        TextButton sortBtn = new TextButton(sortMode.label, Styles.defaultt);
        sortBtn.clicked(() -> { sortMode = sortMode.next(); rebuildList(); sortBtn.setText(sortMode.label); });
        bar.add(sortBtn).width(120).pad(4);
        add(bar).growX().pad(8);
    }

    private void buildList() {
        listTable = new Table();
        // Fixed to Styles.defaultPane
        ScrollPane pane = new ScrollPane(listTable, Styles.defaultPane);
        pane.setFadeScrollBars(false);
        add(pane).grow().pad(8);
        rebuildList();
    }

    private void rebuildList() {
        if (listTable == null) return;
        listTable.clear();

        if (tab == TabKind.KILL) {
            listTable.add("Pick a team, then press Done.").pad(8);
            return;
        }

        if (tab == TabKind.SPAWN) {
            List<UnitType> units = new ArrayList<>();
            for (UnitType u : Vars.content.units()) {
                if (u.isHidden()) continue;
                if (!search.isEmpty() && !u.name.toLowerCase().contains(search)) continue;
                units.add(u);
            }
            units.sort(sortMode.comparator());
            for (UnitType u : units) {
                TextButton row = new TextButton(u.localizedName, Styles.defaultt);
                row.clicked(() -> beginUnitPlacement(u, selectedTeam.team));
                listTable.add(row).growX().pad(2).row();
            }
        } else if (tab == TabKind.CORE) {
            List<Block> cores = new ArrayList<>();
            for (Block b : Vars.content.blocks()) {
                if (!(b instanceof CoreBlock)) continue;
                if (!search.isEmpty() && !b.name.toLowerCase().contains(search)) continue;
                cores.add(b);
            }
            cores.sort(Comparator.comparing(b -> b.name));
            for (Block b : cores) {
                TextButton row = new TextButton(b.localizedName, Styles.defaultt);
                row.clicked(() -> beginCorePlacement(b, selectedTeam.team));
                listTable.add(row).growX().pad(2).row();
            }
        }
    }

    private void buildActionButton() {
        Table bar = new Table();
        if (tab == TabKind.KILL) {
            TextButton done = new TextButton("Done - Kill all " + selectedTeam.label, Styles.defaultt);
            done.clicked(this::killTeam);
            bar.add(done).growX().pad(8);
        } else {
            bar.add("Click an entry above to place.").pad(8);
        }
        add(bar).growX();
    }

    private void beginUnitPlacement(UnitType type, Team team) {
        removeSelf();
        OvxLog.info("devtools", "Placing " + type.name + " for " + team.name);
        Core.scene.root.addChild(new PlacementOverlay(
            () -> {
                Vec2 w = Core.camera.unproject(Core.input.mouseX(), Core.input.mouseY());
                type.spawn(team, w.x, w.y);
            },
            () -> OvxLog.info("devtools", "Cancelled")
        ));
    }

    private void beginCorePlacement(Block core, Team team) {
        removeSelf();
        OvxLog.info("devtools", "Placing core " + core.name + " for " + team.name);
        Core.scene.root.addChild(new PlacementOverlay(
            () -> {
                Vec2 w = Core.camera.unproject(Core.input.mouseX(), Core.input.mouseY());
                Tile tile = Vars.world.tileWorld(w.x, w.y);
                if (tile != null) tile.setBlock(core, team, 0);
            },
            () -> OvxLog.info("devtools", "Cancelled")
        ));
    }

    private void killTeam() {
        List<Unit> kill = new ArrayList<>();
        Groups.unit.each(u -> { if (u.team == selectedTeam.team) kill.add(u); });
        for (Unit u : kill) u.kill();
        OvxLog.info("devtools", "Killed " + kill.size() + " of team " + selectedTeam.team.name);
        removeSelf();
    }

    enum TabKind {
        SPAWN("Spawn Unit"), CORE("Place Core"), KILL("Kill Units");
        final String label; TabKind(String l) { label = l; }
    }

    enum SortMode {
        ALPHA("A-Z"), MODS("Mods"), TIER("Tier");
        final String label; SortMode(String l) { label = l; }
        SortMode next() { return values()[(ordinal() + 1) % values().length]; }
        Comparator<UnitType> comparator() {
            return switch (this) {
                case ALPHA -> Comparator.comparing(u -> u.localizedName);
                case MODS  -> Comparator.comparing(u -> u.name);
                case TIER  -> Comparator.comparing(u -> u.name);
            };
        }
    }

    enum TeamKind {
        SHARDED("Sharded", Team.sharded, Color.valueOf("ffd37f")),
        CRUX   ("Crux",    Team.crux,    Color.valueOf("ff7d7d")),
        DERELICT("Derelict", Team.derelict, Color.valueOf("aa88cc")),
        GREEN  ("Green",   Team.green,   Color.valueOf("7dff7d")),
        BLUE   ("Blue",    Team.blue,    Color.valueOf("7d7dff"));
        final String label; final Team team; final Color color;
        TeamKind(String l, Team t, Color c) { label=l; team=t; color=c; }
    }
}
