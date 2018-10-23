package mc.cooble.location;


import mc.cooble.core.Game;
import mc.cooble.inventory.item.ItemStack;
import mc.cooble.inventory.stuff.StuffToCome;
import mc.cooble.item.Items;
import mc.cooble.music.MPlayer2;
import mc.cooble.world.Location;

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
