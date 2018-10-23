package mc.cooble.entity;

/**
 * Created by Matej on 18.12.2015.
 */
public class ArrowLeft extends Arrow {


    public ArrowLeft(String name,boolean big) {
        super(name);
        setBigPos(big,Arrow.LEFT);
        setActionRectangle(0,0,16*2,90*2);
        setBitmapLocation(-28,60);
    }
}
