package cooble.ch.location;

import cooble.ch.core.Game;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.graphics.Bitmap;
import cooble.ch.graphics.Renderer;
import cooble.ch.inventory.stuff.StuffToCome;
import cooble.ch.music.MPlayer2;
import cooble.ch.world.Location;

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
