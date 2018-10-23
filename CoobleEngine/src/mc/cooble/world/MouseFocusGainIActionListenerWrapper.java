package mc.cooble.world;


import mc.cooble.inventory.item.ItemStack;

import java.awt.*;

/**
 * Created by Matej on 7.3.2015.
 */
public class MouseFocusGainIActionListenerWrapper implements IActionRectangle {

    private boolean focuseState;
    private IActionRectangle iActionListener;

    public MouseFocusGainIActionListenerWrapper(IActionRectangle iActionListener) {
        this.iActionListener = iActionListener;
    }

    /**
     * Called when the mouse is moving( does not have to be on this rectangle)
     *
     * @param x
     */
    @Override
    public final void mouseMove(int x, int y) {

        if (getRectangle()!=null&&getRectangle().contains(x, y)) {
            if (!focuseState) {
                focuseState = true;
                iActionListener.onFocusGiven();
            }
        } else {
            if (focuseState) {
                focuseState = false;
                iActionListener.onFocusLost();
            }

        }
        iActionListener.mouseMove(x, y);
    }

    @Override
    public Rectangle getRectangle() {
        return iActionListener.getRectangle();
    }

    @Override
    public void onFocusGiven() {
        iActionListener.onFocusGiven();
    }

    @Override
    public void onFocusLost() {
        iActionListener.onFocusLost();
    }

    @Override
    public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
        return iActionListener.onClicked(x, y, right_button, released, item);
    }

    @Override
    public void clickElsewhere() {
        iActionListener.clickElsewhere();
    }

    @Override
    public void mousewheeled(int x, int y, int otocka) {
        iActionListener.mousewheeled(x, y, otocka);
    }
}
