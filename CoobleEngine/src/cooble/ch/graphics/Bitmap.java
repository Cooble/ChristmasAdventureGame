package cooble.ch.graphics;


import cooble.ch.core.Game;
import cooble.ch.logger.Log;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.BufferedImageUtil;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Matej on 6.12.2015.
 */
public final class Bitmap implements BitmapProvider {

    public static boolean silentConsole;

    private int xOffset = 0;
    private int yOffset = 0;
    private boolean shouldRender;
    private Image image;

    private Bitmap() {
        shouldRender = true;
    }

    protected Bitmap(Image image) {
        this.image = image;
        shouldRender = true;
    }

    private Bitmap(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        shouldRender = true;

    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void setOffset(int[] offset) {
        this.xOffset = offset[0];
        this.yOffset = offset[1];
    }

    public Bitmap resize(double scale) {
        return resize(this, (int) (getWidth() * scale), (int) (getHeight() * scale));
    }

    public Bitmap resize(int newW, int newH) {
        return resize(this, newW, newH);
    }


    /**
     * @param file
     * @return bitmapStack which was loaded from res folder and path of file
     */
    public static Bitmap get(String file) {
        return get(file, false);
    }

    /**
     * does not print errors if no bitmapStack found
     *
     * @param file
     * @return bitmapStack which was loaded from res folder and path of file
     */
    public static Bitmap getIfExists(String file) {
        return get(file, true);
    }

    /**
     * does not print errors if no bitmapStack found silent
     *
     * @param file
     * @return bitmapStack which was loaded from res folder and path of file
     */
    private static Bitmap get(String file, boolean silent) {
        String file1 = Game.saver.TEXTURE_PATH + file + ".png";
        try {
            InputStream is = Game.saver.getIO().getResourceAsStream(file1, false);
            BufferedImage bufferedImage = ImageIO.read(is);
            Bitmap bitmap = new Bitmap(0, 0);
            bitmap.image = new Image(BufferedImageUtil.getTexture(file1, bufferedImage));
            return bitmap;
        } catch (Exception e) {
            try {
                BufferedImage bufferedImag = ImageIO.read(new File(Game.saver.GAME_PATH + "/res/textures/" + file + ".png"));
                Bitmap bitmap = new Bitmap(0, 0);
                bitmap.image = new Image(BufferedImageUtil.getTexture(file, bufferedImag));
                return bitmap;

            } catch (IOException e1) {
                if (!silent && !silentConsole) {
                    Log.println("Bitmap not found: " + file1, Log.LogType.ERROR);
                    if (Game.isDebugging)
                        new IIOException("Bitmap not found: " + file1).printStackTrace();
                }
            }

        }
        return null;
    }


    public static Bitmap getViolently(File file) {
        try {
            //ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            //InputStream is = classloader.getResourceAsStream(file);
            BufferedImage bufferedImage = ImageIO.read(file);
            Bitmap bitmap = new Bitmap(0, 0);
            bitmap.image = new Image(BufferedImageUtil.getTexture(file.getAbsolutePath(), bufferedImage));
            return bitmap;
        } catch (Exception e) {
            Log.println("Bitmap not found: " + file, Log.LogType.ERROR);
            if (Game.isDebugging)
                e.printStackTrace();
        }
        return null;
    }

    /**
     * draws bitmapStack onto current bitmapStack with specified offset
     *
     * @param offset
     * @param bitmap
     */
    public void draw(int[] offset, Bitmap bitmap) {
        draw(offset[0], offset[1], bitmap);

    }

    /**
     * draws bitmapStack onto current bitmapStack with specified offset
     *
     * @param bitmapProvider
     */
    public void draw(BitmapProvider bitmapProvider) {
        draw(bitmapProvider.getOffset(), bitmapProvider.getCurrentBitmap());
    }

    /**
     * draws bitmapStack onto current bitmapStack with specified offset
     * doesn not work right it needs drawimage with some nonealpha color -> god knows why
     *
     * @param xOffset
     * @param yOffset
     * @param bitmap
     */
    public void draw(int xOffset, int yOffset, Bitmap bitmap) {

        try {
            Graphics graphics = image.getGraphics();
            graphics.drawImage(bitmap.image, xOffset, yOffset, new Color(128, 128, 128));
            graphics.flush();

        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return this;
    }


    /**
     * @param vertical
     * @return newly created inverted bitmapStack
     * Example Horizontal:
     * #old [x=0]
     * #WIDTH = 10
     * #new [x=10] (WIDTH-x)
     */
    public Bitmap flip(boolean vertical) {
        Bitmap out = new Bitmap();
        out.image = createFlipped(image, vertical);
        return out;
    }

    public Bitmap rotateCW(float angle) {
        return new Bitmap(createRotated(image, angle));
    }

    private static Image createFlipped(Image image, boolean vertical) {
        image = image.copy();
        image.setFilter(0);
        return image.getFlippedCopy(!vertical, vertical);
    }

    private static Image createRotated(Image image, float angle) {
        //angle/=10;

        try {
            Image out = new Image(image.getWidth(), image.getHeight());
            Graphics g = out.getGraphics();
            g.rotate(out.getWidth() / 2, out.getHeight() / 2, angle);
            g.drawImage(image, 0, 0);
            g.flush();
            return out;

        } catch (SlickException e) {
            e.printStackTrace();
        }
    /*    out.setFilter(0);
        //out.setCenterOfRotation(out.getWidth()/2, out.getHeight()/2);
        Log.println("width "+out.getWidth());
        Log.println("h "+out.getHeight());
        out.setCenterOfRotation(15, 15);
        out.setRotation(angle);*/
        return null;
    }


    /**
     * @param bitmap to compare with
     * @return true if WIDTH=WIDTH & HEIGHT==HEIGHT
     */
    public boolean equalsSize(Bitmap bitmap) {
        return bitmap.getWidth() == getWidth() && bitmap.getHeight() == getHeight();
    }

    @Override
    public int[] getOffset() {
        return new int[]{xOffset, yOffset};
    }

    @Override
    public boolean shouldRender() {
        return shouldRender;
    }

    public void setShouldRender(boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    public static Bitmap create(int pixel_width, int pixel_height) {
        Bitmap out = new Bitmap();
        try {
            out.image = new Image(pixel_width, pixel_height);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        return out;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public static Bitmap create(int pixel_width, int pixel_height, Color color) {
        Bitmap out = new Bitmap();

        Graphics g = null;
        try {
            out.image = new Image(pixel_width, pixel_height);
            g = out.image.getGraphics();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        g.setColor(color);
        g.fillRect(0, 0, pixel_width, pixel_height);
        g.flush();
        return out;
    }

    public static Bitmap resize(Bitmap img, int newW, int newH) {
        img.getImage().setFilter(0);
        return new Bitmap(img.getImage().getScaledCopy(newW, newH));
    }

    public static Bitmap resize(Bitmap img, double scale) {
        return resize(img, (int) (img.getWidth() * scale), (int) (img.getHeight() * scale));
    }

    public void clear() {
        try {
            image.getGraphics().clear();
            image.getGraphics().flush();
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }

    public Bitmap copy() {
        Bitmap out = new Bitmap(this.image);
        out.setOffset(xOffset, yOffset);
        return out;
    }
}
