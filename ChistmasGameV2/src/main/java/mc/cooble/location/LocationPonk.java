package mc.cooble.location;


import mc.cooble.entity.Arrow;
import mc.cooble.graphics.Renderer;
import mc.cooble.inventory.stuff.Stuff;
import mc.cooble.world.Location;

/**
 * Created by Matej on 12.12.2015.
 */
public final class LocationPonk extends Location {

    Arrow arrow;
    Stuff stuff;

    private static final String ID="blueprintPonk";

    public LocationPonk() {
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
