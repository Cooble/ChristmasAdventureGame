package cs.cooble.module;

import cs.cooble.location.*;
import cs.cooble.world.LocModule;
import cs.cooble.world.Location;

import java.util.ArrayList;

/**
 * Created by Matej on 4.8.2016.
 */
public class ModuleParty extends LocModule {

    public ModuleParty() {
        super("party");
    }

    @Override
    public Location[] load() {
        ArrayList<Location> locations = new ArrayList<>();
        locations.add(new LocationIntro());
        locations.add(new LocationParty());
        locations.add(new LocationCottageLeft());
        locations.add(new LocationCottageFront());
        locations.add(new LocationCottageRight());
        locations.add(new LocationBusStop());
        locations.add(new LocationGarageOut());
        locations.add(new LocationGarageIn());
        locations.add(new LocationCalculator());
        locations.add(new LocationCalculatorBottom());
        locations.add(new LocationElectricity());
        locations.add(new LocationBathroom());
        locations.add(new LocationPath());
        locations.add(new LocationCellar());
        locations.add(new LocationFly());
        locations.add(new LocationOffice());
        locations.add(new LocationToys());
        locations.add(new LocationHall());
        locations.add(new LocationHall2());
        locations.add(new LocationWhiteboard());
        locations.add(new LocationKumbal());
        locations.add(new LocationLibrary());
        locations.add(new LocationBook());
        locations.add(new LocationBlueprint());
        locations.add(new LocationQuadraTrip());

        this.locations = new Location[locations.size()];
        this.locations = locations.toArray(this.locations);


        return this.locations;

    }
}
