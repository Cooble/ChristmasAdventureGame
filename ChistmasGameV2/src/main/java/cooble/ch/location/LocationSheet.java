package cooble.ch.location;


import cooble.ch.entity.Arrow;
import cooble.ch.graphics.Renderer;
import cooble.ch.inventory.stuff.Stuff;
import cooble.ch.world.Location;

/**
 * Created by Matej on 12.12.2015.
 */
public final class LocationSheet extends Location {

    Arrow arrow;
    Stuff stuff;

    private static final String ID="";

    public LocationSheet() {
        super(ID);

    }


    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        //Game.renderer.registerBitmapProvider(stuff);
        //MPlayer2.playSongWithFade("christmas_song", 0.2, 100, 50);
    }

    @Override
    public void onStopRendering(Renderer renderer) {
        super.onStopRendering(renderer);
        //MPlayer2.stopSongWithFade(100);
    }

    @Override
    public void loadTextures() {

    }
}
