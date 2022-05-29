package cooble.ch.item;


import cooble.ch.core.Game;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.inventory.item.Item;

/**
 * Created by Matej on 29.7.2016.
 */
public class ItemMail extends Item {
    public ItemMail(int id) {
        super(id);
        this.textureName="item/mail2";
        setNameAndText("mail");


    }

    @Override
    public void onClickedRightOnThis() {

        Game.core.EVENT_BUS.addEvent( new LocationLoadEvent("mail"));
        Game.getWorld().inventory().removeSelected();
    }
}
