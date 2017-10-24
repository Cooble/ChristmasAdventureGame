package cs.cooble.item;

import cs.cooble.core.Game;
import cs.cooble.event.LocationLoadEvent;
import cs.cooble.inventory.item.Item;

/**
 * Created by Matej on 12.3.2017.
 */
public class ItemBook extends Item {

    public ItemBook(int i) {
        super(i);
        textureName="item/book";
        setNameAndText("book");
    }

    @Override
    public void onClickedRightOnThis() {
        Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("book"));
    }
}
