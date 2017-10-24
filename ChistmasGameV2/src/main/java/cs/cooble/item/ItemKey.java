package cs.cooble.item;

import cs.cooble.inventory.item.Item;

/**
 * Created by Matej on 25.7.2017.
 */
public class ItemKey extends Item {
    public ItemKey(int ID) {
        super(ID);
        setNameAndText("office_key");
        textureName="item/key";
    }
}
