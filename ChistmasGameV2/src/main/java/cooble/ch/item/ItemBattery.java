package cooble.ch.item;

import cooble.ch.core.Game;
import cooble.ch.inventory.item.Item;
import cooble.ch.inventory.item.ItemStack;

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
