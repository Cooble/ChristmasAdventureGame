package cs.cooble.item;

import cs.cooble.core.Game;
import cs.cooble.event.SpeakEvent;
import cs.cooble.inventory.item.Item;
import cs.cooble.inventory.item.ItemStack;

/**
 * Created by Matej on 25.7.2017.
 */
public class ItemCap extends Item {
    public ItemCap(int ID) {
        super(ID);
        setNameAndText("cap");
        textureName="item/cap_item";
    }
    @Override
    public void onClickedRightOnThis() {
        boolean hasFoundKey = Game.getWorld().getNBT().getBoolean("hasFoundKeyInCap",false);
        if(!hasFoundKey) {
            Game.getWorld().getNBT().putBoolean("hasFoundKeyInCap",true);
            Game.core.EVENT_BUS.addEvent(new SpeakEvent("item.cap.findkey"));
            Game.getWorld().inventory().addItem(new ItemStack(Items.itemKey));
        }else Game.core.EVENT_BUS.addEvent(new SpeakEvent("item.cap.nomore"));
    }
}
