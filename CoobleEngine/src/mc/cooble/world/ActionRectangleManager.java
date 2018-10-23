package mc.cooble.world;


import mc.cooble.core.Game;
import mc.cooble.event.MouseEventConsumer;
import mc.cooble.graphics.CollectionManager;
import mc.cooble.inventory.item.ItemStack;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Created by Matej on 12.12.2015.
 */
public class ActionRectangleManager implements MouseEventConsumer, CollectionManager<Supplier<IActionRectangle>> {
    private ArrayList<Supplier<IActionRectangle>> actionRectangles;

    public ActionRectangleManager() {
        actionRectangles = new ArrayList<>();
    }

    @Override
    public boolean consume(int x, int y, int state, boolean released) {
        if (state == MouseEventConsumer.MOUSE_MOVED || state == MouseEventConsumer.MOUSE_DRAGGED) {//moving
            for (Supplier<IActionRectangle> actionListener : actionRectangles) {
                if (actionListener != null) {
                    if (state == MouseEventConsumer.MOUSE_DRAGGED)
                        actionListener.get().mouseDragged(x, y);
                    else
                        actionListener.get().mouseMove(x, y);
                }
            }
            return true;

        } else {
            for (Supplier<IActionRectangle> actionListener : actionRectangles) {
                if (actionListener == null || actionListener.get() == null || actionListener.get().getRectangle() == null)
                    continue;
                if (actionListener.get().getRectangle().contains(x, y)) {
                    ItemStack item = null;
                    if (Game.getWorld().inventory().isOpen())
                        item = Game.getWorld().inventory().getItemInHand();
                    if(actionListener.get().onClicked(x, y, state == MouseEventConsumer.CLICKED_RIGHT, released, item))
                        return true;
                }
            }
            return false;
        }

    }

    @Override
    public void register(Supplier<IActionRectangle> thing) {
        actionRectangles.add(thing);
    }

    public void register(IActionRectangle thing) {
        actionRectangles.add(new Supplier<IActionRectangle>() {
            @Override
            public IActionRectangle get() {
                return thing;
            }
        });
    }

    @Override
    public boolean remove(Supplier<IActionRectangle> actionListener) {
        for (int i = 0; i < actionRectangles.size(); i++) {
            if (actionRectangles.get(i).get().equals(actionListener.get())) {
                actionRectangles.remove(i);

                return true;
            }
        }
        return false;
    }

    public boolean remove(IActionRectangle actionListener) {
        for (int i = 0; i < actionRectangles.size(); i++) {
            if (actionRectangles.get(i).get().equals(actionListener)) {
                actionRectangles.remove(i);

                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        actionRectangles = new ArrayList<>();
    }

    public boolean contains(IActionRectangle actionRectangle) {
        for (Supplier<IActionRectangle> actionRectangle1 : actionRectangles) {
            if (actionRectangle1.get().equals(actionRectangle)) {
                return true;
            }
        }
        return false;
    }
}
