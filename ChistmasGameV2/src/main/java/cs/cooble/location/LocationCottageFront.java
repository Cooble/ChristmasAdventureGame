package cs.cooble.location;


import cs.cooble.core.Game;
import cs.cooble.event.Event;
import cs.cooble.event.SpeakEvent;
import cs.cooble.graphics.Bitmap;
import cs.cooble.graphics.BitmapStack;
import cs.cooble.graphics.Renderer;
import cs.cooble.music.MPlayer2;
import cs.cooble.world.Location;

/**
 * Created by Matej on 5.8.2016.
 */
public class LocationCottageFront extends Location {
    BitmapStack lampi;

    public LocationCottageFront() {
        super("cottage_front");
        setJoeBitmapSize(0.8);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Game.getWorld().getModule().getNBT().getBoolean("panic"))
            MPlayer2.playSoundIfNotExist("panic", 0.2);
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        boolean isElectricity = Game.getWorld().getModule().getNBT().getBoolean("isElectricityOn");
        setShadow(Bitmap.get("shadow/cottage_front" + (isElectricity ? "1" : "0")));
        Game.renderer.setDynamicalLighting(isElectricity ? null : Bitmap.get("shadow/baterka"));
        lampi.setCurrentIndex(isElectricity ? 1 : 0);
        super.onStartRendering(renderer);

        getArrowByID("toPath").setEnabled(isElectricity);
        Event openDoor;
        if(isElectricity){
            openDoor=null;
            getArrowByID("toParty").setOnExit(new Runnable() {
                @Override
                public void run() {
                    MPlayer2.playSound("door");
                }
            });
        }
        else{
            openDoor = new Event() {
                @Override
                public void dispatchEvent() {
                    Game.core.EVENT_BUS.addEvent(new SpeakEvent("location.party.noopendoor"));
                }
            };
        }
        getArrowByID("toParty").setOnClickedEvent(openDoor);



    }

    @Override
    public void loadTextures() {
        lampi = (BitmapStack) getStuffByID("cottage_lamp").getBitmapProvider();

    }
}
