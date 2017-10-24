package cs.cooble.location;


import cs.cooble.core.Game;
import cs.cooble.graphics.Bitmap;
import cs.cooble.graphics.Renderer;
import cs.cooble.music.MPlayer2;
import cs.cooble.stuff.StuffLever;
import cs.cooble.world.Location;

/**
 * Created by Matej on 5.8.2016.
 */
public class LocationCottageLeft extends Location {
    StuffLever lever;

    public LocationCottageLeft() {
        super("cottage_left");
        setJoeBitmapSize(1.1);
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        setShadow(Bitmap.get("shadow/cottage_left" + (Game.getWorld().getModule().getNBT().getBoolean("isElectricityOn") ? "1" : "0")));
        super.onStartRendering(renderer);
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean isElectricity = Game.getWorld().getModule().getNBT().getBoolean("isElectricityOn");getArrowByID("toGarageOut").setEnabled(isElectricity);
        if (Game.getWorld().getModule().getNBT().getBoolean("panic")) {
            MPlayer2.playSoundIfNotExist("panic", 0.1);
        }

    }

    @Override
    public void loadTextures() {
        //stuff
        lever = new StuffLever(generateStuffName("lever"), this, (Bitmap) getStuffByID("fuse_ground").getBitmapProvider());
        addXMLSubstrate(lever, "lever");
        addStuff(lever);
    }
}
