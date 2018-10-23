package mc.cooble.duck;

import com.sun.istack.internal.Nullable;
import mc.cooble.canvas.Bitmap;
import mc.cooble.fx.Controller;

import java.awt.*;

/**
 * Created by Matej on 18.5.2017.
 */
public final class Stuff extends ActionRectangleOwner {
    private Bitmap[] bitmaps;
    private Bitmap bitmapBitmap;
    public int xToCome = 0, yToCome = 0;
    private boolean isToCome;
    private boolean isPickupable;
    private double scale;

    private static final Color selectColor = new Color(0, 255, 0, 100);
    private static final Color blankColor = new Color(0, 0, 0, 0);
    private static final Color actionRectangleColor = new Color(20, 200, 255, 50);
    private int currentIndex;
    private boolean countUp;
    private int type;//saw=0 tooth=1 none=-1
    private int delay;
    private int maxDelay;

    public Stuff(String id) {
        this.id = id;
        actionBitmap = Bitmap.create(100, 100, actionRectangleColor);
        actionBitmapActive = false;
        scale = 1;
        type=-1;
    }

    /**
     * //saw=0 tooth=1 none=-1
     * @return
     */
    public int getAnimationType(){
        return type;
    }

    @Override
    public void tick() {
        if(!isAnimation())
            return;
        if(bitmaps==null||bitmaps.length<2)
            return;
        delay++;
        if (delay >= maxDelay) {
            delay = 0;
            if (countUp) {
                if (currentIndex != bitmaps.length-1) {
                    currentIndex = currentIndex + 1;
                } else {
                    switch (type) {
                        case 0:
                            currentIndex = 0;
                            break;
                        case 1:
                            countUp = false;
                            currentIndex = currentIndex - 1;
                            break;
                    }
                }
            } else {
                if (currentIndex == 0) {
                    currentIndex = 1;
                    countUp = true;
                } else currentIndex--;
            }
        }
    }

    public void setMaxDelay(int maxDelay) {
        this.maxDelay = maxDelay;
    }

    /**
     * //saw=0 tooth=1
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    public void setBitmapOffset(int x, int y) {
        if (bitmaps != null) {
            for (Bitmap bitmap : bitmaps) {
                bitmap.setOffset(x * Controller.RATIO, y * Controller.RATIO);
            }
            bitmapBitmap.setOffset(x * Controller.RATIO, y * Controller.RATIO);
        }
    }

    public int getBitmapOffsetX() {
        if (bitmaps == null)
            return 0;
        return bitmaps[0].getOffsetX() / Controller.RATIO;
    }

    public int getBitmapOffsetY() {
        if (bitmaps == null)
            return 0;
        return bitmaps[0].getOffsetY() / Controller.RATIO;
    }

    public Bitmap getBitmap() {
        if(bitmaps==null)
            return null;
        return bitmaps[currentIndex];
    }

    @Override
    public Type getType() {
        return Type.STUFF;
    }

    @Override
    public Bitmap[] getBufferedImages() {
        return new Bitmap[]{getBitmap(), bitmapBitmap, actionBitmap};
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public void setBitmap(@Nullable Bitmap[] bitmaps) {
        if (bitmaps != null) {
            this.bitmaps = bitmaps;
            this.bitmapBitmap = Bitmap.create(bitmaps[0].getWidth(), bitmaps[0].getHeight(), blankColor);
            this.bitmapBitmap.setOffset(bitmaps[0].getOffsetX(), bitmaps[0].getOffsetY());

            setActionOffset(bitmaps[0].getOffsetX() / Controller.RATIO, bitmaps[0].getOffsetY() / Controller.RATIO);
            setActionDimensions(bitmaps[0].getWidth() / Controller.RATIO, bitmaps[0].getHeight() / Controller.RATIO);
        } else {
            this.bitmaps = null;
        }
    }

    @Override
    public void onDeselected() {
        super.onDeselected();
        if (bitmaps != null) {
            bitmapBitmap = Bitmap.create(bitmaps[0].getWidth(), bitmaps[0].getHeight(), blankColor);
            bitmapBitmap.setOffset(bitmaps[0].getOffsetX(), bitmaps[0].getOffsetY());
        }
    }

    @Override
    public void onSelected() {
        this.setActionBitmapActive(Controller.isArMode());
        super.onSelected();
        if (bitmaps != null) {
            bitmapBitmap = Bitmap.create(bitmaps[0].getWidth(), bitmaps[0].getHeight(), selectColor);
            bitmapBitmap.setOffset(bitmaps[0].getOffsetX(), bitmaps[0].getOffsetY());
        }
    }


    public void setToCome(int x, int y) {
        xToCome = x * Controller.RATIO;
        yToCome = y * Controller.RATIO;
    }

    public int getxToCome() {
        return xToCome / Controller.RATIO;
    }

    public int getyToCome() {
        return yToCome / Controller.RATIO;
    }

    public void setIsToCome(boolean isToCome) {
        this.isToCome = isToCome;
    }

    public boolean isToCome() {
        return isToCome;
    }

    public boolean isPickupable() {
        return isPickupable;
    }

    public void setIsPickupable(boolean isPickupable) {
        this.isPickupable = isPickupable;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
        if (bitmaps != null) {
            for (Bitmap bitmap : bitmaps) {
                bitmap.scale(scale * Controller.RATIO);
            }
            bitmapBitmap = new Bitmap(bitmaps[0].getWidth(), bitmaps[0].getHeight());
        }
    }

    public boolean isAnimation() {
        return isBitmapStack()&&maxDelay!=0;
    }
    public boolean isBitmapStack() {
        return bitmaps!=null&&bitmaps.length>1;
    }

    public int getDelay() {
        return maxDelay;
    }

    public void setAnimationType(int type) {
        this.type=type;
    }

    public int getMaxDelay() {
        return maxDelay;
    }
}
