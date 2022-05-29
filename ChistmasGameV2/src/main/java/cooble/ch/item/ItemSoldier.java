package cooble.ch.item;

import cooble.ch.inventory.item.Item;
import cooble.ch.inventory.item.ItemStack;

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
