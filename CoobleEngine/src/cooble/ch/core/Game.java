package cooble.ch.core;


import cooble.ch.event.InitEvent;
import cooble.ch.event.PostInitEvent;
import cooble.ch.event.PreInitEvent;
import cooble.ch.event.UserInput;
import cooble.ch.graphics.Bitmap;
import cooble.ch.graphics.Renderer;
import cooble.ch.graphics.dialog.DialogManager;
import cooble.ch.music.VPlayer;
import cooble.ch.saving.Saver;
import cooble.ch.translate.Translator;
import cooble.ch.world.CustomSettings;
import cooble.ch.world.NBT;
import cooble.ch.world.World;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class with gameCore,settings,saver,world, lot of attributes
 * Handles init events
 */
public final class Game {
    private static int WIDTH = 1280, HEIGHT = WIDTH / 16 * 9;//1280/8=160;720/8=90
    public static Renderer renderer;
    public static boolean isDebugging = false;
    public static UserInput input;
    public static TrueTypeFont font;
    public static TrueTypeFont smallFont;
    public static String[] error;
    public static Random random = new Random();
    public static String gameName = "CoobleEngine";
    private static CustomSettings settings = new CustomSettings();
    public static Saver saver = new Saver(gameName,CoobleLauncher.class);
    private static World world;
    public static boolean noSave;
    public static boolean isFPS = false;
    public static final int FULL_SCREEN = -100;
    public static boolean fullScreenMode;
    public static boolean isReadyToPlay;
    public static DialogManager dialog;

    public static String lastLOCID;
    public static String lastMID;
    public static boolean paused;
    public static String pauseLOCID;
    public static String pauseMID;
    public static boolean enableIT = true;

    public static Bitmap loadingBitmap;
    public static boolean showLoadingBitmap;
    public static ArrayList<String> iconNames = new ArrayList<>();

    {
        //  VPlayer.load(saver);
    }

    private static ArrayList<PostInitEvent> postInitEvents = new ArrayList<>();
    private static ArrayList<InitEvent> initEvents = new ArrayList<>();
    private static ArrayList<PreInitEvent> preInitEvents = new ArrayList<>();
    public static GameCore core;

    public static void registerInitEventConsumer(InitEvent initEvent) {
        initEvents.add(initEvent);
    }

    public static void registerPreInitEventConsumer(PreInitEvent preInitEvent) {
        preInitEvents.add(preInitEvent);
    }

    public static void registerPostInitEventConsumer(PostInitEvent postInitEvent) {
        postInitEvents.add(postInitEvent);
    }

    public static ArrayList<InitEvent> getInitEvents() {
        return initEvents;
    }

    public static ArrayList<PostInitEvent> getPostInitEvents() {
        return postInitEvents;
    }

    public static ArrayList<PreInitEvent> getPreInitEvents() {
        return preInitEvents;
    }

    private Game() {
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public static void setScreenSize(int i) {
        if (i == FULL_SCREEN) {
            settings.setAttribute(settings.FULLSCREEN,true);
            fullScreenMode = true;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            WIDTH = (int) screenSize.getWidth();
            HEIGHT = (int) screenSize.getHeight();
        } else {
            WIDTH = i;
            HEIGHT = WIDTH / 16 * 9;
        }
    }

    public static CustomSettings getSettings() {
        return settings;
    }

    public static World getWorld() {
        return world;
    }

    public static void setWorld(World world) {
        Game.world = world;
    }

    public static void saveGame() {
        world.saveWorld();
        saver.saveWorld(world.getNBT());
        saveSettings();
    }

    public static void saveSettings() {
        NBT nbt = new NBT();
        settings.writeToNBT(nbt);
        saver.saveSettingsNBT(nbt);
    }

    public static void setLanguage(String name) {
        settings.setAttribute(settings.LANG,name);
        Translator.loadLanguage(Game.saver,name);
        VPlayer.setLanguage(name, Game.saver);
        if (getWorld() != null) {
            getWorld().items().refreshLanguage();
        }
    }

    public static void setLoadingBitmap(Bitmap loadingBitmap) {
        if (Game.loadingBitmap != null)
            Game.renderer.removeTop(Game.loadingBitmap);

        Game.loadingBitmap = loadingBitmap;
        if (loadingBitmap != null) {
            Game.renderer.registerTop(loadingBitmap);
            loadingBitmap.setShouldRender(showLoadingBitmap);
        }
    }

    public static void setLoadingScreen(boolean b) {
        showLoadingBitmap = b;
        if (loadingBitmap == null)
            return;
        Game.loadingBitmap.setShouldRender(b);

        Game.input.muteInput(b);

        if (b)
            Game.core.EVENT_BUS.pauseEvents();
    }
}
