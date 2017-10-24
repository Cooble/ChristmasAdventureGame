package cs.cooble.location;

import cs.cooble.core.Game;
import cs.cooble.event.LocationLoadEvent;
import cs.cooble.event.SpeakEvent;
import cs.cooble.graphics.Bitmap;
import cs.cooble.graphics.Renderer;
import cs.cooble.graphics.TextPainter;
import cs.cooble.inventory.item.ItemStack;
import cs.cooble.item.Items;
import cs.cooble.music.MPlayer2;
import cs.cooble.world.IAction;
import cs.cooble.world.Location;
import cs.cooble.world.NBT;
import org.newdawn.slick.Color;

import java.awt.*;


/**
 * Created by Matej on 7.8.2016.
 */
public class LocationCalculator extends Location {

    private String numero;
    private boolean isPowered;
    private final String code = "314159";
    private int tickToEndError;
    private int tickToOpenGarage = -1;
    private TextPainter textPainter;
    private LocationLoadEvent garageLoadEvent;

    private int noPowerClicks;
    private int noPowerClicksAmnesia;

    public LocationCalculator() {
        super("calculator");
    }

    @Override
    public void tick() {
        super.tick();
        if (tickToEndError != 0) {
            tickToEndError--;
            if (tickToEndError == 0) {
                numero = "";
                setTextDisplay(Color.white);
            }
        }
        if (tickToOpenGarage > 0) {
            tickToOpenGarage--;
            if (tickToOpenGarage == 0) {
                tickToOpenGarage = -1;
                Game.core.EVENT_BUS.addEvent(garageLoadEvent);
            }
        }

    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        isPowered = isCalculatorPowered();
        renderer.registerBitmapProvider(textPainter.getBitmaps()[0]);
        setTextDisplay(isOpenedGarage() ? Color.green : Color.white);
    }


    private void keyPressed(int key) {
        if (isPowered) {
            if (!isOpenedGarage()) {
                if (tickToEndError == 0) {
                    numero += key;
                    if (numero.length() == code.length()) {
                        if (numero.equals(code)) {
                            setTextDisplay(Color.green);
                            Game.core.EVENT_BUS.addDelayedEvent(20, () -> MPlayer2.playSound("success", 0.6));
                            tickToOpenGarage = 60;
                            Game.getWorld().getModule().getNBT().putBoolean("openGarage", true);
                        } else {
                            numero = "";
                            for (int i = 0; i < code.length(); i++) {
                                numero += "#";
                            }
                            setTextDisplay(Color.red);
                            tickToEndError = 50;
                            MPlayer2.playSound("error");
                            noPowerClicksAmnesia++;
                            if (noPowerClicksAmnesia > 1) {
                                noPowerClicksAmnesia = 0;
                                Game.core.EVENT_BUS.addDelayedEvent(30, new SpeakEvent("entity.joe.santaAmnesia"));
                            }
                        }
                    } else setTextDisplay(Color.white);
                }
            }
        }
    }

    private void setTextDisplay(Color color) {
        textPainter.writeOnBitmap(numero, color, 48 * 2, 15 * 2);
    }

    private boolean isCalculatorPowered() {
        return Game.getWorld().getModule().getNBT().getBoolean("isCalculatorPowered");
    }

    private boolean isOpenedGarage() {
        return Game.getWorld().getModule().getNBT().getBoolean("openGarage");
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putString("numero", numero);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        numero = nbt.getString("numero");
        if (numero == null)
            numero = "";
    }

    @Override
    public void loadTextures() {

        numero = "";
        setBackground(Bitmap.get("location/garage_calculator"));
        textPainter = new TextPainter(46 * 2, 20 * 2, Game.font);

        garageLoadEvent = new LocationLoadEvent("garage", null);
        garageLoadEvent.setJoesLocation(151, 168);

        Rectangle[] rectangles = new Rectangle[9];
        int indexik = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                rectangles[indexik] = new Rectangle(46 * 2 + 14 * 2 * j, 32 * 2 + 14 * 2 * i, 13 * 2, 13 * 2);
                indexik++;
            }
        }
        getStuffByID("calcu").setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {

                if (!right_button && !released && item != null && item.ITEM.ID == Items.itemBattery.ID) {
                    Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.batteryNoFit"));
                }

                if (!IAction.onlyPressed(right_button, released, item))
                    return true;
                MPlayer2.playSound("cvak_0");
                if (isCalculatorPowered()) {
                    for (int i = 0; i < rectangles.length; i++) {
                        if (rectangles[i].contains(x, y)) {
                            keyPressed(i + 1);
                            return true;
                        }

                    }
                } else {
                    noPowerClicks++;
                    if (noPowerClicks > 2) {
                        noPowerClicks = 0;
                        Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.calcNoPower"));
                    }
                }
                return true;
            }
        });
        getStuffByID("toBottom2").setAction(new IAction() {//todo has to be vissible textname
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!IAction.onlyPressed(right_button, released, item))
                    return true;
                LocationLoadEvent event = new LocationLoadEvent("calculator_bottom");
                Game.core.EVENT_BUS.addEvent(event);
                return true;
            }
        });
        getStuffByID("toBottom2").setTextNameAsName();

    }
}
