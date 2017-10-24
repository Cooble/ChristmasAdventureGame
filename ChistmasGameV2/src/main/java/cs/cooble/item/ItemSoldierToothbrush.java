package cs.cooble.item;

import cs.cooble.inventory.item.Item;

/**
 * Created by Matej on 25.7.2017.
 */
public class ItemSoldierToothbrush extends Item {
    public ItemSoldierToothbrush(int ID) {
        super(ID);
        setNameAndText("soldier_brush");
        textureName="item/soldier_brush";
    }
}
