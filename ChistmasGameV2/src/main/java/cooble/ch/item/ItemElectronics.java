package cooble.ch.item;

import cooble.ch.inventory.item.Item;

/**
 * Created by Matej on 28.7.2017.
 */
public class ItemElectronics extends Item {
    public ItemElectronics(int ID) {
        super(ID);
        setNameAndText("electronics");
        textureName="item/electronics_item";
    }
}
