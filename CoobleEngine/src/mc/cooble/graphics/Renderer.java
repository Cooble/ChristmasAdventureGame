package mc.cooble.graphics;

import com.sun.istack.internal.Nullable;
import mc.cooble.core.Game;
import mc.cooble.event.ActionRectangleDrawer;
import mc.cooble.logger.Log;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import java.util.ArrayList;

/**
 * Created by Matej on 6.12.2015.
 * Does everything with rendering bitmaps and size_of_pixel management
 */
public final class Renderer {

    public final byte PIXEL_SIZE;
    public final int PIXEL_SIZE_RATIO = 160 * 2;

    private ArrayList<Runnable> runs = new ArrayList<>();
    private ArrayList<BitmapProvider> bitmapProviders = new ArrayList<>();
    private ArrayList<MultiBitmapProvider> guiProviders = new ArrayList<>();
    private ArrayList<MultiBitmapProvider> topProviders = new ArrayList<>();
    private ArrayList<MultiBitmapProvider> multiBitmapProviders = new ArrayList<>();
    private BitmapProvider background;
    private MultiBitmapProvider foreground;
    private Bitmap shadow;
    private Camera cam;
    private CameraMan cameraMan;

    private boolean blackScreenEnabled;

    public final int SCREEN_WIDTH;
    public final int SCREEN_HEIGHT;
    public final int PIXEL_WIDTH;
    public final int PIXEL_HEIGHT;
    private BitmapProvider dynamicLight;

    /**
     * @param screen_width  pixels real
     * @param screen_height pixels real
     *                      (will be converted To pixelSize ones)
     */
    public Renderer(int screen_width, int screen_height) {
        SCREEN_WIDTH = screen_width;
        SCREEN_HEIGHT = screen_height;
        PIXEL_SIZE = (byte) (SCREEN_WIDTH / PIXEL_SIZE_RATIO);
        PIXEL_WIDTH = SCREEN_WIDTH / PIXEL_SIZE;
        PIXEL_HEIGHT = SCREEN_HEIGHT / PIXEL_SIZE;
        dynamicLight = Bitmap.create(PIXEL_WIDTH, PIXEL_HEIGHT);
        if (Game.isDebugging) {
            Log.println("Screen established with real  dimensions: X=" + SCREEN_WIDTH + " Y=" + SCREEN_HEIGHT);
            Log.println("Screen established with pixel dimensions: X=" + PIXEL_WIDTH + " Y=" + PIXEL_HEIGHT + " PIXEL_SIZE=" + PIXEL_SIZE);
        }
        cam = new Camera(SCREEN_WIDTH, SCREEN_HEIGHT);
        cameraMan = new CameraMan(cam);


    }

    private boolean enableNormalDrawing = false;

    public void setEnableNormalDrawing(boolean enableNormalDrawing) {
        this.enableNormalDrawing = enableNormalDrawing;
    }

    /**
     * sets bitmapStack of background
     * (used in LocationLoadEvent)
     *
     * @param background
     */
    public void setBackground(BitmapProvider background) {
        this.background = background;
    }

    public void setDynamicalLighting(BitmapProvider light) {
        dynamicLight = light;
    }

    /**
     * sets bitmapStack of foreground
     * (used in LocationLoadEvent)
     *
     * @param foreground
     */
    public void setForeground(@Nullable MultiBitmapProvider foreground) {
        this.foreground = foreground;
    }

    public void setShadow(@Nullable Bitmap shadow) {
        this.shadow = shadow;
    }


    public boolean isShadowHandled() {
        return shadow != null;
    }


