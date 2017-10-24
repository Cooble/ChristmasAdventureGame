package cs.cooble.location;

import cs.cooble.graphics.Bitmap;
import cs.cooble.inventory.item.ItemStack;
import cs.cooble.inventory.stuff.StuffToCome;
import cs.cooble.item.Items;
import cs.cooble.world.Location;

/**
 * Created by Matej on 19.2.2017.
 */
public class LocationLibrary extends Location {

   // private boolean isBook;
    private StuffToCome book;


    public LocationLibrary() {
        super("library");
    }

    @Override
    public void loadTextures() {
        setBackground(Bitmap.get("location/library"));

        book = (StuffToCome) getStuffByID("guidebook");
        book.setItem(new ItemStack(Items.itemBook));
    }
}
