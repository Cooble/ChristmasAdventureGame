package cooble.ch.item;

import cooble.ch.core.Game;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.inventory.item.Item;

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
