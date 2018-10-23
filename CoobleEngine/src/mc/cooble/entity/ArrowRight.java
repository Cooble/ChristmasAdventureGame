package mc.cooble.entity;

/**
 * Created by Matej on 18.12.2015.
 */
public class ArrowRight extends Arrow {
    public ArrowRight(String name,boolean big) {
        super(name);
        setBigPos(big,Arrow.LEFT);
        setActionRectangle(145 * 2, 0 * 2, 15 * 2, 90 * 2);
        setBitmapLocation(144 * 2, 30 * 2);
    }
}
