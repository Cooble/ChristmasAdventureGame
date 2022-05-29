package cooble.ch.item;

import cooble.ch.inventory.item.Item;

/**
 * Created by Matej on 28.7.2017.
 */
public class ItemFan extends Item {
    public ItemFan(int i) {
        super(i);
        setNameAndText("fan");
        textureName="item/fan_head";
    }
}
