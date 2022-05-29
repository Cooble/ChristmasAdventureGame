package mc.cooble.event;


import mc.cooble.core.Game;
import mc.cooble.world.LocModule;

/**
 * Created by Matej on 4.8.2016.
 * Loads world with joe,currentLocation etc
 * By varying those 2 attributes default you can set where (location) the game will start
 */
public class WorldLoadEvent implements Event {
    public static final String defaultLocation = "home";
    public static final String defaultModule = "intro";

    @Override
    public void dispatchEvent() {
        Game.setLoadingScreen(true);
        Game.core.EVENT_BUS.addEvent(new Event() {
            @Override
            public void dispatchEvent() {
                Game.getWorld().loadWorldNBT();
                String moduleMID = Game.getWorld().getNBT().getString("current_module");
                LocModule locModule;
                if (moduleMID == null) {
                    locModule = Game.getWorld().modules().getModule(defaultModule);
                } else {
                    locModule = Game.getWorld().modules().getModule(moduleMID);
                }
                Game.getWorld().getUniCreature().readFromNBT(Game.getWorld().getNBT().getNBT("joe"));
                Game.getWorld().setModule(locModule);
                String locationID = Game.getWorld().getNBT().getString("current_location");
                locationID = (locationID != null && !"intro".equals(locationID)) ? locationID : defaultLocation;
                LocationLoadEvent locationLoadEvent = new LocationLoadEvent((locationID), null);
                Game.core.EVENT_BUS.addDelayedEvent(1, locationLoadEvent);
                Game.core.EVENT_BUS.addDelayedEvent(60, () -> Game.setLoadingScreen(false));
            }
        });


    }
}
