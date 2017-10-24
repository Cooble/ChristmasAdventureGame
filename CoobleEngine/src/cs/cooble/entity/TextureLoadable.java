package cs.cooble.entity;

/**
 * Has some bitmaps to be loaded
 * very important is to use specifically this method to load bitmaps from GameSrc/textures
 * if textures are loaded outside this method -> problem may occur!
 * Called after everything is successfully set.
 *
 * In this method Bitmap.getBitmap() can be used non violently
 */
public interface TextureLoadable {
    /**
     * called before nbt read
     */
    void loadTextures();
}
