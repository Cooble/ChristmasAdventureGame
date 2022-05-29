package cooble.ch.actionrectangle;


import java.awt.*;

/**
 * Created by Matej on 7.3.2015.
 */
public interface IActionRectangle {

    Rectangle getRectangle();

    default void mouseMove(int x,int y){}

    default String getID(){return "not specified";}

    /**
     * Called when was clicked to this rectangle
     * @param prave_tlacitko
     */

    void click(int x, int y, boolean prave_tlacitko);

    void clickElsewhere();

}
