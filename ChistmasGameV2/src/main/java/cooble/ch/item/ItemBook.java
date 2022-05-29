package cooble.ch.item;

import cooble.ch.core.Game;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.inventory.item.Item;

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
