package mc.cooble.location;


import mc.cooble.core.Game;
import mc.cooble.entity.Arrow;
import mc.cooble.event.LocationLoadEvent;
import mc.cooble.event.SpeakEvent;
import mc.cooble.graphics.Renderer;
import mc.cooble.inventory.stuff.StuffToCome;
import mc.cooble.music.MPlayer2;
import mc.cooble.stuff.StuffGarage;
import mc.cooble.translate.Translator;
import mc.cooble.world.Location;
import mc.cooble.world.NBT;

/**
 * Created by Matej on 6.8.2016.
 */
public class LocationGarageOut extends Location {

    private boolean calcIsFound;
    private boolean openDoor;
    private StuffGarage stuffGarage;

    private boolean firstOpenOfGarage;

    private Arrow arrowToGarage;

    private StuffToCome calculator;

    public LocationGarageOut() {
        super("garage");

    }

    public Arrow getArrowToGarage() {
        return arrowToGarage;
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        openDoor = Game.getWorld().getModule().getNBT().getBoolean("openGarage");
        if(openDoor) {
            if(firstOpenOfGarage){
                MPlayer2.playSound("garage_door_open");
                firstOpenOfGarage=false;
            }
            stuffGarage.open();
        }
       Game.renderer.registerRenderable(stuffGarage);

    }

    @Override
    public void onStopRendering(Renderer renderer) {
        super.onStopRendering(renderer);
        Game.renderer.removeRenderable(stuffGarage);
    }

    @Override
    public void tick() {
        super.tick();
        if(stuffGarage.wasOpened()){
            arrowToGarage.setEnabled(true);
        }
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        calcIsFound=nbt.getBoolean("calcIsFound");
        firstOpenOfGarage=nbt.getBoolean("firstOpenOfGarage",true);
        calculator.setTextName(Translator.translate(calculator.getFullName()+".name."+(calcIsFound?"1":"0")));

    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("calcIsFound",calcIsFound);
        nbt.putBoolean("firstOpenOfGarage",firstOpenOfGarage);
    }

    @Override
    public void loadTextures() {
        arrowToGarage=getArrowByID("toGarageIn");
        arrowToGarage.setEnabled(false);

        calculator= (StuffToCome) getStuffByID("calculator");
        calculator.setTextName(Translator.translate(calculator.getFullName()+".name.0"));
        calculator.setOnPickedUp(new Runnable() {
            @Override
            public void run() {
                if (!calcIsFound) {
                    SpeakEvent se = new SpeakEvent(calculator.getFullName()+".comment.0");
                    Game.core.EVENT_BUS.addEvent(se);
                    calcIsFound = true;
                    calculator.setTextName(Translator.translate(calculator.getFullName()+".name.1"));
                } else {
                    Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("calculator"));
                }
            }
        });
        stuffGarage = new StuffGarage(this);
        addXMLSubstrate(stuffGarage,"garage_door");
        addStuff(stuffGarage);
        stuffGarage.setVisible(true);
    }
}
