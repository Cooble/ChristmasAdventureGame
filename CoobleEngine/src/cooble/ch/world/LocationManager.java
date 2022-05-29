package cooble.ch.world;


import cooble.ch.core.Game;
import cooble.ch.entity.TextureLoadable;
import cooble.ch.event.UserInput;
import cooble.ch.logger.Log;
import cooble.ch.window.Tickable;
import cooble.ch.xml.XMLManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Loads every Location in module
 * Store all Locations in module
 * Store currently used location
 * Supply ticking to currently used Location
 */
public final class LocationManager implements Tickable, NBTSaveable, TextureLoadable {

    private Location[] locations;

    private int subLocationIndex = -1;

    private int currentIndex;

    public LocationManager(Location[] locations) {
        this.locations = locations;
    }

    @Override
    public void tick() {
        if (locations != null) {
            locations[currentIndex].tick();
            if (subLocationIndex != -1)
                locations[subLocationIndex].tick();
        }
    }

    public Location getCurrentLocation() {
        if (locations == null)
            return null;
        return locations[(subLocationIndex != -1 ? subLocationIndex : currentIndex)];
    }

    public boolean setLocation(String ID) {
            Game.lastLOCID =getCurrentLocationID();
            Game.lastMID =Game.getWorld().getModule().MID;
        removeOnlySubLoc();
        for (int i = 0; i < locations.length; i++) {
            Location location = locations[i];
            if (location.getLOCID().equals(ID)) {
                if (location.isSubLocation()) {
                    subLocationIndex = i;
                    Game.renderer.clear();
                    Game.dialog.setText(null);
                    locations[currentIndex].onStopRendering(Game.renderer);
                    Game.input.unregisterMouseEventConsumer(locations[currentIndex]);
                    Game.input.registerMouseEventConsumer(locations[subLocationIndex], UserInput.LOW_PRIORITY);
                    locations[subLocationIndex].onStart();
                } else {
                    locations[currentIndex].onStop();
                    Game.input.unregisterMouseEventConsumer(locations[currentIndex]);
                    currentIndex = i;
                    Game.renderer.clear();
                    Game.input.registerMouseEventConsumer(locations[currentIndex], UserInput.LOW_PRIORITY);
                    locations[currentIndex].onStart();
                }
                return true;
            }
        }
        return false;
    }

    public void removeSubLocationIfExists() {
        if (isSubLocationEnabled()) {
            locations[subLocationIndex].onStop();
            Game.input.unregisterMouseEventConsumer(locations[subLocationIndex]);
            Game.renderer.clear();
            subLocationIndex = -1;
            Game.input.registerMouseEventConsumer(locations[currentIndex], UserInput.LOW_PRIORITY);
            locations[currentIndex].onStartRendering(Game.renderer);
        }
    }
    private void removeOnlySubLoc(){
        if(isSubLocationEnabled()){
            locations[subLocationIndex].onStop();
            Game.input.unregisterMouseEventConsumer(locations[subLocationIndex]);
            Game.renderer.clear();
            subLocationIndex = -1;
        }
    }

    public boolean isSubLocationEnabled() {
        return subLocationIndex != -1;
    }

    public String getCurrentLocationID() {
        return locations[currentIndex].getLOCID();
    }

    @Override
    public void readFromNBT(NBT nbt) {
        if (locations != null)
            for (Location location : locations) {
                if (location != null)
                    location.readFromNBT(nbt.getNBT("loc_" + location.getLOCID()));
            }
    }

    @Override
    public void writeToNBT(NBT nbt) {
        if (locations != null)
            for (Location location : locations) {
                if (location != null)
                    location.writeToNBT(nbt.getNBT("loc_" + location.getLOCID()));
            }
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * called when you wanna stop all locations running and you dont want to set it to something else
     */
    public void closeCurrentSubAndLocation() {
        if (locations != null) {
            removeSubLocationIfExists();
            locations[currentIndex].onStop();
            Game.input.unregisterMouseEventConsumer(locations[currentIndex]);
        }
    }

    @Override
    public String toString() {
        String out = "locationManager:{";
        for (Location location : locations) {
            out += location.getLOCID() + " \n";
        }
        return out + "}";
    }

    @Override
    public void loadTextures() {
        for (Location location : locations) {
            location.loadTextures();
        }
    }

    /**
     * Looks on xml folder and load all locs in that folder
     * if folderloc does not exist in src code -> no problem it will add it
     *
     * @param currentLocModule needs to check in which xml/module_name
     */
    public void loadXML(LocModule currentLocModule) {
        XMLManager manager = new XMLManager(Game.saver);
        List<String> locationNames = manager.getLocationNames(currentLocModule.MID);
        ArrayList<Location> locationsToAdd = new ArrayList<>();
        for (String locName : locationNames) {
            boolean hasFoundLocInSrc = false;
            for (Location location : locations) {
                if (location.getLOCID().equals(new File(locName).getName())) {//this loc has xml
                    LocationFactory.parseLocation(manager.loadDocument(currentLocModule.MID, locName), location);
                    hasFoundLocInSrc = true;
                    break;
                }
            }
            if (!hasFoundLocInSrc) {//this loc only exists as xml and does not have .java file
                Log.println("Location does not have associated .java file (reading from xml instead): " +currentLocModule.MID + " --- "+locName, Log.LogType.WARN);
                locationsToAdd.add(LocationFactory.parseLocation(manager.loadDocument(currentLocModule.MID, locName), null));

            }

        }
        Location[] newArray = new Location[locations.length + locationsToAdd.size()];
        System.arraycopy(locations, 0, newArray, 0, locations.length);

        for (int i = 0; i < locationsToAdd.size(); i++) {
            newArray[locations.length + i] = locationsToAdd.get(i);
        }
        locations = newArray;
    }
}
