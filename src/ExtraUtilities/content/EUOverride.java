package ExtraUtilities.content;

import ExtraUtilities.worlds.entity.bullet.CtrlMissile;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.entities.part.FlarePart;
import mindustry.entities.part.ShapePart;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.BaseTurret;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.units.UnitFactory;

import static ExtraUtilities.ExtraUtilitiesMod.name;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.type.ItemStack.with;

public class EUOverride {
    public static void overrideBlockAll(){
        for(int i = 0; i < Vars.content.blocks().size; i ++){
            Block block = Vars.content.blocks().get(i);
            if(block instanceof Turret && block.size >= 5){
                boolean has = false;
                for(ItemStack stack : block.requirements){
                    if(stack.item == EUItems.lightninAlloy){
                        has = true;
                        break;
                    }
                }
                if(has) continue;
                ItemStack[] copy = new ItemStack[block.requirements.length + 1];
                System.arraycopy(block.requirements, 0, copy, 0, block.requirements.length);
                copy[block.requirements.length] = new ItemStack(EUItems.lightninAlloy, 50 + 50 * (block.size - 5));
                block.requirements = copy;
            }
        }
    }

    public static void overrideBlock1(){
        Blocks.sandWater.itemDrop = Items.sand;
        Blocks.sandWater.playerUnmineable = true;
        Blocks.darksandWater.itemDrop = Items.sand;
        Blocks.darksandWater.playerUnmineable = true;
        Blocks.darksandTaintedWater.itemDrop = Items.sand;
        Blocks.darksandTaintedWater.playerUnmineable = true;
        Blocks.oxidationChamber.canOverdrive = true;
        Blocks.neoplasiaReactor.canOverdrive = true;

        Blocks.arc.consumePower(2f);
        Blocks.smite.requirements(Category.turret, with(Items.oxide, 200, Items.surgeAlloy, 400, Items.silicon, 800, Items.carbide, 500, Items.phaseFabric, 300, EUItems.lightninAlloy, 120));
        Blocks.malign.requirements(Category.turret, with(Items.carbide, 400, Items.beryllium, 2000, Items.silicon, 800, Items.graphite, 800, Items.phaseFabric, 300, EUItems.lightninAlloy, 100));
        ((BaseTurret)Blocks.scathe).fogRadiusMultiplier = 0.75f;

        ((UnitFactory)Blocks.airFactory).plans.add(new UnitFactory.UnitPlan(EUUnitTypes.winglet, 60f * 30, with(Items.silicon, 20, Items.titanium, 10, Items.lead, 15)));
    }

