package ExtraUtilities.worlds.blocks.unit;

import arc.graphics.g2d.Draw;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.*;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.game.Team;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.PayloadSource;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

public class ADCPayloadSource extends PayloadSource {
    private final Team[] teams = new Team[]{Team.derelict, Team.sharded, Team.crux, Team.green, Team.malis, Team.blue};
    public ADCPayloadSource(String name) {
        super(name);
        noUpdateDisabled = false;
        unitCapModifier = 1;
        targetable = false;
        underBullets = true;
        destructible = false;

        config(Block.class, (ADCPayloadSourceBuild build, Block block) -> {
            if(canProduce(block) && build.block != block){
                build.block = block;
                build.unit = null;
                build.payload = null;
                build.scl = 0f;
            }
        });

        config(UnitType.class, (ADCPayloadSourceBuild build, UnitType unit) -> {
            if(canProduce(unit) && build.unit != unit){
                build.unit = unit;
                build.block = null;
                build.payload = null;
                build.scl = 0f;
            }
        });

        config(Integer.class, (ADCPayloadSourceBuild build, Integer i) -> {
            build.unit = null;
            build.block = null;
            build.payload = null;
            build.scl = 0;
        });

        configClear((ADCPayloadSourceBuild build) -> {
            build.block = null;
            build.unit = null;
            build.payload = null;
            build.scl = 0f;
        });
    }

    @Override
    public boolean canProduce(Block b) {
        return b.isVisible() && !(b instanceof CoreBlock) && !state.rules.isBanned(b) && b.environmentBuildable();
    }

    public class ADCPayloadSourceBuild extends PayloadSourceBuild{

        @Override
        public void updateTile() {
            enabled = true;
            super.updateTile();
        }

        @Override
        public void buildConfiguration(Table table){
            ButtonGroup<ImageButton> g = new ButtonGroup<>();
            Table cont = new Table();
            cont.defaults().size(55);
            int i = 0;
            for(; i < teams.length; i++){
                Team team1 = teams[i];
                ImageButton button = cont.button(((TextureRegionDrawable)Tex.whiteui).tint(team1.color), Styles.clearTogglei, 35, () -> {}).group(g).get();
                button.changed(() -> {
                    if(button.isChecked()) {
                        if(Vars.player.team() == team){
                            configure(team1.id);
                        } else deselect();
                    }
                });
                button.update(() -> button.setChecked(team == team1));
            }
            table.add(cont).maxHeight(Scl.scl(55 * 2)).left();
            table.row();
            ItemSelection.buildTable(ADCPayloadSource.this, table,
                    content.blocks().select(ADCPayloadSource.this::canProduce).<UnlockableContent>as()
                            .add(content.units().select(ADCPayloadSource.this::canProduce).as()),
                    () -> (UnlockableContent)config(), this::configure, false, selectionRows, selectionColumns);
        }

        @Override
        public void configure(Object value) {
            if(Vars.player.team() == team) super.configure(value);
            else deselect();
        }

        @Override
        public void configured(Unit builder, Object value) {
            super.configured(builder, value);
            if(!(value instanceof Integer)) return;
            if (builder != null && builder.isPlayer()) {
                Team team = Team.get((int)value);
                builder.team = team;
                builder.getPlayer().team(team);

                onRemoved();
                changeTeam(team);
                onProximityUpdate();
            }
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            Draw.rect(outRegion, x, y, rotdeg());
            Draw.color(team.color);
            Draw.rect(teamRegion, x, y);
            Draw.color();
            Draw.scl(scl);
            drawPayload();
            Draw.reset();
        }

        @Override
        public void damage(float damage) {

        }

        @Override
        public boolean canPickup() {
            return false;
        }
    }
}
