package mc.cooble.item;


import mc.cooble.core.Game;
import mc.cooble.event.LocationLoadEvent;
import mc.cooble.inventory.item.Item;

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
