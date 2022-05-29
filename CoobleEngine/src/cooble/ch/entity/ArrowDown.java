package cooble.ch.entity;

/**
 * Created by Matej on 18.12.2015.
 */
public class ArrowDown extends Arrow {

    public ArrowDown(String name,boolean big) {
        super(name);
        setBigPos(big, LEFT);
        setActionRectangle(0*2, 75*2, 160*2, 15*2);
        setBitmapLocation(65*2, 74*2);
    }
}
