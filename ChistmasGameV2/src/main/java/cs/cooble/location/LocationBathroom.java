package cs.cooble.location;

import cs.cooble.inventory.item.ItemStack;
import cs.cooble.inventory.stuff.StuffToCome;
import cs.cooble.item.Items;
import cs.cooble.music.MPlayer2;
import cs.cooble.world.Location;

/**
 * Created by Matej on 9.10.2016.
 */
public class LocationBathroom extends Location {

    public LocationBathroom() {
        super("bathroom");
        setJoeBitmapSize(1.25);
    }

    @Override
    public void loadTextures() {
        StuffToCome brush = (StuffToCome) getStuffByID("toothbrush");
        brush.setItem(new ItemStack(Items.itemToothbrush));
    }

    @Override
    public void onStart() {
        super.onStart();
        MPlayer2.playSound("door");
    }

    @Override
    public void onStop() {
        super.onStop();
        MPlayer2.playSound("door");
    }
}
