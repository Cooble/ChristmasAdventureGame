package cs.cooble.graphics;

/**
 * Inteface of gui (get multiple bitmapProviders as one object)
 */
public interface MultiBitmapProvider {


    BitmapProvider[] getBitmaps();

    boolean shouldRender();
}
