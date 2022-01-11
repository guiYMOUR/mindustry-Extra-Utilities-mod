const crafter = require("other/MultiCrafter");
const crusher = crafter.MultiCrafter(GenericCrafter, GenericCrafter.GenericCrafterBuild, "crusher", [
    {
        input: {
            items: ["copper/2"],
            power: 2,
        },
        output: {
            items: ["scrap/1"],
        },
        craftTime: 120,
    },
    {
        input: {
            items: ["lead/2"],
            power: 2,
        },
        output: {
            items: ["scrap/1"],
        },
        craftTime: 120,
    },
    {
        input: {
            items: ["metaglass/1"],
            power: 1.5,
        },
        output: {
            items: ["scrap/3"],
        },
        craftTime: 72,
    },
    {
        input: {
            items: ["silicon/1"],
            power: 1.5,
        },
        output: {
            items: ["scrap/4"],
        },
        craftTime: 72,
    },
    {
        input: {
            items: ["plastanium/1"],
            power: 1.8,
        },
        output: {
            items: ["scrap/6"],
        },
        craftTime: 81,
    },
    {
        input: {
            items: ["surge-alloy/1"],
            power: 2,
        },
        output: {
            items: ["scrap/15"],
        },
        craftTime: 108,
    },
    {
        input: {
            items: ["sand/6", "lead/2"],
            power: 1.5,
        },
        output: {
            items: ["scrap/5"],
        },
        craftTime: 90,
    },
    {
        input: {
            items: ["copper/2", "thorium/2"],
            power: 2.5,
        },
        output: {
            items: ["scrap/8"],
        },
        craftTime: 108,
    },
    {
        input: {
            items: ["lead/3", "titanium/2"],
            power: 2,
        },
        output: {
            items: ["scrap/6"],
        },
        craftTime: 96,
    },
], 
/*{
    setStats(){
        this.super$setStats();
        this.stats.remove(Stat.liquidCapacity);
    },
}*/);
Object.assign(crusher, {
    size : 2,
    itemCapacity : 30,
});
crusher.drawer = new DrawRotator();
crusher.requirements = ItemStack.with(
    Items.copper, 25,
    Items.graphite, 15,
    Items.silicon, 15,
    Items.titanium, 20,
);
crusher.buildVisibility = BuildVisibility.shown;
crusher.category = Category.crafting;

exports.crusher = crusher;