package mc.cooble.location;

import mc.cooble.core.Game;
import mc.cooble.graphics.Renderer;
import mc.cooble.music.MPlayer2;
import mc.cooble.world.Location;

/**
 * Created by Matej on 17.12.2015.
 */
public abstract class LocationPaper extends Location {
    public LocationPaper(String s) {
        super(s);

    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        MPlayer2.playSound("paper");

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

    @Override
    public void loadTextures() {
        setJoeProhibited(true);
    }
}
