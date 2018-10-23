package mc.cooble.graphics;

import com.sun.istack.internal.NotNull;
import mc.cooble.window.Tickable;

import java.util.Random;

/**
 * Created by Matej on 20.4.2017.
 * Same as BitmapStack but also automatically reacts to change in time.
 * Changes/ alternates images over time.
 *
 * You can choose between TOOTH -> counting up and then again
 * or SAW_TOOTH -> counting up and then counting down
 * You can manually change image when calling changeImage() (recommended not to use tick() when you do manually)
 *
 * You can also choose random mode:
 *  nextImage will be called in random time specified by:
 *      delay=random.nextInt(maxRandom)+base;
 *
 */
public final class Animation implements Tickable, BitmapProvider {
    private BitmapStack bitmaps;
    private int delayTicks;
    private boolean countUp = true;
    /**
     * from 0 to max, then from 0 to max
     */
    public static final int TYPE_SAW_TOOTH = 0;
    /**
     * from 0 to max, then from max to zero
     */
    public static final int TYPE_TOOTH = 1;
    private int type=TYPE_SAW_TOOTH;
    private int maxDelay;

    private int useRandomMax;
    private Random random;
    private int useRandomBase;

    private Runnable onChange;

    private boolean animationEnabled;

    public Animation(BitmapStack bitmaps,int maxDelay) {
        this.bitmaps = bitmaps;
        setDelay(maxDelay);
        setAnimationEnabled(true);
    }
    public Animation(BitmapStack bitmaps,int maxDelay,int type) {
        this(bitmaps,maxDelay);
        this.type=type;

    }

    public void setRandomEnable(@NotNull Random random,int base,int maxRandom){
        this.random = random;
        this.useRandomBase=base;
        this.useRandomMax=maxRandom;
    }
    public void setRandomDisable(){
        random=null;
    }

    public void setDelay(int maxDelay) {
        this.maxDelay = maxDelay;
    }

    public void changeImage() {
        if (countUp) {
            if (bitmaps.getCurrentIndex() != bitmaps.getMaxLength()-1) {
                bitmaps.setCurrentIndex(bitmaps.getCurrentIndex() + 1);
            } else {
                switch (type) {
                    case TYPE_SAW_TOOTH:
                        bitmaps.setCurrentIndex(0);
                        break;
                    case TYPE_TOOTH:
                        countUp = false;
                        bitmaps.setCurrentIndex(bitmaps.getCurrentIndex() - 1);
                        break;
                }
            }
        } else {
            if (bitmaps.getCurrentIndex() == 0) {
                bitmaps.setCurrentIndex(1);
                countUp = true;
            } else bitmaps.setCurrentIndex(bitmaps.getCurrentIndex() - 1);
        }
        if(onChange!=null)
            onChange.run();
    }

    @Override
    public void tick() {
        if(!animationEnabled)
            return;
        if(!bitmaps.shouldRender())
            return;
        delayTicks++;
        if (delayTicks >= maxDelay) {
            delayTicks = 0;
            if(random!=null){
                maxDelay=random.nextInt(useRandomMax)+useRandomBase;
            }
            changeImage();
        }
    }

    public int getCurrentIndex(){
        return bitmaps.getCurrentIndex();
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return bitmaps.getCurrentBitmap();
    }

    @Override
    public int[] getOffset() {
        return bitmaps.getOffset();
    }

    @Override
    public boolean shouldRender() {
        return bitmaps.shouldRender();
    }

    public void setShouldRender(boolean shouldRender) {
            bitmaps.setCurrentIndex(shouldRender?0:-1);
    }

    public void setOnChangeListener(Runnable onChange) {
        this.onChange = onChange;
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        this.animationEnabled = animationEnabled;
    }

    public BitmapStack getBitmapStack() {
        return bitmaps;
    }
}
