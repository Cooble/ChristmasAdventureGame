package cooble.ch.listener;

import cooble.ch.core.Game;
import cooble.ch.event.*;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.item.Items;
import cooble.ch.logger.Log;
import cooble.ch.world.CustomSettings;
import org.newdawn.slick.Input;

/**
 * Created by Matej on 8.10.2016.
 */
public class MyUserInput extends CUserInput {

    public MyUserInput(MyKeyListener keyEvent, MyMouseListener cMouseEvent) {
        super(keyEvent, cMouseEvent);
    }

    @Override
    protected void keyTick() {
        if (muteKey)
            return;
        CustomSettings settings = Game.getSettings();
        if (Game.isDebugging) {
            if (keyListener.isfreshedPressed(Input.KEY_O)) {
                Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("office", "party"));
            }
            if (keyListener.isfreshedPressed(Input.KEY_B)) {
                Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("garage", "party"));
            }
            if (keyListener.isfreshedPressed(Input.KEY_S)) {
                Game.getWorld().inventory().addItem(new ItemStack(Items.itemScrewdriver));
            }
            if (keyListener.isfreshedPressed(Input.KEY_G)) {
                Game.getWorld().inventory().addItem(new ItemStack(Items.itemBattery));
            }
            if (keyListener.isfreshedPressed(Input.KEY_Q)) {
                Game.getWorld().inventory().addItem(new ItemStack(Items.itemQuadracopter));
            }
            if (keyListener.isfreshedPressed(Input.KEY_F)) {
                settings.setAttribute(settings.FULLSCREEN,settings.getBoolean(settings.FULLSCREEN));
                Log.println("Fullscreen set to " + settings.getBoolean(settings.FULLSCREEN));
                GameExitEvent event = new GameExitEvent();
                event.setRestart(true);
                Game.core.EVENT_BUS.addEvent(event);
            }
            if (keyListener.isfreshedPressed(Input.KEY_L)) {
                Log.println("Joe's location: {" + Game.getWorld().getUniCreature().getX() + " ," + Game.getWorld().getUniCreature().getY() + "}");
            }

            if (keyListener.getTicksOn(Input.KEY_ESCAPE) == 70 && Game.getWorld() != null && Game.getWorld().getLocationManager() != null && Game.getWorld().getLocationManager().getCurrentLocationID().equals("mail")) {

            } else {
                super.keyTick();
                return;
            }
        }
        if (keyListener.isfreshedPressed(Input.KEY_D) && keyListener.isPressed(Input.KEY_LCONTROL) && keyListener.isPressed(Input.KEY_RCONTROL) && keyListener.isPressed(Input.KEY_LSHIFT)) {
            Game.isDebugging = !Game.isDebugging;
            Log.println("Debug: " + Game.isDebugging);
        }
        super.keyTick();

    }

}
