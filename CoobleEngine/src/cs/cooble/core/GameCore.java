package cs.cooble.core;

import cs.cooble.event.*;
import cs.cooble.event.Event;
import cs.cooble.graphics.Renderable;
import cs.cooble.logger.Log;
import cs.cooble.music.MPlayer2;
import cs.cooble.window.Tickable;
import org.newdawn.slick.*;
import org.newdawn.slick.Graphics;

import java.awt.Font;


/**
 * This takes care of the game itself,
 * Handles things like  WIDTH,HEIGHT,FULLSCREEN, main window
 * TARGET_TicksPerSecond
 * USER INPUT
 * IT Manages things like render/tick/user_input
 */
public class GameCore extends BasicGame {

    public static GameCore create() throws SlickException {
        GameCore gameCore = new GameCore();
        AppGameContainer container = new AppGameContainer(gameCore);
        container.setAlwaysRender(true);
        container.setDisplayMode(gameCore.WIDTH, gameCore.HEIGHT, Game.getSettings().fullScreen);
        int tps = 1000 / gameCore.TARGET_TPS;
        container.setMinimumLogicUpdateInterval(tps);
        container.setMaximumLogicUpdateInterval(tps);
       for(String name:Game.iconNames)
            container.setIcon(name);
        // container.setTargetFrameRate(60);
        container.setVerbose(false);
        container.setShowFPS(Game.isFPS);
        // container.setVSync(true);
        gameCore.container = container;
        return gameCore;
    }

    public int TARGET_TPS;
    public final int WIDTH, HEIGHT;
    private boolean FULLSCREEN;


    private boolean initialized;
    public CEventBus EVENT_BUS;
    private Event gameLoadEvent;

    private Tickable tickable;
    private Renderable renderable;
    private AppGameContainer container;

    private GameCore(int WIDTH, int HEIGHT, String title) {
        super(title);
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        TARGET_TPS = 60;
        Game.core = this;
        EVENT_BUS = new CEventBus();
    }

    private GameCore() {
        this(Game.getWIDTH(), Game.getHEIGHT(), Game.gameName);
    }

    public void setTickable(Tickable tickable) {
        this.tickable = tickable;
    }

    public void setRenderable(Renderable renderable) {
        this.renderable = renderable;
    }

    public void start(Event event) {
        gameLoadEvent = event;
        try {
            container.start();
        } catch (SlickException ignored) {//callbackUtil.java is not found in final jar (who knows why?)

        }

    }

    private void render(Graphics g) {
        try {
            Game.renderer.render(g);
        } catch (SlickException e) {
            e.printStackTrace();
        }
        renderable.render(g);
    }

    private void tick() {
        MPlayer2.tick();
        Game.getWorld().inventory().tick();
        Game.getWorld().getLocationManager().tick();
        Game.getWorld().getUniCreature().tick();
        Game.input.tick();
        Game.renderer.getCameraMan().tick();
        Game.dialog.tick();
        tickable.tick();
    }

    public void stop() {
        container.destroy();
    }

    /**
     * preprepreInit
     *
     * @param gameContainer
     * @throws SlickException
     */
    @Override
    public void init(GameContainer gameContainer) throws SlickException {
        try {
            Font big = Font.createFont(Font.PLAIN, Game.saver.getResourceAsStream(Game.saver.FONT_PATH + "MinecraftCzechReloaded.ttf"));
            Game.font = new TrueTypeFont(big.deriveFont(16f), false);
            Game.smallFont = new TrueTypeFont(big.deriveFont(8f), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //inputs
        container.getInput().addKeyListener(new MyKeyListener());
        MyKeyListener keyListener = new MyKeyListener();
        MyMouseListener mouseListener = new MyMouseListener();

        container.getInput().addKeyListener(keyListener);
        container.getInput().addMouseListener(mouseListener);
        Game.input = new CUserInput(keyListener, mouseListener);
        initialized = true;
        Log.println("INIT in GameCore done");

        Game.core.EVENT_BUS.addEvent(gameLoadEvent);
    }

    private char[] toArray(String s) {
        char[] out = new char[s.length()];
        for (int i = 0; i < s.length(); i++) {
            out[i] = s.charAt(i);
        }
        return out;
    }

    @Override
    public void update(GameContainer gameContainer, int delta) throws SlickException {
        EVENT_BUS.proccessEvents();
        if (Game.isReadyToPlay)
            tick();
    }

    @Override
    public void render(GameContainer gameContainer, Graphics graphics) throws SlickException {
        if (Game.isReadyToPlay)
            render(graphics);
    }

    @Override
    public boolean closeRequested() {
        Game.core.EVENT_BUS.addEvent(new GameExitEvent());
        return false;
    }


    public boolean isInitialized() {
        return initialized;
    }
}
