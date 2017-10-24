package cs.cooble.item;

import cs.cooble.inventory.item.Item;

/**
 * Created by Matej on 6.8.2016.
 */
public class ItemScrewDriver extends Item {
    public ItemScrewDriver(int id) {
        super(id);
        textureName="item/screwdriver";
        setNameAndText("screwdriver");
    }
}
