package cs.cooble.event;

/**
 * Created by Matej on 2.10.2016.
 *
 * PostInitEvent
 *              calls World.loadNBT() to load its NBT
 *              looks what Modules/Location loadNow
 */
public interface PostInitEvent {
    void postInit();
}
