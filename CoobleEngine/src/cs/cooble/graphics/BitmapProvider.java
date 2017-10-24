package cs.cooble.graphics;

/**
 * Used whether is needed to render static image =(Bitmap) or gif =(BitmapStack) without separating anyone of them
 */
public interface BitmapProvider extends MultiBitmapProvider{

    /**
     * @returns bitmapStack of object
     */
    Bitmap getCurrentBitmap();

    int[] getOffset();

    default boolean shouldRender() {
        return true;
    }

    @Override
    default BitmapProvider[] getBitmaps(){
        return new Bitmap[]{getCurrentBitmap()};
    }
}
