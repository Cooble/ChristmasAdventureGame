package mc.cooble.duck;

import mc.cooble.canvas.BitmapProvider;

/**
 * Created by Matej on 18.5.2017.
 */
public interface ListViewItem extends BitmapProvider {

    Type getType();

    String getID();

    default void onSelected() {

    }

    default void onDeselected() {

    }

    default void tick(){}

    public enum Type{
        STUFF,ARROW,LOCATION
    }
}
