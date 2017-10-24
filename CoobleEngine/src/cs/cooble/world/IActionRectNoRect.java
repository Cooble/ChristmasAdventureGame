package cs.cooble.world;

import java.awt.*;

/**
 * Created by Matej on 5.2.2017.
 */
public interface IActionRectNoRect extends IActionRectangle {
    @Override
    default Rectangle getRectangle(){
        return null;
    }
}
