package cs.cooble.graphics;

import cs.cooble.core.Game;
import cs.cooble.logger.Log;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.io.File;
import java.util.List;

/**
 * Created by Matej on 10.12.2015.
 * Something like .gif
 * has more inner bitmaps
 * with setting the current index you can chooseNext, what bitmapStack from whole stack will be shown
 */
public class BitmapStack implements BitmapProvider {

    private final Bitmap[] bitmaps;
    private int currentIndex = 0;

    private int xOffset, yOffset;

    private BitmapStack source = null;
    private int height;
    private int width;

    public BitmapStack(Bitmap... bitmaps) {
        this.bitmaps = bitmaps;
        for (Bitmap bitmap : bitmaps) {
            if (bitmap == null)
                Log.println("Cannot load bitmapstack, bitmap is null! ", Log.LogType.ERROR);
        }
        checkIfSameDimensions();
    }

    /**
     * create new instance of bitmapStack in eggshell mode -> cant use flip,rotate,addBitmapStack
     * (used when you need same images but want to have own currentIndex)
     *
     * @param source
     */
    public BitmapStack(BitmapStack source) {
        bitmaps = null;
        this.source = source;
        xOffset = source.xOffset;
        yOffset = source.yOffset;
    }

    /**
     * adds bitmaps from ".gif" file
     * (in one image there are lot of images)
     * numberOfImages will be find out by suffix in name of image
     * Example: joe_6.png means that there are six subimages
     * images must be in a column, not in row!
     *
     * @param dir
     */
    public static BitmapStack getBitmapStack(String dir) {
        Image src = Bitmap.get(dir).getImage();
        String name = dir.substring(dir.length() - 10);
        int index = -1;
        for (int i = name.length() - 1; i >= 0; i--) {
            if (name.charAt(i) == '_') {
                index = i + 1;
                break;
            }
        }
        if (index == -1) {
            new Exception("Cannot make BitmapStack from image called: " + dir).printStackTrace();
        }
        name = name.substring(index, name.length());
        int quantity = Integer.parseInt(name);
        Bitmap[] outs = new Bitmap[quantity];
        Image[] bufferedImages = new Image[quantity];
        for (int i = 0; i < bufferedImages.length; i++) {
            try {
                bufferedImages[i] = new Image(src.getWidth(), src.getHeight() / quantity);
                Graphics g = bufferedImages[i].getGraphics();
                g.drawImage(src, 0, -src.getHeight() / quantity * i);
                g.destroy();
            } catch (SlickException e) {
                e.printStackTrace();
            }

        }
        for (int i = 0; i < outs.length; i++) {
            outs[i] = new Bitmap(bufferedImages[i]);
        }
        return new BitmapStack(outs);
    }

    /**
     * Loads all bitmaps from the resources folder with given names in array
     * <p>
     * Conditions:
     * # path in an array has to be only of image
     *
     * @param dir
     * @return
     */
    public static BitmapStack getBitmapStack(String[] dir) {
        Bitmap[] bitmaps = new Bitmap[dir.length];

        for (int i = 0; i < dir.length; i++) {
            bitmaps[i] = Bitmap.get(dir[i]);
            if(bitmaps[i]==null){
                Log.println("Cannot load bitmapstack, bitmap is null! ", Log.LogType.ERROR);
            }
        }
        return new BitmapStack(bitmaps);

    }

