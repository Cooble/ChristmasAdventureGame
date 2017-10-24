package cs.cooble.item;

import cs.cooble.core.Game;
import cs.cooble.inventory.item.Item;
import cs.cooble.inventory.item.ItemStack;

/**
 * Created by Matej on 7.8.2016.
 */
public class ItemBattery extends Item {

    public ItemBattery(int id) {
        super(id);
        textureName="item/battery_item";
        setNameAndText("battery");
    }

    @Override
    public ItemStack onRightClickOnItem(ItemStack someItem, ItemStack thisItem) {
        if(someItem.ITEM.ID==ID){
            if(!Game.getWorld().getNBT().getBoolean("hasCraftedBigBattery")){
                Game.getWorld().getNBT().putBoolean("hasCraftedBigBattery", true);
                return new ItemStack(Items.itemBigBattery);
            }
        }
        return null;
    }
}
