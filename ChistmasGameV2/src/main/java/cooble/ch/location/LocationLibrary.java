package cooble.ch.location;

import cooble.ch.graphics.Bitmap;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.inventory.stuff.StuffToCome;
import cooble.ch.item.Items;
import cooble.ch.world.Location;

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
