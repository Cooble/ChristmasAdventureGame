package cs.cooble.item;

import cs.cooble.inventory.item.Item;
import cs.cooble.inventory.item.ItemStack;

/**
 * Created by Matej on 21.4.2017.
 */
public class ItemToothBrush extends Item {
    public ItemToothBrush(int i) {
        super(i);
        textureName= "item/brush";
        setNameAndText("brush");
    }
    @Override
    public ItemStack onRightClickOnItem(ItemStack someItem, ItemStack thisItem) {
        if(someItem.ITEM.ID==Items.itemSoldier.ID){
            return new ItemStack(Items.itemSoldierBrush);
        }
        return null;
    }

}
