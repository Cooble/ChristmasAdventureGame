package cs.cooble.item;


import cs.cooble.core.Game;
import cs.cooble.event.LocationLoadEvent;
import cs.cooble.inventory.item.Item;

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
