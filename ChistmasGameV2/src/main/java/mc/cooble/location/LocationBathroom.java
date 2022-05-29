package mc.cooble.location;

import mc.cooble.inventory.item.ItemStack;
import mc.cooble.inventory.stuff.StuffToCome;
import mc.cooble.item.Items;
import mc.cooble.music.MPlayer2;
import mc.cooble.world.Location;

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