    public static void overrideUnit1(){
        UnitTypes.corvus.mineTier = 2;
        UnitTypes.corvus.mineSpeed = 7;
        UnitTypes.corvus.buildSpeed = 2;

        UnitTypes.quell.health = 6500;
        UnitTypes.quell.armor = 7;
        UnitTypes.quell.weapons.get(0).bullet = new CtrlMissile("quell-missile", -1, -1){{//
            shootEffect = Fx.shootBig;
            smokeEffect = Fx.shootBigSmoke2;
            speed = 4.3f;
            keepVelocity = false;
            maxRange = 6f;
            lifetime = 60f * 1.6f;
            damage = 110;
            splashDamage = 110;
            splashDamageRadius = 25;
            buildingDamageMultiplier = 0.5f;
            hitEffect = despawnEffect = Fx.massiveExplosion;
            trailColor = Pal.sapBulletBack;
        }};
        UnitTypes.quell.weapons.get(0).shake = 1;

        UnitTypes.disrupt.weapons.get(0).bullet = new CtrlMissile("disrupt-missile", -1, -1){{//
            shootEffect = Fx.sparkShoot;
            smokeEffect = Fx.shootSmokeTitan;
            hitColor = Pal.suppress;
            maxRange = 5f;
            speed = 4.6f;
            keepVelocity = false;
            homingDelay = 10f;
            trailColor = Pal.sapBulletBack;
            trailLength = 8;
            hitEffect = despawnEffect = new ExplosionEffect(){{
                lifetime = 50f;
                waveStroke = 5f;
                waveLife = 8f;
                waveColor = Color.white;
                sparkColor = smokeColor = Pal.suppress;
                waveRad = 40f;
                smokeSize = 4f;
                smokes = 7;
                smokeSizeBase = 0f;
                sparks = 10;
                sparkRad = 40f;
                sparkLen = 6f;
                sparkStroke = 2f;
            }};
            damage = 150;
            splashDamage = 150;
            splashDamageRadius = 25;
            buildingDamageMultiplier = 0.5f;

            parts.add(new ShapePart(){{
                layer = Layer.effect;
                circle = true;
                y = -3.5f;
                radius = 1.6f;
                color = Pal.suppress;
                colorTo = Color.white;
                progress = PartProgress.life.curve(Interp.pow5In);
            }});
        }};
        UnitTypes.disrupt.weapons.get(0).shake = 1f;

        UnitTypes.anthicus.weapons.get(0).bullet = new CtrlMissile("anthicus-missile", -1, -1){{//
            shootEffect = new MultiEffect(Fx.shootBigColor, new Effect(9, e -> {
                color(Color.white, e.color, e.fin());
                stroke(0.7f + e.fout());
                Lines.square(e.x, e.y, e.fin() * 5f, e.rotation + 45f);

                Drawf.light(e.x, e.y, 23f, e.color, e.fout() * 0.7f);
            }), new WaveEffect(){{
                colorFrom = colorTo = Pal.techBlue;
                sizeTo = 15f;
                lifetime = 12f;
                strokeFrom = 3f;
            }});

            smokeEffect = Fx.shootBigSmoke2;
            speed = 3.7f;
            keepVelocity = false;
            inaccuracy = 2f;
            maxRange = 6;
            trailWidth = 2;
            trailColor = Pal.techBlue;
            low = true;
            absorbable = true;

            damage = 110;
            splashDamage = 110;
            splashDamageRadius = 25;
            buildingDamageMultiplier = 0.8f;

            despawnEffect = hitEffect = new MultiEffect(Fx.massiveExplosion, new WrapEffect(Fx.dynamicSpikes, Pal.techBlue, 24f), new WaveEffect(){{
                colorFrom = colorTo = Pal.techBlue;
                sizeTo = 40f;
                lifetime = 12f;
                strokeFrom = 4f;
            }});

            parts.add(new FlarePart(){{
                progress = PartProgress.life.slope().curve(Interp.pow2In);
                radius = 0f;
                radiusTo = 35f;
                stroke = 3f;
                rotation = 45f;
                y = -5f;
                followRotation = true;
            }});
        }};
        UnitTypes.anthicus.weapons.get(0).shake = 2;
        UnitTypes.anthicus.weapons.get(0).reload = 120;

        UnitTypes.tecta.weapons.get(0).bullet.damage = 0;
        UnitTypes.tecta.weapons.get(0).bullet.splashDamage = 95;
        UnitTypes.tecta.health = 9000;
    }

    public static void overrideBuilder(){
        for(int i = 0; i < Vars.content.units().size; i++){
            UnitType u = Vars.content.unit(i);
            if(u != null && u.buildSpeed > 0){
                StatusEffect s = Vars.content.statusEffect("new-horizon-scanner-down");
                if(s != null) u.immunities.add(s);
            }
        }
    }

    public static void overrideAmr(){
        for(int i = 0; i < Vars.content.units().size; i++){
            UnitType u = Vars.content.unit(i);
            if(u != null){
                u.armor = Math.min(u.armor, 80);
            }
        }
    }

    //愚人节特别改动
    public static void ap4sOverride(){
        for(int i = 0; i < Vars.content.blocks().size; i++){
            Block b = Vars.content.block(i);
            if(b != null){
                String n = b.description;
                if(n == null) continue;
                int d = Mathf.random(Vars.content.blocks().size - 1);
                if(d == i) continue;
                Block b1 = Vars.content.block(d);
                if(b1 != null){
                    if(b1.description == null) continue;
                    b.description = b1.description;
                    b1.description = n;
                }
            }
        }
        for(int i = 0; i < Vars.content.items().size; i++){
            Item b = Vars.content.item(i);
            if(b != null){
                String n = b.description;
                if(n == null) continue;
                int d = Mathf.random(Vars.content.items().size - 1);
                if(d == i) continue;
                Item b1 = Vars.content.item(d);
                if(b1 != null){
                    if(b1.description == null) continue;
                    b.description = b1.description;
                    b1.description = n;
                }
            }
        }
        for(int i = 0; i < Vars.content.units().size; i++){
            UnitType b = Vars.content.unit(i);
            if(b != null){
                String n = b.description;
                if(n == null) continue;
                int d = Mathf.random(Vars.content.units().size - 1);
                if(d == i) continue;
                UnitType b1 = Vars.content.unit(d);
                if(b1 != null){
                    if(b1.description == null) continue;
                    b.description = b1.description;
                    b1.description = n;
                }
            }
        }
    }
}
