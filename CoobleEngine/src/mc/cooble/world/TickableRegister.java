package mc.cooble.world;


import mc.cooble.window.Tickable;

/**
 * Created by Matej on 12.8.2016.
 */
public interface TickableRegister {
    void registerTickable(Tickable tickable);
    boolean removeTickable(Tickable tickable);
}
