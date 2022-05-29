package cooble.ch.item;

import cooble.ch.inventory.item.Item;

/**
 * Created by Matej on 30.7.2016.
 */
public class ItemPot extends Item {
    public ItemPot(int id) {
        super(id);
        this.textureName="item/pot2";
        setNameAndText("pot");
    }

}
