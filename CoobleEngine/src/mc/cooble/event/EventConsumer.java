package mc.cooble.event;

import mc.cooble.inventory.item.ItemStack;

/**
 * Created by Matej on 1.1.2016.
 */
public interface EventConsumer {
    /**
     * @return true if event was consumed
     * false if should be carried on
     * @param x
     * @param y
     * @param right_button
     * @param released
     * @param item
     */
    boolean consumeEvent(int x, int y, boolean right_button, boolean released, ItemStack item);
}
