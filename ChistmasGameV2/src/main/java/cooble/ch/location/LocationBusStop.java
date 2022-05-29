package cooble.ch.location;


import cooble.ch.core.Game;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.inventory.stuff.StuffToCome;
import cooble.ch.item.Items;
import cooble.ch.music.MPlayer2;
import cooble.ch.world.Location;

/**
 * Created by Matej on 5.8.2016.
 */
public final class LocationBusStop extends Location {

    public LocationBusStop() {
        super("bus_stop");
        setJoeBitmapSize(1.1);
    }
    @Override
    public void loadTextures() {
        setDefaultShadow();
        StuffToCome screwdriver = (StuffToCome) getStuffByID("screwdriver");
        screwdriver.setItem(new ItemStack(Items.itemScrewdriver));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Game.getWorld().getModule().getNBT().getBoolean("panic")) {
            MPlayer2.playSoundIfNotExist("panic", 0.1);
        }
    }
}
