package cs.cooble.location;

import cs.cooble.core.Game;
import cs.cooble.event.SpeakEvent;
import cs.cooble.inventory.item.ItemStack;
import cs.cooble.world.IActionRectangle;
import cs.cooble.world.Location;

import java.awt.*;

/**
 * Created by Matej on 4.2.2017.
 */
public class LocationWhiteboard extends Location {
    public LocationWhiteboard() {
        super("whiteboard");
        setJoeProhibited(true);

    }

    @Override
    public void loadTextures() {
        actionRectangleManager.register(new IActionRectangle() {
            @Override
            public Rectangle getRectangle() {
                return new Rectangle(0,0,Game.renderer.PIXEL_WIDTH,Game.renderer.PIXEL_HEIGHT-30);
            }

            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if(released)
                    return true;
                Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.code"));

                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Game.getWorld().inventory().lock(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        Game.getWorld().inventory().lock(false);

    }
}