    public static BitmapStack getBitmapStackFromFolder(String dir) {
        int size = 0;
        String[] files = new File(Game.saver.GAME_PATH + "/res/textures/" + dir).list();
        if (files == null || files.length == 0) {
            List<String> l = Game.saver.findResourceC(Game.saver.getRes() + "/textures/" + dir, null);
            if (l != null)
                size = l.size();

        } else size = files.length;
        if (size == 0)
            return null;
        Bitmap[] bitmaps = new Bitmap[size];
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = Bitmap.get(dir + "/" + i);
            if(bitmaps[i]==null){
                Log.println("Cannot load bitmapstack, bitmap is null! ", Log.LogType.ERROR);
            }
        }
        return new BitmapStack(bitmaps);

    }

    @Override
    public Bitmap getCurrentBitmap() {
        if (source != null)
            return source.bitmaps[currentIndex];
        return bitmaps[currentIndex];
    }

    @Override
    public int[] getOffset() {
        return new int[]{xOffset, yOffset};
    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }


    /**
     * @param index
     * @return Bitmap on the given index
     */
    public Bitmap getBitmap(int index) {
        if (source != null)
            return source.bitmaps[index];
        return bitmaps[index];
    }

    /**
     * @return index of currently used Bitmap in stack
     */
    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * @return length of bitmapStack stack
     */
    public int getMaxLength() {
        if (source != null)
            return source.bitmaps.length;
        return bitmaps.length;
    }

    /**
     * Sets the index of currently used bitmapStack
     * if index is higher than getMaxLength() then index will not be changed
     *
     * @param currentIndex if == -1 -> dont render
     */
    public void setCurrentIndex(int currentIndex) {
        if (currentIndex < 0) {
            this.currentIndex = -1;
            return;
        }
        if (currentIndex > (source != null ? source.bitmaps.length : bitmaps.length) - 1)//if source is present checking index of source
            new IndexOutOfBoundsException("Current index is bigger than the bitmaps array[" + getMaxLength() + "]! " + currentIndex).printStackTrace();
        else
            this.currentIndex = currentIndex;

    }


    private void checkIfSameDimensions() {
     /*   for (int i = 1; i < bitmaps.length; i++) {//check if all bitmaps have the same size
            if (!bitmaps[i].equalsSize(bitmaps[0]))
                new IllegalArgumentException("Bitmaps are not same dimension!").printStackTrace();
        }*/
    }

    /**
     * @param vertikal
     * @return new BitmapStack with flipped Bitmaps
     */
    public BitmapStack flipBitmaps(boolean vertikal) {
        if ((source != null)) throw new AssertionError("calling flipBitmaps when this is just eggshell");

        Bitmap[] array = new Bitmap[bitmaps.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = bitmaps[i].flip(vertikal);

        }
        return new BitmapStack(array);
    }

    public BitmapStack rotateBitmaps(float degree) {
        if ((source != null)) throw new AssertionError("calling rotateBitmaps when this is just eggshell");
        Bitmap[] bitmaps1 = new Bitmap[this.bitmaps.length];

        for (int i = 0; i < bitmaps1.length; i++) {
            bitmaps1[i] = this.bitmaps[i].rotateCW(degree);
        }
        return new BitmapStack(bitmaps1);
    }

    /**
     * Creates new BitmapStack which will contain all of bitmaps from bough Bitma
     *
     * @param bitmapStack
     * @return
     */
    public BitmapStack addBitmapStack(BitmapStack bitmapStack) {
        if ((source != null)) throw new AssertionError("calling addBitmapStack when this is just eggshell");

        Bitmap[] bitmaps = new Bitmap[this.bitmaps.length + bitmapStack.bitmaps.length];
        for (int i = 0; i < this.bitmaps.length; i++) {
            bitmaps[i] = this.bitmaps[i];
        }
        for (int i = this.bitmaps.length; i < bitmaps.length; i++) {
            bitmaps[i] = bitmapStack.bitmaps[i - this.bitmaps.length];
        }
        BitmapStack out = new BitmapStack(bitmaps);
        out.setOffset(this.xOffset, this.yOffset);
        return out;
    }

    @Override
    public boolean shouldRender() {
        return currentIndex != -1;
    }

    public Bitmap[] getAllBitmaps() {
        return bitmaps;
    }

    public int getHeight() {
        if (bitmaps.length == 0)
            return 0;
        return bitmaps[0].getHeight();
    }

    public int getWidth() {
        if (bitmaps.length == 0)
            return 0;
        return bitmaps[0].getWidth();
    }

    public BitmapStack resize(double value) {
        if ((source != null)) throw new AssertionError("calling resizeBitmaps when this is just eggshell");
        Bitmap[] bitmaps1 = new Bitmap[this.bitmaps.length];

        for (int i = 0; i < bitmaps1.length; i++) {
            bitmaps1[i] = this.bitmaps[i].resize(value);
        }
        return new BitmapStack(bitmaps1);
    }
}
