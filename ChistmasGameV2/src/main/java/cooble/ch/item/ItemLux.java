package cooble.ch.item;

import cooble.ch.inventory.item.Item;

/**
 * Created by Matej on 21.4.2017.
 */
public class ItemLux extends Item {
    public ItemLux(int ID) {
        super(ID);
        setNameAndText("lux");
        setTextureName("item/lux_item");
    }
}
