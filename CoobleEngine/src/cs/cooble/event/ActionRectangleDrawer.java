package cs.cooble.event;

import com.sun.istack.internal.Nullable;
import cs.cooble.graphics.Bitmap;

/**
 * Created by Matej on 31.12.2015.
 */
@Deprecated
public final class ActionRectangleDrawer {
    public static int posX, posY, width = 20, height = 20;
    public static boolean active;
    public static Bitmap bitmap;

    public static void setBitmap(@Nullable Bitmap bitmap){
        ActionRectangleDrawer.bitmap=bitmap;
        if(bitmap!=null){
            ActionRectangleDrawer.width=bitmap.getWidth();
            ActionRectangleDrawer.height=bitmap.getHeight();
        }
    }
    public static String getString(){
        return "Rectangle x " + ActionRectangleDrawer.posX + " y " + ActionRectangleDrawer.posY + " width " + ActionRectangleDrawer.width + " height " + ActionRectangleDrawer.height;
    }

    public static String getClipboardString() {
        return posX + "," + posY + "," + width + "," + height;
    }
}