    public void render(Graphics g) throws SlickException {
        for (Runnable r : runs) {
            if (r != null)
                r.run();
        }
        Graphics.setCurrent(g);
        if(blackScreenEnabled){
            g.setColor(new Color(0,0,0));
            g.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        }else {
            if (cam.getScale() != 1) {
                if (cam.isZoomEnabled()) {
                    g.scale(cam.getScale(), cam.getScale());
                    g.translate(-cam.getX(), -cam.getY());
                }
            }

            if (enableNormalDrawing) {
                if (background != null)
                    drawResizedImage(background.getCurrentBitmap());

                for (BitmapProvider bitmapProvider : bitmapProviders) {
                    if (bitmapProvider.shouldRender()) {
                        drawImage(bitmapProvider, g);
                    }
                }
                renderMultiBitmapProviders(multiBitmapProviders, g);

                if (foreground != null) {
                    BitmapProvider[] bitmapProviders = foreground.getBitmaps();
                    if (bitmapProviders != null)
                        for (BitmapProvider bitmapProvider : bitmapProviders) {
                            if (bitmapProvider != null && bitmapProvider.shouldRender()) {
                                drawResizedImage(bitmapProvider);
                                //screen.draw(bitmapProvider.getCurrentBitmap());
                            }
                        }
                }

                renderShadow(g);
                renderMultiBitmapProviders(guiProviders, g);
            }
            renderAR(g);
            if (!cam.isZoomEnabled() && cam.getScale() != 1) {
                g.setColor(Color.cyan);
                g.drawRect(cam.getX(), cam.getY(), cam.getWidth(), cam.getHeight());
            }
            renderMultiBitmapProviders(topProviders, g);
        }
    }

    private void drawImage(BitmapProvider provider,Graphics g) {
        Image image = provider.getCurrentBitmap().getImage();
        image.setFilter(0);
        int[] offset = provider.getOffset();
        //x1 y1 x2 y2 rx1 ry1 rx2 ry2
        g.drawImage(image,offset[0] * PIXEL_SIZE, offset[1] * PIXEL_SIZE,offset[0] * PIXEL_SIZE+image.getWidth()*PIXEL_SIZE, offset[1] * PIXEL_SIZE+image.getHeight()*PIXEL_SIZE,0,0,image.getWidth(),image.getHeight());

        //image.draw(offset[0] * PIXEL_SIZE, offset[1] * PIXEL_SIZE, provider.getCurrentBitmap().getWidth() * PIXEL_SIZE, provider.getCurrentBitmap().getHeight() * PIXEL_SIZE);
    }

