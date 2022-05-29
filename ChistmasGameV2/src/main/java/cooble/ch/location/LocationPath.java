package cooble.ch.location;

import cooble.ch.core.Game;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.event.SpeakEvent;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.inventory.stuff.Stuff;
import cooble.ch.item.Items;
import cooble.ch.music.MPlayer2;
import cooble.ch.world.IAction;
import cooble.ch.world.Location;
import cooble.ch.world.NBT;

/**
 * Created by Matej on 9.10.2016.
 */
public class LocationPath extends Location {

    private int commentIndex;

    public LocationPath() {
        super("path");
        setJoeBitmapSize(0.75);
    }

    @Override
    public void loadTextures() {
        Stuff hat = getStuffByID("santa_hat");
        hat.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (released)
                    return true;
                commentIndex++;
                if (commentIndex < 5) {
                    Game.core.EVENT_BUS.addEvent(new SpeakEvent(getLocationPrefix()+".stuff.santa_hat.comment."+(commentIndex-1)));

                    return false;
                }

                hat.markDeath();
                Game.getWorld().inventory().addItem(new ItemStack(Items.itemCap));
                Game.core.EVENT_BUS.addEvent(new SpeakEvent(getLocationPrefix()+".stuff.santa_hat.pickup"));
                return true;
            }
        });
        getStuffByID("path").setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (released)
                    return true;
                if (item != null && item.ITEM.ID == Items.itemQuadracopter.ID) {
                    Game.input.muteMouseInput(true);
                    Game.getWorld().inventory().setItemInHand(null);
                    SpeakEvent event = new SpeakEvent("entity.joe.letsfly");
                    event.setOnEnd(new Runnable() {
                        @Override
                        public void run() {
                            Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("quadratrip"));
                            Game.input.muteMouseInput(false);
                        }
                    });
                    Game.core.EVENT_BUS.addEvent(event);
                }
                else Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.idea"));

                return true;
            }
        });
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        commentIndex = nbt.getInteger("comment");
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putInteger("comment", commentIndex);
    }

    @Override
    public void onStart() {
        super.onStart();
        MPlayer2.playSoundIfNotExist("arctic_wind", 0.5);
    }
}
