package mc.cooble.location;

import mc.cooble.core.Game;
import mc.cooble.event.LocationLoadEvent;
import mc.cooble.graphics.Bitmap;
import mc.cooble.graphics.Renderer;
import mc.cooble.inventory.stuff.StuffToCome;
import mc.cooble.music.MPlayer2;
import mc.cooble.world.Location;

/**
 * Created by Matej on 9.10.2016.
 */
public class LocationCottageRight extends Location {
    private Bitmap doorBitmap;
    public LocationCottageRight() {
        super("cottage_right");
        setJoeBitmapSize(1.1);
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        doorBitmap.setShouldRender(false);

    }

    @Override
    public void loadTextures() {
        StuffToCome door = (StuffToCome) getStuffByID("cellar_door");
        doorBitmap=door.getBitmapProvider().getCurrentBitmap();
        doorBitmap.setShouldRender(false);

        door.setOnPickedUp(()-> {
            MPlayer2.playSound("door");
            doorBitmap.setShouldRender(true);
            Game.core.EVENT_BUS.addDelayedEvent(30,new LocationLoadEvent("cellar"));
        });
    }
}