    private void drawResizedImage(BitmapProvider provider) {
        Image image = provider.getCurrentBitmap().getImage();
        image.setFilter(0);
        image.draw(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

    }

    private void renderShadow(Graphics g) {
        if (shadow != null && shadow.shouldRender()) {
            lights(g);
        }
    }

    /**
     * Debugging actionRectangle, help for checking dimensions
     *
     * @param graphics
     */
    private void renderAR(Graphics graphics) {
        if (ActionRectangleDrawer.active) {
            if (ActionRectangleDrawer.bitmap != null) {
                ActionRectangleDrawer.bitmap.getImage().draw(ActionRectangleDrawer.posX * PIXEL_SIZE, ActionRectangleDrawer.posY * PIXEL_SIZE);
            } else {
                graphics.setColor(new Color(41, 128, 228, 100));
                graphics.fillRect(ActionRectangleDrawer.posX * PIXEL_SIZE, ActionRectangleDrawer.posY * PIXEL_SIZE, ActionRectangleDrawer.width * PIXEL_SIZE, ActionRectangleDrawer.height * PIXEL_SIZE);
            }
        }
    }

    private void renderMultiBitmapProviders(ArrayList<MultiBitmapProvider> multiBitmapProviders,Graphics g) {
        for (MultiBitmapProvider m : multiBitmapProviders) {
            if (m.shouldRender()) {
                BitmapProvider[] bitmaps = m.getBitmaps();
                for (BitmapProvider bitmapProvider : bitmaps) {
                    if (bitmapProvider != null)
                        if (bitmapProvider.shouldRender()) {
                            drawImage(bitmapProvider,g);
                            //screen.draw(bitmapProvider);
                        }
                }
            }
        }
    }

    /**
     * adds new bitmapStack to stack of rendered bitmaps
     *
     * @param bitmap
     */
    public void registerBitmapProvider(BitmapProvider bitmap) {
        if (Game.isDebugging && bitmap == null) {
            new Exception("[Renderer] cannot register null as a bitmapProvider").printStackTrace();
        }
        bitmapProviders.add(bitmap);
    }

    public boolean removeBitmapProvider(BitmapProvider bitmap) {
        for (int i = 0; i < bitmapProviders.size(); i++) {
            if (bitmapProviders.get(i).equals(bitmap)) {
                bitmapProviders.remove(i);
                return true;
            }
        }
        return false;
    }

    public void registerMultiBitmapProvider(MultiBitmapProvider bitmap) {
        if (Game.isDebugging && bitmap == null) {
            new Exception("[Renderer] cannot register null as a multibitmapProvider").printStackTrace();
        }
        multiBitmapProviders.add(bitmap);
    }

    public boolean removeMultiProvider(MultiBitmapProvider bitmap) {
        for (int i = 0; i < multiBitmapProviders.size(); i++) {
            if (multiBitmapProviders.get(i).equals(bitmap)) {
                multiBitmapProviders.remove(i);
                return true;
            }
        }
        return false;
    }

    public void registerGUIProvider(MultiBitmapProvider multiBitmapProvider) {
        guiProviders.add(multiBitmapProvider);
    }

    public void removeGuiProvider(MultiBitmapProvider multiBitmapProvider) {
        for (int i = 0; i < guiProviders.size(); i++) {
            if (guiProviders.get(i).equals(multiBitmapProvider)) {
                guiProviders.remove(i);
                return;
            }
        }
    }

    public void clear() {
        bitmapProviders = new ArrayList<>();
    }

    public void clearGUI() {
        guiProviders = new ArrayList<>();
    }


    private void lights(Graphics g) {
        int size = 1;

        /** Scaling stuff so we don't have to use big alpha map image */
        float invSize = 1f / size;
        g.scale(size, size);

        /** setting alpha channel ready so lights add up instead of clipping */
        g.clearAlphaMap();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        int[] loc = Game.input.getMouseListener().getLocation();
        /** actual lights, coordinates are transformed to scaled coordinates */
        if (dynamicLight != null)
            dynamicLight.getCurrentBitmap().getImage().draw(loc[0] - SCREEN_WIDTH / 2, loc[1] - SCREEN_HEIGHT / 2, SCREEN_WIDTH, SCREEN_HEIGHT);

        if (shadow != null)
            shadow.getImage().draw(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        /** Scaling back stuff so we don't have to use big alpha map image */
        g.scale(invSize, invSize);

        /**
         * setting alpha channel for clearing everything but light maps just
         * added
         */
        GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_DST_ALPHA);

        /** paint everything else with black */
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        /** setting drawing mode back to normal */
        g.setDrawMode(Graphics.MODE_NORMAL);

    }

    public CameraMan getCameraMan() {
        return cameraMan;
    }

    public void registerTop(MultiBitmapProvider provider) {
        topProviders.add(provider);
    }

    public void registerRenderable(Runnable r) {
        runs.add(r);
    }

    public void removeRenderable(Runnable r) {
        for (int i = 0; i < runs.size(); i++) {
            if (runs.get(i).equals(r)) {
                runs.remove(i);
                return;
            }
        }
    }

    public void removeTop(MultiBitmapProvider loadingBitmap) {
        for (int i = 0; i < topProviders.size(); i++) {
            if (topProviders.get(i).equals(loadingBitmap)) {
                topProviders.remove(i);
                return;
            }
        }
    }

    public void enableBlackScreen(boolean blackScreenEnabled) {
        this.blackScreenEnabled = blackScreenEnabled;
    }

    public boolean isBlackScreenEnabled() {
        return blackScreenEnabled;
    }
}
