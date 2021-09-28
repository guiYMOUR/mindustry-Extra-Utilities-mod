/*
* @author <(main) younggam 6.0>
* @author <guiY 7.0 adapter>
* @readme <
    Version 6.0 To version 7.0 Not available due to changes in game source code,
    for example, NumberValue becomes StatValues.number, This means that some parameters and internal functions are changed,
    i just change some parameters and functions, to make it can adapt ver 7.0(and StatValue can be extended[line 554])
>
*/

function MultiCrafterBuild() {
    this.acceptItem = function(source, item) {
        if(typeof this.block["getInputItemSet"] !== "function") return false;
        if(this.items.get(item) >= this.getMaximumAccepted(item)) return false;
        return this.block.getInputItemSet().contains(item);
    };
    this.acceptLiquid = function(source, liquid) {
        if(typeof this.block["getInputLiquidSet"] !== "function") return false;
        return this.block.getInputLiquidSet().contains(liquid);
    };
    this.removeStack = function(item, amount) {
        var ret = this.super$removeStack(item, amount);
        if(!this.items.has(item)) this.toOutputItemSet.remove(item);
        return ret;
    };
    this.handleItem = function(source, item) {
        var current = this._toggle;
        if((this.block.doDumpToggle() ? current > -1 && this.block.getRecipes()[current].output.items.some(a => a.item == item) : this.block.getOutputItemSet().contains(item)) && !this.items.has(item)) this.toOutputItemSet.add(item);
        this.items.add(item, 1);
    };
    this.handleStack = function(item, amount, tile, source) {
        var current = this._toggle;
        if((this.block.doDumpToggle() ? current > -1 && this.block.getRecipes()[current].output.items.some(a => a.item == item) : this.block.getOutputItemSet().contains(item)) && !this.items.has(item)) this.toOutputItemSet.add(item);
        this.items.add(item, amount);
    };
    this.displayConsumption = function(table) {
        if(typeof this.block["getRecipes"] !== "function") return;
        const recs = this.block.getRecipes();
        var z = 0;
        var y = 0;
        var x = 0;
        var recLen = recs.length;
        table.left();
        for(var i = 0; i < recLen; i++) {
            var items = recs[i].input.items;
            var liquids = recs[i].input.liquids;
            for(var j = 0, len = items.length; j < len; j++) {
                (function(that, stack) {
                    table.add(new ReqImage(new ItemImage(stack.item.icon(Cicon.medium), stack.amount), () => that.items != null && that.items.has(stack.item, stack.amount))).size(8 * 4);
                })(this, items[j]);
            };
            z += len;
            for(var l = 0, len = liquids.length; l < len; l++) {
                (function(that, stack) {
                    table.add(new ReqImage(new ItemImage(stack.liquid.icon(Cicon.medium), stack.amount), () => that.liquids != null && that.liquids.get(stack.liquid) > stack.amount)).size(8 * 4);
                })(this, liquids[l]);
            };
            z += len;
            if(z == 0) {
                table.image(Icon.cancel).size(8 * 4);
                x += 1;
            };
            if(i < recLen - 1) {
                var next = recs[i + 1].input;
                y += next.items.length + next.liquids.length;
                x += z;
                if(x + y <= 8 && y != 0 || x + y <= 7 && y == 0) {
                    table.image(Icon.pause).size(8 * 4);
                    x += 1;
                } else {
                    table.row();
                    x = 0;
                };
            };
            y = 0;
            z = 0;
        }
    };
    this.getPowerProduction = function() {
        var i = this._toggle;
        if(i < 0 || typeof this.block["getRecipes"] !== "function") return 0;
        var oPower = this.block.getRecipes()[i].output.power;
        if(oPower > 0 && this._cond) {
            if(this.block.getRecipes()[i].input.power > 0) {
                this._powerStat = this.efficiency();
                return oPower * this.efficiency();
            } else {
                this._powerStat = 1;
                return oPower;
            };
        }
        this._powerStat = 0;
        return 0;
    };
    this.getProgressIncreaseA = function(i, baseTime) {
        if(typeof this.block["getRecipes"] !== "function" || this.block.getRecipes()[i].input.power > 0) return this.getProgressIncrease(baseTime);
        else return 1 / baseTime * this.delta();
    };
    this.checkinput = function(i) {
        const recs = this.block.getRecipes();
        var items = recs[i].input.items;
        var liquids = recs[i].input.liquids;
        if(!this.items.has(items)) return true;
        for(var j = 0, len = liquids.length; j < len; j++) {
            if(this.liquids.get(liquids[j].liquid) < liquids[j].amount) return true;
        };
        return false;
    };
    this.checkoutput = function(i) {
        const recs = this.block.getRecipes();
        var items = recs[i].output.items;
        var liquids = recs[i].output.liquids;
        for(var j = 0, len = items.length; j < len; j++) {
            if(this.items.get(items[j].item) + items[j].amount > this.getMaximumAccepted(items[j].item)) return true;
        };
        for(var j = 0, len = liquids.length; j < len; j++) {
            if(this.liquids.get(liquids[j].liquid) + liquids[j].amount > this.block.liquidCapacity) return true;
        };
        return false;
    };
    this.checkCond = function(i) {
        if(this.block.getRecipes()[i].input.power > 0 && this.power.status <= 0) {
            this._condValid = false;
            this._cond = false;
            return false;
        } else if(this.checkinput(i)) {
            this._condValid = false;
            this._cond = false;
            return false;
        } else if(this.checkoutput(i)) {
            this._condValid = true;
            this._cond = false;
            return false;
        };
        this._condValid = true;
        this._cond = true;
        return true;
    };
    this.customCons = function(i) {
        const recs = this.block.getRecipes();
        if(this.checkCond(i)) {
            if(this.progressArr[i] != 0 && this.progressArr[i] != null) {
                this.progress = this.progressArr[i];
                this.progressArr[i] = 0;
            };
            this.progress += this.getProgressIncreaseA(i, recs[i].craftTime);
            this.totalProgress += this.delta();
            this.warmup = Mathf.lerpDelta(this.warmup, 1, 0.02);
            if(Mathf.chance(Time.delta * this.updateEffectChance)) Effects.effect(this.updateEffect, this.x + Mathf.range(this.size * 4), this.y + Mathf.range(this.size * 4));
        } else this.warmup = Mathf.lerp(this.warmup, 0, 0.02);
    };
    this.customProd = function(i) {
        const recs = this.block.getRecipes();
        var inputItems = recs[i].input.items;
        var inputLiquids = recs[i].input.liquids;
        var outputItems = recs[i].output.items;
        var outputLiquids = recs[i].output.liquids;
        var eItems = this.items;
        var eLiquids = this.liquids;
        for(var k = 0, len = inputItems.length; k < len; k++) eItems.remove(inputItems[k]);
        for(var j = 0, len = inputLiquids.length; j < len; j++) eLiquids.remove(inputLiquids[j].liquid, inputLiquids[j].amount);
        for(var a = 0, len = outputItems.length; a < len; a++) {
            for(var aa = 0, amount = outputItems[a].amount; aa < amount; aa++) {
                var oItem = outputItems[a].item
                if(!this.put(oItem)) {
                    if(!eItems.has(oItem)) this.toOutputItemSet.add(oItem);
                    eItems.add(oItem, 1);
                };
            };
        };
        for(var j = 0, len = outputLiquids.length; j < len; j++) {
            var oLiquid = outputLiquids[j].liquid;
            if(eLiquids.get(oLiquid) <= 0.001) this.toOutputLiquidset.add(oLiquid);
            this.handleLiquid(this, oLiquid, outputLiquids[j].amount);
        };
        this.block.craftEffect.at(this.x, this.y);
        this.progress = 0;
    };
    this.updateTile = function() {
        if(typeof this.block["getRecipes"] !== "function") return;
        if(this.timer.get(1, 60)) {
            this.itemHas = 0;
            this.items.each(item => this.itemHas++);
        };
        if(!Vars.headless && Vars.control.input.frag.config.getSelectedTile() != this) this.block.invFrag.hide();
        const recs = this.block.getRecipes();
        var recLen = recs.length;
        var current = this._toggle;
        if(typeof this["customUpdate"] === "function") this.customUpdate();
        if(current >= 0) {
            this.customCons(current);
            if(this.progress >= 1) this.customProd(current);
        };
        var eItems = this.items;
        var eLiquids = this.liquids;
        if(this.block.doDumpToggle() && current == -1) return;
        var que = this.toOutputItemSet.orderedItems(),
            len = que.size,
            itemEntry = this.dumpItemEntry;
        if(this.timer.get(this.block.dumpTime) && len > 0) {
            for(var i = 0; i < len; i++) {
                var candidate = que.get((i + itemEntry) % len);
                if(this.put(candidate)) {
                    eItems.remove(candidate, 1);
                    if(!eItems.has(candidate)) this.toOutputItemSet.remove(candidate);
                    break;
                };
            };
            if(i != len) this.dumpItemEntry = (i + itemEntry) % len;
        };
        var que = this.toOutputLiquidset.orderedItems(),
            len = que.size;
        if(len > 0) {
            for(var i = 0; i < len; i++) {
                var liquid = que.get(i);
                this.dumpLiquid(liquid);
                if(eLiquids.get(liquid) <= 0.001) this.toOutputLiquidset.remove(liquid);
                break;
            };
        };
    };
    this.shouldConsume = function() {
        return this._condValid && this.productionValid();
    };
    this.productionValid = function() {
        return this._cond && this.enabled;
    };
    this.updateTableAlign = function(table) {
        var pos = Core.input.mouseScreen(this.x, this.y - this.block.size * 4 - 1).y;
        var relative = Core.input.mouseScreen(this.x, this.y + this.block.size * 4);
        table.setPosition(relative.x, Math.min(pos, relative.y - Math.ceil(this.itemHas / 3) * 48 - 4), Align.top);
        if(!this.block.getInvFrag().isShown() && Vars.control.input.frag.config.getSelectedTile() == this && this.items.total() > 0) this.block.getInvFrag().showFor(this);
    };
    this.buildConfiguration = function(table) {
        if(typeof this.block["getRecipes"] !== "function") return;
        const recs = this.block.getRecipes(),
            invFrag = this.block.getInvFrag();
        if(!invFrag.isBuilt()) invFrag.build(table.parent);
        if(invFrag.isShown()) {
            invFrag.hide();
            Vars.control.input.frag.config.hideConfig();
            return;
        };
        var group = new ButtonGroup();
        group.setMinCheckCount(0);
        group.setMaxCheckCount(1);
        var recLen = recs.length;
        for(var i = 0; i < recLen; i++) {
            (function(i, that) {
                var output = recs[i].output;
                var button = table.button(Tex.whiteui, Styles.clearToggleTransi, 40, () => that.configure(button.isChecked() ? i : -1)).group(group).get();
                button.getStyle().imageUp = new TextureRegionDrawable(output.items.length > 0 ? output.items[0].item.icon(Cicon.small) : output.liquids.length > 0 ? output.liquids[0].liquid.icon(Cicon.small) : output.power > 0 ? Icon.power : Icon.cancel);
                button.update(() => button.setChecked(that._toggle == i));
            })(i, this);
        };
        table.row();
        var lengths = [];
        var max = 0;
        for(var l = 0; l < recLen; l++) {
            var output = recs[l].output;
            var outputItemLen = output.items.length;
            var outputLiquidLen = output.liquids.length;
            if(lengths[l] == null) lengths[l] = [0, 0, 0];
            if(outputItemLen > 0) lengths[l][0] = outputItemLen - 1;
            if(outputLiquidLen > 0) {
                if(outputItemLen > 0) lengths[l][1] = outputLiquidLen;
                else lengths[l][1] = outputLiquidLen - 1;
            };
            if(output.power > 0) lengths[l][2] = 1;
        };
        for(var i = 0; i < recLen; i++) {
            max = max < lengths[i][0] + lengths[i][1] + lengths[i][2] ? lengths[i][0] + lengths[i][1] + lengths[i][2] : max;
        };
        for(var i = 0; i < max; i++) {
            for(var j = 0; j < recLen; j++) {
                var output = recs[j].output;
                var outputItemLen = output.items.length;
                var outputLiquidLen = output.liquids.length;
                if(lengths[j][0] > 0) {
                    table.image(output.items[outputItemLen - lengths[j][0]].item.icon(Cicon.small));
                    lengths[j][0]--;
                } else if(lengths[j][1] > 0) {
                    table.image(output.liquids[outputLiquidLen - lengths[j][1]].liquid.icon(Cicon.small));
                    lengths[j][1]--;
                } else if(lengths[j][2] > 0) {
                    if(output.items[0] != null || output.liquids[0] != null) {
                        table.image(Icon.power);
                    } else table.image(Tex.clear);
                    lengths[j][2]--;
                } else {
                    table.image(Tex.clear);
                };
            };
            table.row();
        };
    };
    this.configured = function(player, value) {
        if(isNaN(value) || typeof value != "number") {
            this._toggle = -1;
            this._cond = false;
            this._condValid = false;
            return;
        };
        var current = this._toggle;
        if(current >= 0) this.progressArr[current] = this.progress;
        if(value == -1) {
            this._condValid = false;
            this._cond = false;
        };
        if(this.block.doDumpToggle()) {
            this.toOutputItemSet.clear();
            this.toOutputLiquidset.clear();
            if(value > -1) {
                var oItems = this.block.getRecipes()[value].output.items;
                var oLiquids = this.block.getRecipes()[value].output.liquids;
                for(var i = 0, len = oItems.length; i < len; i++) {
                    var item = oItems[i].item;
                    if(this.items.has(item)) this.toOutputItemSet.add(item);
                };
                for(var i = 0, len = oLiquids.length; i < len; i++) {
                    var liquid = oLiquids[i].liquid;
                    if(this.liquids.get(liquid) > 0.001) this.toOutputLiquidset.add(liquid);
                };
            };
        };
        this.progress = 0;
        this._toggle = value;
    };
    this.onConfigureTileTapped = function(other) {
        return this.items.total() > 0 ? true : this != other;
    };
    this.created = function() {
        var that = this;
        this.cons = extendContent(ConsumeModule, this, {
            _entity: that,
            status() {
                if(this._entity.productionValid()) return BlockStatus.active;
                if(this._entity.getCondValid()) return BlockStatus.noOutput;
                return BlockStatus.noInput;
            }
        });
    };
    this.getToggle = function() {
        return this._toggle;
    };
    this._toggle = 0;
    this.progressArr = [];
    this.getCond = function() {
        return this._cond;
    };
    this._cond = false;
    this._condValid = false;
    this.getCondValid = function() {
        return this._condValid;
    };
    this.getPowerStat = function() {
        return this._powerStat;
    };
    this._powerStat = 0;
    this.toOutputItemSet = new OrderedSet();
    this.toOutputLiquidset = new OrderedSet();
    this.dumpItemEntry = 0;
    this.itemHas = 0;
    this.config = function() {
        return this._toggle;
    };
    this.write = function(write) {
        this.super$write(write);
        write.s(this._toggle);
        var queItem = this.toOutputItemSet.orderedItems(),
            len = queItem.size;
        write.s(len);
        for(var i = 0; i < len; i++) write.s(queItem.get(i).id);
        var queLiquid = this.toOutputLiquidset.orderedItems(),
            len = queLiquid.size;
        write.s(len);
        for(var i = 0; i < len; i++) write.s(queLiquid.get(i).id);
    };
    this.read = function(read, revision) {
        this.super$read(read, revision);
        this._toggle = read.s();
        this.toOutputItemSet.clear();
        this.toOutputLiquidset.clear();
        var len = read.s(),
            vc = Vars.content,
            ci = ContentType.item,
            cl = ContentType.liquid;
        for(var i = 0; i < len; i++) this.toOutputItemSet.add(vc.getByID(ci, read.s()));
        var len = read.s();
        for(var i = 0; i < len; i++) this.toOutputLiquidset.add(vc.getByID(cl, read.s()));
    };
};

