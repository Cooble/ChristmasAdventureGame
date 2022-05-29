package cooble.ch.location;

import cooble.ch.core.Game;
import cooble.ch.entity.IMovable;
import cooble.ch.entity.Mover;
import cooble.ch.entity.Position;
import cooble.ch.entity.Weather;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.graphics.Bitmap;
import cooble.ch.graphics.BitmapProvider;
import cooble.ch.graphics.BitmapStack;
import cooble.ch.graphics.Renderer;
import cooble.ch.module.Modules;
import cooble.ch.music.MPlayer2;
import cooble.ch.world.Location;

import java.util.function.Supplier;

/**
 * Created by Matej on 12.12.2015.
 */
public final class LocationBusTrip extends Location {
    IMovable bus;
    int[] pos;
    BitmapProvider bitmapProviderBus;
    Weather weather;

    public LocationBusTrip() {
        super("ice_plain");
        setJoeProhibited(true);

    }

    @Override
    public void onStart() {
        super.onStart();
        MPlayer2.playSound("bus");
        Game.getWorld().inventory().lock(true);
        registerTickable(new Mover(bus, new Position(220*2, -5*2), 2, () -> {
            //todo demo
         /*  Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("intro"));
            GameExitEvent event = new GameExitEvent();
          event.setRestart(false);
            event.setClear(true);
          Game.core.EVENT_BUS.addEvent(event);*/
            Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("party", Modules.moduleParty.MID));
        }));
        Game.input.muteInput(true);
        for (int i = 0; i < Game.core.TARGET_TPS * 10; i++) {
            weather.tick();
        }
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);
        Game.renderer.registerBitmapProvider(bitmapProviderBus);
    }

    @Override
    public void onStop() {
        super.onStop();
        MPlayer2.stopSound("bus");
        Game.input.muteInput(false);


    }

    @Override
    public void loadTextures() {
        setBackground(Bitmap.get("location/ice_plain"));
        Bitmap busMap = Bitmap.get("item/bus").flip(false);
        pos = new int[2];
        pos[0] = -250*2;
        pos[1] = 0;
        bus = new IMovable() {
            @Override
            public int getX() {
                return pos[0];
            }

            @Override
            public int getY() {
                return pos[1];
            }

            @Override
            public void setX(int x) {
                pos[0] = x;
            }

            @Override
            public void setY(int y) {
                pos[1] = y;

            }

            @Override
            public void setIsMoving(boolean isMoving) {

            }
        };
        bitmapProviderBus = new BitmapProvider() {
            @Override
            public Bitmap getCurrentBitmap() {
                return busMap;
            }

            @Override
            public int[] getOffset() {
                return pos;
            }
        };
        weather = new Weather(new Supplier<Bitmap>() {
            BitmapStack b =  BitmapStack.getBitmapStackFromFolder("item/snow_spark").resize(0.5);
            @Override
            public Bitmap get() {
                return b.getBitmap(Game.random.nextInt(b.getMaxLength())).copy();
            }
        });
        weather.setSpeed(-5,0.85);
        weather.setCadence(35);
        weather.spawnWidth(3);
        registerTickable(weather);
        weather.setEnabled(true);
    }
}
