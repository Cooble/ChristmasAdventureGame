package cooble.ch.graphics;


import cooble.ch.core.Game;
import cooble.ch.logger.Log;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by Matej on 19.12.2015.
 */
public class BoolMap {
    private final int width;
    private final int height;
    private boolean[] booleans;

    private BoolMap(int WIDTH, int HEIGHT) {

        width = WIDTH;
        height = HEIGHT;
        this.booleans = new boolean[WIDTH * HEIGHT];
    }

    public static BoolMap getBoolMap(String file) {
        file = Game.saver.TEXTURE_PATH + "/bool/" + file + ".png";
        try {
            BufferedImage srcImage = ImageIO.read(Game.saver.getIO().getResourceAsStream(file, false));
            BufferedImage newOne = new BufferedImage(Game.renderer.PIXEL_WIDTH, Game.renderer.PIXEL_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics g = newOne.getGraphics();
            g.drawImage(srcImage, 0, 0, Game.renderer.PIXEL_WIDTH, Game.renderer.PIXEL_HEIGHT, null);
            g.dispose();
            BoolMap boolMap = new BoolMap(newOne.getWidth(), newOne.getHeight());
            int w = newOne.getWidth(null);
            int h = newOne.getHeight(null);
            int[] pixels = new int[w * h];
            newOne.getRGB(0, 0, w, h, pixels, 0, w);


            for (int i = 0; i < pixels.length; i++) {
                boolMap.booleans[i] = (pixels[i] == -16777216);//is black then true
            }

            return boolMap;
        } catch (Exception e) {
            if (!Bitmap.silentConsole) {
                Log.println("Bitmap not found: " + file, Log.LogType.ERROR);
                if (Game.isDebugging)
                    new IIOException("Bitmap not found: " + file).printStackTrace();
            }
        }
        return null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean[] getBooleans() {
        return booleans;
    }

    public boolean getBoolean(int x, int y) {
        int loc = y * width + x;
        if (loc < booleans.length) {
            return booleans[y * width + x];
        }
        return false;
    }

    private void setBoolean(int x, int y, boolean b) {
        booleans[y * width + x] = b;
    }

    public static BoolMap resize(BoolMap boolMap, int newW, int newH) {
        BoolMap out = new BoolMap(newW, newH);
        for (int x = 0; x < out.width; x++) {
            for (int y = 0; y < out.height; y++) {
                out.setBoolean(x, y, boolMap.getBoolean((int) ((double) x / (double) out.width * boolMap.width), (int) ((double) y / (double) out.height * boolMap.height)));
            }
        }
        return out;
    }

    public static BoolMap create(int width, int height, boolean val) {
        BoolMap out = new BoolMap(width, height);
        if (val) {
            for (int x = 0; x < out.width; x++) {
                for (int y = 0; y < out.height; y++) {
                    out.setBoolean(x, y, val);
                }
            }
        }
        return out;
    }
}
