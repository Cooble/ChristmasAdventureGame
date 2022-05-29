package mc.cooble.world;

import com.sun.istack.internal.Nullable;
import mc.cooble.core.Game;
import mc.cooble.entity.UniCreature;
import mc.cooble.inventory.Inventory;
import mc.cooble.logger.Log;
import mc.cooble.window.Tickable;

/**
 * Created by Matej on 22.7.2016.
 */
public class World implements Tickable {
    private NBT worldNbt;//used only to share data among locations, not for personal location use, f. ex. not to store things like whether mailbox is opened, but general public things like have player already looked at something. this NBT does not belong to module so its not cleared when module changed

    private CModules modules;
    private CItems items;
    private LocationManager locationManager;
    private Inventory inventory;
    private LocModule currentLocModule;
    private UniCreature uniCreature;

    public World(@Nullable NBT worldNbt) {

        modules = new CModules();
        items = new CItems();

        this.worldNbt = worldNbt;
        inventory = new Inventory();


    }

    public void loadWorldNBT() {
        if (worldNbt != null) {
            inventory.readFromNBT(getInventoryNBT());
        } else {
            this.worldNbt = new NBT();
        }
    }

    private NBT getInventoryNBT() {
        if (worldNbt == null)
            return null;
        return worldNbt.getNBT("inventory");
    }

    public CModules modules() {
        return modules;
    }

    public CItems items() {
        return items;
    }

    /**
     * saves current Module and loads new one
     * if locModule == null then only save current one(usually called on game exit event)
     *
     * @param locModule
     */
    public void setModule(@Nullable LocModule locModule) {
        if (Game.isDebugging) {
            Log.println("[Module set from ]" + (currentLocModule != null ? currentLocModule.MID : "null") + " to " + (locModule != null ? locModule.MID : "null"));
        }
        if (locModule != null && this.currentLocModule != null) {//ingoring same module it will still reload it!
            if (this.currentLocModule.MID.equals(locModule.MID))
                return;//same module
        }
        if (currentLocModule != null) {
            this.locationManager.closeCurrentSubAndLocation();
            this.locationManager.writeToNBT(currentLocModule.getNBT());
            Game.saver.saveModuleNBT(currentLocModule.getNBT(), currentLocModule.MID);
            currentLocModule.clear();
        }
        this.currentLocModule = locModule;
        if (locModule == null) {
            return;
        }
        this.currentLocModule.setNBT(Game.saver.loadModuleNBT(locModule.MID));
        locationManager = new LocationManager(this.currentLocModule.load());
        locationManager.loadXML(this.currentLocModule);
        locationManager.loadTextures();
        locationManager.readFromNBT(currentLocModule.getNBT());
        System.gc();
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public NBT getNBT() {
        return worldNbt;
    }

    public Inventory inventory() {
        return inventory;
    }


    @Override
    public void tick() {
        locationManager.tick();
        inventory.tick();
    }

    public LocModule getModule() {
        return currentLocModule;
    }

    public UniCreature getUniCreature() {
        return uniCreature;
    }

    public void setUniCreature(UniCreature uniCreature) {
        this.uniCreature = uniCreature;
    }

    public void saveWorld() {
         if (getInventoryNBT() == null) {//game stopped at intro screen
             setModule(null);

             return;

         }
        inventory.writeToNBT(getInventoryNBT());
        if (locationManager.getCurrentLocation() != null && "intro".equals(locationManager.getCurrentLocationID())) {
            if (Game.paused) {
                worldNbt.putString("current_module", Game.pauseMID);
                worldNbt.putString("current_location", Game.pauseLOCID);
            } else {
                worldNbt.putString("current_module", Game.lastMID);
                worldNbt.putString("current_location", Game.lastLOCID);
            }
        } else {
            worldNbt.putString("current_module", currentLocModule.MID);
            worldNbt.putString("current_location", locationManager.getCurrentLocationID());
        }
        NBT joeNbt = new NBT();
        uniCreature.writeToNBT(joeNbt);
        worldNbt.putNBT("joe", joeNbt);
        setModule(null);
    }
}
