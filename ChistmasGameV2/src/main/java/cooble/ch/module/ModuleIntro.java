package cooble.ch.module;

import cooble.ch.location.*;
import cooble.ch.world.LocModule;
import cooble.ch.world.Location;

import java.util.ArrayList;

/**
 * Created b
 */
public final class ModuleIntro extends LocModule {
    public ModuleIntro() {
        super("intro");
    }

    public Location[] load() {
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(new LocationIntro());
        locations.add(new LocationHome());
        locations.add(new LocationMail());
        locations.add(new LocationPostBox());
        locations.add(new LocationExit());
        //locations.add(new LocationPause());
        locations.add(new LocationBusTrip());


        this.locations = new Location[locations.size()];
        this.locations = locations.toArray(this.locations);


        return this.locations;
    }
}
