package mc.cooble.location;

import mc.cooble.graphics.Bitmap;
import mc.cooble.inventory.item.ItemStack;
import mc.cooble.inventory.stuff.StuffToCome;
import mc.cooble.item.Items;
import mc.cooble.world.Location;

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
