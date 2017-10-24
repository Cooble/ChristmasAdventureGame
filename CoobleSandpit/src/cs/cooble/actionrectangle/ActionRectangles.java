package cs.cooble.actionrectangle;

import java.awt.*;
import java.util.ArrayList;

/**
 *Třida obstaravajici klikání na rectangles. Pokud se kliklo na neci rectangle
 */
public class ActionRectangles {
    private static ActionRectangles ourInstance = new ActionRectangles();

    public static ActionRectangles getInstance() {
        return ourInstance;
    }

    private ActionRectangles() {}

    private ArrayList<IActionRectangle> rectangles = new ArrayList<>();

    public void registerActionRectangle(IActionRectangle actionRectangle){
        rectangles.add(actionRectangle);
    }

    public void removeActionRectangle(Rectangle rectangle){
        for (int i = 0; i < rectangles.size(); i++) {
            if(rectangles.get(i).getRectangle().equals(rectangle)) {
                rectangles.remove(i);
                return;
            }
        }
    }
    public void removeActionRectangle(IActionRectangle iActionRectangle){
        removeActionRectangle(iActionRectangle.getRectangle());
    }
    public IActionRectangle getActionRectangle(String id){
        for (IActionRectangle rectangle:rectangles){
            if(rectangle.getID().equals(id))
                return rectangle;
        }
        return null;
    }

    public boolean click(int x,int y,boolean prave_tlacitko){
        boolean b = false;
        for (int i = 0; i < rectangles.size(); i++) {
            if(rectangles.get(i).getRectangle().contains(x,y)) {
                rectangles.get(i).click(x,y, prave_tlacitko);
                b=true;
            }
            else rectangles.get(i).clickElsewhere();

        }
        return b;
    }

    public void clear() {
        rectangles.clear();
    }
}
