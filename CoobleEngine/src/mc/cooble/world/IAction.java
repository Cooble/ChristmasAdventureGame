package mc.cooble.world;

import mc.cooble.inventory.item.ItemStack;

/**
 * Created by Matej on 17.5.2017.
 */
public interface IAction {
    /**
     * called once when cursor get to the rectangle
     */
    default void onFocusGiven() {
    }

    /**
     * called once when cursor leaves the rectangle
     */
    default void onFocusLost() {
    }

    /**
     * Called when was clicked to this rectangle
     *
     * @param right_button
     * @param released     false =pressed true = released
     * @return true if event was consumed
     */
    default boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
        return false;
    }

    /**
     *
     * @param x pixel (not real)
     * @param y pixel (not real)
     */
    default void mouseMove(int x, int y) {
    }
    /**
     *
     * @param x pixel (not real)
     * @param y pixel (not real)
     */
    default void mouseDragged(int x, int y) {
    }

    /**
     * Called when was clicked elsewhere (not on this rectangle)
     */
    default void clickElsewhere() {
    }

    /**
     * Called when is wheeled(doesnt have to be on this rectangle)
     *
     * @param otocka
     */
    default void mousewheeled(int x, int y, int otocka) {
    }

    /**
     *
     * @param right_button
     * @param released
     * @param item
     * @return
     */
    static boolean onlyPressed(boolean right_button,boolean released,ItemStack item){
        return !right_button&&!released&&item==null;
    }
}
