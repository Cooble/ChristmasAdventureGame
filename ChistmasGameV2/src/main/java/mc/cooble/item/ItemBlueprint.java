package mc.cooble.item;

import mc.cooble.core.Game;
import mc.cooble.event.LocationLoadEvent;
import mc.cooble.inventory.item.Item;

/**
 * Created by Matej on 19.3.2017.
 */
public class ItemBlueprint extends Item {
    public ItemBlueprint(int i) {
        super(i);
        textureName="item/blueprint_item_item";
        setNameAndText("blueprint");
    }
    @Override
    public void onClickedRightOnThis() {
        LocationLoadEvent locationLoadEvent = new LocationLoadEvent("blueprint");
        Game.core.EVENT_BUS.addEvent(locationLoadEvent);
    }

}