function MultiCrafterBlock() {
    this.tempRecs = [];
    var recs = [];
    var infoStyle = null;
    this.getRecipes = function() {
        return recs;
    };
    this._liquidSet = new ObjectSet();
    this.getLiquidSet = function() {
        return this._liquidSet;
    };
    this.hasOutputItem = false;
    this._inputItemSet = new ObjectSet();
    this.getInputItemSet = function() {
        return this._inputItemSet;
    };
    this._inputLiquidSet = new ObjectSet();
    this.getInputLiquidSet = function() {
        return this._inputLiquidSet;
    };
    this._outputItemSet = new ObjectSet();
    this.getOutputItemSet = function() {
        return this._outputItemSet;
    };
    this._outputLiquidSet = new ObjectSet();
    this.getOutputLiquidSet = function() {
        return this._outputLiquidSet;
    };
    this.dumpToggle = false;
    this.doDumpToggle = function() {
        return this.dumpToggle;
    };
    this.powerBarI = false;
    this.powerBarO = false;
    this._invFrag = extend(BlockInventoryFragment, {
        _built: false,
        isBuilt() {
            return this._built;
        },
        visible: false,
        isShown() {
            return this.visible;
        },
        showFor(t) {
            this.visible = true;
            this.super$showFor(t);
        },
        hide() {
            this.visible = false;
            this.super$hide();
        },
        build(parent) {
            this._built = true;
            this.super$build(parent);
        }
    });
    this.getInvFrag = function() {
        return this._invFrag;
    };
    this.init = function() {
        for(var i = 0; i < this.tmpRecs.length; i++) {
            var tmp = this.tmpRecs[i];
            var isInputExist = tmp.input != null,
                isOutputExist = tmp.output != null;
            var tmpInput = tmp.input;
            var tmpOutput = tmp.output;
            if(isInputExist && tmpInput.power > 0) this.powerBarI = true;
            if(isOutputExist && tmpOutput.power > 0) this.powerBarO = true;
            recs[i] = {
                input: {
                    items: [],
                    liquids: [],
                    power: isInputExist ? typeof tmpInput.power == "number" ? tmpInput.power : 0 : 0
                },
                output: {
                    items: [],
                    liquids: [],
                    power: isOutputExist ? typeof tmpOutput.power == "number" ? tmpOutput.power : 0 : 0
                },
                craftTime: typeof tmp.craftTime == "number" ? tmp.craftTime : 80
            };
            var vc = Vars.content;
            var ci = ContentType.item;
            var cl = ContentType.liquid;
            var realInput = recs[i].input;
            var realOutput = recs[i].output;
            if(isInputExist) {
                if(tmpInput.items != null) {
                    for(var j = 0, len = tmpInput.items.length; j < len; j++) {
                        if(typeof tmpInput.items[j] != "string") throw "It is not string at " + j + "th input item in " + i + "th recipe";
                        var words = tmpInput.items[j].split("/");
                        if(words.length != 2) throw "Malform at " + j + "th input item in " + i + "th recipe";
                        var item = vc.getByName(ci, words[0]);
                        if(item == null) throw "Invalid item: " + words[0] + " at " + j + "th input item in " + i + "th recipe";
                        this._inputItemSet.add(item);
                        if(isNaN(words[1])) throw "Invalid amount: " + words[1] + " at " + j + "th input item in " + i + "th recipe";
                        realInput.items[j] = new ItemStack(item, words[1] * 1);
                    };
                };
                if(tmpInput.liquids != null) {
                    for(var j = 0, len = tmpInput.liquids.length; j < len; j++) {
                        if(typeof tmpInput.liquids[j] != "string") throw "It is not string at " + j + "th input liquid in " + i + "th recipe";
                        var words = tmpInput.liquids[j].split("/");
                        if(words.length != 2) throw "Malform at " + j + "th input liquid in " + i + "th recipe";
                        var liquid = vc.getByName(cl, words[0]);
                        if(liquid == null) throw "Invalid liquid: " + words[0] + " at " + j + "th input liquid in " + i + "th recipe";
                        this._inputLiquidSet.add(liquid);
                        this._liquidSet.add(liquid);
                        if(isNaN(words[1])) throw "Invalid amount: " + words[1] + " at " + j + "th input liquid in " + i + "th recipe";
                        realInput.liquids[j] = new LiquidStack(liquid, words[1] * 1);
                    };
                };
            };
            if(isOutputExist) {
                if(tmpOutput.items != null) {
                    for(var j = 0, len = tmpOutput.items.length; j < len; j++) {
                        if(typeof tmpOutput.items[j] != "string") throw "It is not string at " + j + "th output item in " + i + "th recipe";
                        var words = tmpOutput.items[j].split("/");
                        if(words.length != 2) throw "Malform at " + j + "th output item in " + i + "th recipe"
                        var item = vc.getByName(ci, words[0]);
                        if(item == null) throw "Invalid item: " + words[0] + " at " + j + "th output item in " + i + "th recipe";
                        this.outputItemSet.add(item);
                        if(isNaN(words[1])) throw "Invalid amount: " + words[1] + " at " + j + "th output item in " + i + "th recipe";
                        realOutput.items[j] = new ItemStack(item, words[1] * 1);
                    };
                    if(j != 0) this.hasOutputItem = true;
                };
                if(tmpOutput.liquids != null) {
                    for(var j = 0, len = tmpOutput.liquids.length; j < len; j++) {
                        if(typeof tmpOutput.liquids[j] != "string") throw "It is not string at " + j + "th output liquid in " + i + "th recipe";
                        var words = tmpOutput.liquids[j].split("/");
                        if(words.length != 2) throw "Malform at " + j + "th output liquid in " + i + "th recipe";
                        var liquid = vc.getByName(cl, words[0]);
                        if(liquid == null) throw "Invalid liquid: " + words[0] + " at " + j + "th output liquid in " + i + "th recipe";
                        this._outputLiquidSet.add(liquid);
                        this._liquidSet.add(liquid);
                        if(isNaN(words[1])) throw "Invalid amount: " + words[1] + " at " + j + "th output liquid in " + i + "th recipe";
                        realOutput.liquids[j] = new LiquidStack(liquid, words[1] * 1);
                    };
                };
            };
        };
        this.hasPower = this.powerBarI || this.powerBarO;
        if(this.powerBarI) this.consumes.add(extend(ConsumePower, {
            requestedPower(entity) {
                if(typeof entity["getToggle"] !== "function") return 0;
                var i = entity.getToggle();
                if(i < 0) return 0;
                var input = entity.block.getRecipes()[i].input.power;
                if(input > 0 && entity.getCond()) return input;
                return 0;
            }
        }));
        this.consumesPower = this.powerBarI;
        this.outputsPower = this.powerBarO;
        this.super$init();
        if(!this._outputLiquidSet.isEmpty()) this.outputsLiquid = true;
        this.timers++;
        if(!Vars.headless) infoStyle = Core.scene.getStyle(Button.ButtonStyle);
    };
    this.setStats = function() {
        this.super$setStats();
        if(this.powerBarI) this.stats.remove(Stat.powerUse);
        this.stats.remove(Stat.productionTime);
        this.stats.add(Stat.input, new JavaAdapter(StatValue, {
        display(table){
            table.row();
            var recLen = recs.length;
            for(var i = 0; i < recLen; i++) {
                var rec = recs[i];
                var outputItems = rec.output.items,
                    inputItems = rec.input.items;
                var outputLiquids = rec.output.liquids,
                    inputLiquids = rec.input.liquids;
                var inputPower = rec.input.power,
                    outputPower = rec.output.power;
                table.table(infoStyle.up, part => {
                    part.add("[accent]" + Stat.input.localized()).expandX().left().row();
                    part.table(cons(row => {
                        for(var l = 0, len = inputItems.length; l < len; l++) row.add(new ItemDisplay(inputItems[l].item, inputItems[l].amount, true)).padRight(5);
                    })).left().row();
                    part.table(cons(row => {
                        for(var l = 0, len = inputLiquids.length; l < len; l++) row.add(new LiquidDisplay(inputLiquids[l].liquid, inputLiquids[l].amount, false));
                    })).left().row();
                    if(inputPower > 0) {
                        part.table(cons(row => {
                            row.add("[lightgray]" + Stat.powerUse.localized() + ":[]").padRight(4);
                            (StatValues.number(recs[i].input.power * 60, StatUnit.powerSecond)).display(row);
                        })).left().row();
                    }
                    part.add("[accent]" + Stat.output.localized()).left().row();
                    part.table(cons(row => {
                        for(var jj = 0, len = outputItems.length; jj < len; jj++) row.add(new ItemDisplay(outputItems[jj].item, outputItems[jj].amount, true)).padRight(5);
                    })).left().row();
                    part.table(cons(row => {
                        for(var jj = 0, len = outputLiquids.length; jj < len; jj++) row.add(new LiquidDisplay(outputLiquids[jj].liquid, outputLiquids[jj].amount, false));
                    })).left().row();
                    if(outputPower > 0) {
                        part.table(cons(row => {
                            row.add("[lightgray]" + Stat.basePowerGeneration.localized() + ":[]").padRight(4);
                            (StatValues.number(recs[i].output.power * 60, StatUnit.powerSecond)).display(row);
                        })).left().row();
                    }
                    part.table(cons(row => {
                        row.add("[lightgray]" + Stat.productionTime.localized() + ":[]").padRight(4);
                        (StatValues.number(rec.craftTime / 60, StatUnit.seconds)).display(row);
                    })).left().row();
                    if(typeof this["customDisplay"] === "function") this.customDisplay(part, i);
                }).color(Pal.accent).left().growX();
                table.add().size(18).row();
            }
        },
        }));
    };
    this.setBars = function() {
        this.super$setBars();
        this.bars.remove("liquid");
        this.bars.remove("items");
        if(!this.powerBarI && this.hasPower) this.bars.remove("power");
        if(this.powerBarO) this.bars.add("poweroutput", entity => new Bar(() => Core.bundle.format("bar.poweroutput", entity.getPowerProduction() * 60 * entity.timeScale), () => Pal.powerBar, () => typeof entity["getPowerStat"] === "function" ? entity.getPowerStat() : 0));
        var i = 0;
        if(!this._liquidSet.isEmpty()) {
            this._liquidSet.each(k => {
                this.bars.add("liquid" + i, entity => new Bar(() => k.localizedName, () => k.barColor == null ? k.color : k.barColor, () => entity.liquids.get(k) / this.liquidCapacity));
                i++;
            });
        }
    }
    this.outputsItems = function() {
        return this.hasOutputItem;
    }
}

function cloneObject(obj) {
    var clone = {};
    for(var i in obj) {
        print(i);
        if(typeof obj[i] == "object" && obj[i] != null) clone[i] = cloneObject(obj[i]);
        else clone[i] = obj[i];
    }
    return clone;
}
module.exports = {
    MultiCrafter(Type, EntityType, name, recipes, def, ExtraEntityDef) {
        const block = new MultiCrafterBlock();
        Object.assign(block, def);
        const multi = extendContent(Type, name, block);
        multi.buildType = () => extendContent(EntityType, multi, Object.assign(new MultiCrafterBuild(), typeof ExtraEntityDef == "function" ? new ExtraEntityDef() : cloneObject(ExtraEntityDef)));
        multi.configurable = true;
        multi.hasItems = true;
        multi.hasLiquids = true;
        multi.hasPower = false;
        multi.tmpRecs = recipes;
        multi.saveConfig = true;
        return multi;
    }
}
