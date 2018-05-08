package cs.cooble.location;


import cs.cooble.core.Game;
import cs.cooble.entity.Arrow;
import cs.cooble.event.SpeakEvent;
import cs.cooble.graphics.Renderer;
import cs.cooble.inventory.item.ItemStack;
import cs.cooble.inventory.stuff.StuffToCome;
import cs.cooble.item.Items;
import cs.cooble.music.MPlayer2;
import cs.cooble.resources.StringStream;
import cs.cooble.world.Location;
import cs.cooble.world.NBT;

import java.util.function.Supplier;

/**
 * Created by Matej on 12.12.2015.
 */
public final class LocationHome extends Location {

    private boolean fresh;

    public LocationHome() {
        super("home");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(fresh){
            Game.getWorld().getUniCreature().setPos(15,130);
            Game.getWorld().getUniCreature().setRight(true);
            fresh=false;
        }
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
       // MPlayer2.playSound("fireplace", 1);
        MPlayer2.playSongIfNot("christmas_song", 1, 200);

    }

    @Override
    public void onStopRendering(Renderer renderer) {
        super.onStopRendering(renderer);
     //   MPlayer2.stopSound("fireplace");
    }

    @Override
    public void loadTextures() {

        System.out.println("loading pot "+this);
        //pot2
        StuffToCome pot = (StuffToCome) getStuffByID("pot");
        String[] strings = StringStream.getArray(getLocationPrefix() + ".stuff.pot.comment.");
        pot.setLore(new Supplier<String>() {
            int indexik;
            @Override
            public String get() {
                indexik++;
                if(indexik>1){
                    indexik=0;
                }

                return strings[indexik];
            }
        });
        pot.setOnPickedUp(() -> {
            if(Game.getWorld().getModule().getNBT().getBoolean("find_post")) {
                Game.getWorld().inventory().addItem(new ItemStack(Items.itemPot));
                Game.getWorld().inventory().setOn();
                pot.markDeath();
                MPlayer2.playSound("pot_pick_up", 0.5);
                SpeakEvent event = new SpeakEvent(strings[2]);
                Game.core.EVENT_BUS.addEvent(event);
            }
        });


        //arrow
        Arrow postBoxArrow = getArrowByID("toPostbox");
        postBoxArrow.setOnExit(() -> MPlayer2.playSound("door", 1));
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("fresh",fresh);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        fresh=nbt.getBoolean("fresh",true);
    }
}
