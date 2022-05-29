package mc.cooble.chgame;

import mc.cooble.core.CoobleLauncher;
import mc.cooble.core.Game;
import mc.cooble.core.GameCore;
import mc.cooble.entity.Joe;
import mc.cooble.event.*;
import mc.cooble.graphics.Bitmap;
import mc.cooble.graphics.Renderable;
import mc.cooble.item.Items;
import mc.cooble.listener.MyUserInput;
import mc.cooble.logger.Log;
import mc.cooble.module.Modules;
import mc.cooble.music.MPlayer2;
import mc.cooble.saving.Saver;
import mc.cooble.window.Tickable;
import org.newdawn.slick.Graphics;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Matej on 1.10.2016.
 */
public class LauncherV2 implements Tickable, Renderable, PreInitEvent, InitEvent, PostInitEvent {

    public static void main(String[] args) throws InterruptedException, URISyntaxException, IOException {
        Game.gameName = "ChristmasGame";
        CoobleLauncher.main(args);
        main2(args);

    }


    private static void main2(String[] args) {
        LauncherV2 launcherV2 = new LauncherV2();

        GameCore gameCore = null;

        Game.saver = new Saver(Game.gameName,LauncherV2.class);
        Game.iconNames.add(Game.saver.getIO().makeResString(Game.saver.TEXTURE_PATH + "gui/icon16.png"));
        Game.iconNames.add(Game.saver.getIO().makeResString(Game.saver.TEXTURE_PATH + "gui/icon24.png"));
        Game.iconNames.add(Game.saver.getIO().makeResString(Game.saver.TEXTURE_PATH + "gui/icon32.png"));
        try {
            Game.core = gameCore = GameCore.create();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        gameCore.setRenderable(launcherV2);
        gameCore.setTickable(launcherV2);
        Event gameLoadEvent = new GameLoadEvent();
        Game.registerPreInitEventConsumer(launcherV2);
        Game.registerInitEventConsumer(launcherV2);
        Game.registerPostInitEventConsumer(launcherV2);
        Game.core.EVENT_BUS.addEvent(() -> Game.input = new MyUserInput(Game.input.getKeyListener(), Game.input.getMouseListener()));
        Game.core.start(gameLoadEvent);
    }


    @Override
    public void render(Graphics graphics) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void preInit() {
        Log.println("Pre-INIT in launcher");
        Thread t = new Thread(this::loadBigSounds);
        t.start();

    }

    @Override
    public void init() {
        Items.load(Game.getWorld().items());
        Modules.load(Game.getWorld().modules());
        Log.println("INIT in launcher");
        Game.setLoadingBitmap(Bitmap.get("gui/loading"));
        Game.setLoadingScreen(true);

    }

    @Override
    public void postInit() {
        Game.getWorld().setUniCreature(new Joe());
        Game.getWorld().getUniCreature().loadTextures();
        Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("intro", "intro"));
        Game.core.EVENT_BUS.addDelayedEvent(1, () -> Game.setLoadingScreen(false));
        Log.println("Post-INIT in launcher");


    }

    private void loadBigSounds() {
        MPlayer2.loadSong("carol_of_bells");
        MPlayer2.loadSong("christmas_song");
        MPlayer2.loadSound("arctic_wind");
        MPlayer2.loadSong("jingle_bells_cs");
        MPlayer2.loadSong("jingle_bells_en");
        MPlayer2.loadSong("party");
        MPlayer2.loadSound("panic");
        MPlayer2.loadSound("cellar");
        MPlayer2.loadSound("helicopter");
        MPlayer2.loadSound("shower");
        Log.println("Big audio loaded!");
    }
}
