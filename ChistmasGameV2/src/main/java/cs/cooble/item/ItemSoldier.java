package cs.cooble.item;

import cs.cooble.inventory.item.Item;
import cs.cooble.inventory.item.ItemStack;

/**
 * Created by Matej on 25.7.2017.
 */
public class ItemSoldier extends Item {
    public ItemSoldier(int id) {
        super(id);
        setNameAndText("soldier");
        setTextureName("item/soldier_item");

    }

    @Override
    public ItemStack onRightClickOnItem(ItemStack someItem, ItemStack thisItem) {
        if(someItem.ITEM.ID==Items.itemToothbrush.ID){
            return new ItemStack(Items.itemSoldierBrush);
        }
        return null;
    }
}
