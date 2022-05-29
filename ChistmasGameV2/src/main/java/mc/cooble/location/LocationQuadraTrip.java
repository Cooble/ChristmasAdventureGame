package mc.cooble.location;

import mc.cooble.core.Game;
import mc.cooble.entity.Cleaner;
import mc.cooble.entity.Helper;
import mc.cooble.entity.Joe;
import mc.cooble.entity.Talkable;
import mc.cooble.event.DialogEvent;
import mc.cooble.event.GameExitEvent;
import mc.cooble.event.MyKeyListener;
import mc.cooble.event.SpeakEvent;
import mc.cooble.graphics.Animation;
import mc.cooble.graphics.Bitmap;
import mc.cooble.graphics.BitmapStack;
import mc.cooble.graphics.Renderer;
import mc.cooble.inventory.stuff.Stuff;
import mc.cooble.inventory.stuff.StuffToCome;
import mc.cooble.music.MPlayer2;
import mc.cooble.resources.StringStream;
import mc.cooble.translate.Translator;
import mc.cooble.world.Location;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Matej on 28.7.2017.
 */
public class LocationQuadraTrip extends Location {
    Bitmap wallpaper0, wallpaper1;
    private int WIDTH;
    private int offsetX, offsetY;
    private int posX, posY;
    private int ticksToNextBall = 10;
    ArrayList<Snowball> snowballs = new ArrayList<>();
    BitmapStack snowballTextures;
    StuffToCome quadra;
    private int snowballCadence;
    private boolean fallingJoe;

    private int wallpaperSpeed = 4;
    private int joeSpeed = 2;

    private int pixelDrift;

    private BitmapStack quadracopter, joe;

    private StringStream fallingStream, retryStream;

    private boolean minigameStarted;
    private boolean startFalling;

    private boolean canUseArrows;
    private boolean end;
    private boolean goEnd;

    private Cleaner cleaner;
    private Helper helper;
    private Stuff arrowGuide;

    private boolean mutedInput;

    public LocationQuadraTrip() {
        super("quadratrip");
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        MPlayer2.playSound("arctic_wind", 0.6);
        ((Joe) Game.getWorld().getUniCreature()).setBitmapStack(Game.getWorld().getUniCreature().getBitmapStack().resize(0.5));
        Game.getWorld().getUniCreature().setPos(125, 169);
        super.onStartRendering(renderer);
        wallpaper0.setShouldRender(false);
        wallpaper1.setShouldRender(false);
        renderer.registerBitmapProvider(cleaner.getBitmapStack());
        renderer.registerBitmapProvider(helper.getBitmapStack());


        Game.input.muteInput(true);
        mutedInput = true;
        SpeakEvent event = new SpeakEvent("entity.helper.bye");
        event.setColor(Color.darkGray);
        event.setTalkable(helper);
        Game.core.EVENT_BUS.addDelayedEvent(100, event);
        event.setOnEnd(new Runnable() {
            @Override
            public void run() {
                SpeakEvent event = new SpeakEvent("entity.cleaner.bye");
                event.setColor(Color.orange);
                event.setTalkable(cleaner);
                Game.core.EVENT_BUS.addEvent(event);
                event.setOnEnd(() ->
                        Game.input.muteInput(false));
                mutedInput = false;
            }
        });
        arrowGuide.setVisible(false);
    }

    @Override
    public void onStopRendering(Renderer renderer) {
        super.onStopRendering(renderer);
        Game.getWorld().getUniCreature().loadTextures();
    }

    @Override
    public void loadTextures() {
        fallingStream = StringStream.getStream("entity.joe.falling.");
        retryStream = StringStream.getStream("entity.joe.retryFly.");

        wallpaper0 = getStuffByID("wallpaper0").getBitmapProvider().getCurrentBitmap();
        wallpaper1 = getStuffByID("wallpaper1").getBitmapProvider().getCurrentBitmap();
        wallpaper0.setShouldRender(false);
        wallpaper1.setShouldRender(false);
        WIDTH = wallpaper0.getWidth();


        arrowGuide = getStuffByID("arrow_guide");
        quadra = (StuffToCome) getStuffByID("quadracopter");
        ((Animation) quadra.getBitmapProvider()).setAnimationEnabled(false);
        quadracopter = ((Animation) quadra.getBitmapProvider()).getBitmapStack();

        quadra.setOnPickedUp(new Runnable() {
            @Override
            public void run() {
                getStuffByID("joe").setVisible(true);
                Game.renderer.removeBitmapProvider(Game.getWorld().getUniCreature().getBitmapStack());
                startFalling = true;
                SpeakEvent event = new SpeakEvent("entity.joe.snowing");
                Game.core.EVENT_BUS.addEvent(event);
                event.setOnEnd(new Runnable() {
                    @Override
                    public void run() {
                        arrowGuide.setVisible(true);
                        MPlayer2.playSound("helicopter", 0.3);
                        ((Animation) quadra.getBitmapProvider()).setAnimationEnabled(true);
                        canUseArrows = true;
                    }
                });

            }
        });

        joe = ((BitmapStack) getStuffByID("joe").getBitmapProvider());
        getStuffByID("joe").setVisible(false);

        rectangleOffsetX = quadra.getActionListener().getRectangle().x - quadracopter.getOffset()[0];
        rectangleOffsetY = quadra.getActionListener().getRectangle().y - quadracopter.getOffset()[1];

        offsetX = joe.getOffset()[0] - quadracopter.getOffset()[0];
        offsetY = joe.getOffset()[1] - quadracopter.getOffset()[1];

        snowballTextures = BitmapStack.getBitmapStackFromFolder("item/snowball");

        posX = quadracopter.getOffset()[0];
        posY = quadracopter.getOffset()[1];

        snowballCadence = Game.core.TARGET_TPS * 4;


        cleaner = new Cleaner("cleaner");
        cleaner.loadTextures();
        cleaner.setBitmapStack(cleaner.getBitmapStack().resize(0.5));
        cleaner.setRight(true);
        cleaner.setPos(30, 162);
        cleaner.setRectangle(30, 162, cleaner.getBitmapStack().getWidth(), cleaner.getBitmapStack().getHeight());
        registerTickable(cleaner);

        helper = new Helper("helper0");
        helper.loadTextures();
        helper.setBitmapStack(helper.getBitmapStack().resize(0.5));
        helper.setRight(true);
        helper.setPos(60, 156);
        helper.setRectangle(60, 156, helper.getBitmapStack().getWidth(), helper.getBitmapStack().getHeight());
        registerTickable(helper);
    }

    private void startMiniGame() {
        posX = 0;
        posY = 40;
        Game.renderer.removeBitmapProvider(cleaner.getBitmapStack());
        Game.renderer.removeBitmapProvider(helper.getBitmapStack());
        wallpaper0.setShouldRender(true);
        wallpaper1.setShouldRender(true);
        minigameStarted = true;
        Renderer renderer = Game.renderer;
        wallpaper0.setShouldRender(true);
        wallpaper1.setShouldRender(true);
        renderer.removeBitmapProvider(getStuffByID("quadracopter").getBitmapProvider());
        renderer.removeBitmapProvider(getStuffByID("joe").getBitmapProvider());
        renderer.registerBitmapProvider(getStuffByID("quadracopter").getBitmapProvider());
        renderer.registerBitmapProvider(getStuffByID("joe").getBitmapProvider());
        MPlayer2.playSoundIfNotExist("helicopter", 0.65);
        Game.dialog.getDialogPainter().setMaxLines(1);
        MPlayer2.playSongIfNot("jingle_bells_" + Translator.getLanguage(), 2, 60);
        ticksToNextBall = 10;
        snowballsSpawned = 0;
        snowballCadence = Game.core.TARGET_TPS * 4;
    }

    int rectangleOffsetX, rectangleOffsetY;

    private Rectangle getQuadraRect() {
        return new Rectangle(rectangleOffsetX + quadracopter.getOffset()[0], rectangleOffsetY + quadracopter.getOffset()[1], quadra.getActionListener().getRectangle().width, quadra.getActionListener().getRectangle().height);
    }


    @Override
    public void tick() {
        super.tick();
        if (minigameStarted) {
            if (!endMuteSounds) {
                MPlayer2.playSoundIfNotExist("helicopter", 0.65);
                MPlayer2.playSongIfNot("jingle_bells_" + Translator.getLanguage(), 1.5, 80);
            }
            pixelDrift -= wallpaperSpeed;
            if (pixelDrift < 0) {
                pixelDrift = WIDTH;
            }
            refreshWallpaper();
        }
        MyKeyListener listener = Game.input.getKeyListener();
        if (!fallingJoe && canUseArrows && !goEnd) {
            if (listener.getTicksOn(Input.KEY_UP) > 0) {
                arrowGuide.setVisible(false);
                if (posY > 0)
                    posY -= joeSpeed;
            } else if (listener.getTicksOn(Input.KEY_DOWN) > 0) {
                if (posY < wallpaper0.getHeight() - quadracopter.getOffset()[1] + quadracopter.getHeight() / 2) {
                    posY += 2 * joeSpeed;
                } else {
                    die();
                }
            }
            if (listener.getTicksOn(Input.KEY_LEFT) > 0) {
                if (posX > 0)
                    posX -= 2 * joeSpeed;
            } else if (listener.getTicksOn(Input.KEY_RIGHT) > 0) {
                if (!minigameStarted) {
                    posX += joeSpeed;
                    if (posX > Game.renderer.PIXEL_WIDTH && posY < Game.renderer.PIXEL_HEIGHT / 2) {
                        startMiniGame();
                    }
                } else if (posX < 0.65 * Game.renderer.PIXEL_WIDTH) {
                    posX += joeSpeed / 2;
                }
            }
        } else if (canUseArrows && !goEnd) {
            posX -= 2 * joeSpeed;
            posY += 2 * joeSpeed;
            if (posX < -400 || posY < -400) {
                fallingJoe = false;
                ((Animation) quadra.getBitmapProvider()).setAnimationEnabled(true);
                SpeakEvent event = new SpeakEvent(retryStream.getNextString());
                event.setTalkable(new Talkable() {
                    @Override
                    public void setIsTalking(boolean isTalking) {
                        joe.setCurrentIndex(isTalking ? 1 : 0);
                    }
                });
                Game.core.EVENT_BUS.addEvent(event);
                posX = 0;
                posY = 40;
            }
        }
        if (goEnd) {
            posX += joeSpeed;
            if (posX > Game.renderer.PIXEL_WIDTH) {
                GameExitEvent event = new GameExitEvent();
                event.setClear(true);
                Game.core.EVENT_BUS.addEvent(event);
            }
        }
        refreshPosition();
        snowballTick();
    }

    private int snowballsSpawned;
    private boolean endMuteSounds;

    private void snowballTick() {
        Rectangle r = getQuadraRect();
        ArrayList<Snowball> toRemove = new ArrayList<>();
        for (Snowball s : snowballs) {
            if (s != null) {
                s.tick();
                if (s.isColliding(r)) {
                    die();
                    toRemove.add(s);
                }
                if (s.getY() > Game.renderer.PIXEL_HEIGHT + s.src.getHeight()) {
                    toRemove.add(s);
                }
            }
        }
        for (Snowball s : toRemove) {
            snowballs.remove(s);
            Game.renderer.removeBitmapProvider(s.src);
        }
        if (ticksToNextBall != 0 && startFalling) {
            ticksToNextBall--;
            if (ticksToNextBall < 1) {
                if (minigameStarted) {
                    snowballsSpawned++;
                    spawnSnowball();
                }
                ticksToNextBall = snowballCadence + Game.random.nextInt(60) + 1;
            }
            if (Game.random.nextInt(5) == 0) {
                spawnNotDangerousSnowball();
            }
        }
        switch (snowballsSpawned) {
            case 2:
                Game.dialog.getDialogPainter().setMaxLines(2);
                Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.snowBalls"));
                break;
            case 5:
                snowballCadence = 2 * Game.core.TARGET_TPS;
                break;
            case 10:
                snowballCadence = Game.core.TARGET_TPS;
                break;
            case 15:
                snowballCadence = Game.core.TARGET_TPS / 2;
                Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.snowharder"));
                break;
            case 23:
                endMuteSounds = true;
                snowballsSpawned = 0;
                startFalling = false;
                Game.dialog.getDialogPainter().setMaxLines(10);
                MPlayer2.setSongVolume(0.1, 60);
                SpeakEvent event = new SpeakEvent("entity.joe.snowend");
                Game.core.EVENT_BUS.addEvent(event);
                event.setOnEnd(new Runnable() {
                    @Override
                    public void run() {
                        DialogEvent event = new DialogEvent("semiEnd");
                        event.registerTalkable(new Talkable() {
                            @Override
                            public void setIsTalking(boolean isTalking) {
                                joe.setCurrentIndex(isTalking ? 1 : 0);
                            }
                        }, "joe", org.newdawn.slick.Color.green);
                        event.registerTalkable(isTalking -> {
                        }, "player", org.newdawn.slick.Color.lightGray);
                        Game.core.EVENT_BUS.addEvent(event);
                        event.setOnEnd(() -> goEnd = true);
                    }
                });
                end = true;
                break;
        }

    }

    private void die() {
        if (!fallingJoe && minigameStarted && !end) {
            snowballsSpawned = 0;
            snowballCadence = Game.core.TARGET_TPS * 3;
            MPlayer2.playSoundIfNotExist("crash");
            ((Animation) quadra.getBitmapProvider()).setAnimationEnabled(false);
            SpeakEvent event = new SpeakEvent(fallingStream.getNextString());
            event.setTalkable(new Talkable() {
                @Override
                public void setIsTalking(boolean isTalking) {
                    joe.setCurrentIndex(isTalking ? 1 : 0);
                }
            });
            Game.core.EVENT_BUS.addEvent(event);
            fallingJoe = true;
        }

    }


    private void spawnSnowball() {
        Random random = Game.random;
        Snowball snowball = new Snowball(snowballTextures.getBitmap(random.nextInt(snowballTextures.getMaxLength())).copy(), -random.nextDouble() - 0.65, random.nextDouble() + 0.5);
        snowball.setPos(random.nextInt(Game.renderer.PIXEL_WIDTH / 2) + Game.renderer.PIXEL_WIDTH / 2, -snowball.src.getHeight());
        snowball.tick();
        snowballs.add(snowball);
        Game.renderer.registerBitmapProvider(snowball.src);
    }

    private void spawnNotDangerousSnowball() {
        Random random = Game.random;
        Snowball snowball = new Snowball(snowballTextures.getBitmap(random.nextInt(snowballTextures.getMaxLength())).copy(), minigameStarted ? (-random.nextDouble() - 0.65) : (random.nextDouble() - 0.5) / 3, random.nextDouble() + 0.5);
        snowball.setNotDangerous();
        snowball.setPos(snowball.src.getWidth() + random.nextInt((int) (Game.renderer.PIXEL_WIDTH * 1.6)), -snowball.src.getHeight());
        snowball.tick();
        snowballs.add(snowball);
        Game.renderer.registerBitmapProvider(snowball.src);
    }

    private void refreshPosition() {
        quadracopter.setOffset(posX, posY);
        joe.setOffset(posX + offsetX, posY + offsetY);
    }

    private void refreshWallpaper() {
        wallpaper0.setOffset(pixelDrift - WIDTH, 0);
        wallpaper1.setOffset(pixelDrift, 0);
    }


    private class Snowball {
        private Bitmap src;
        private final double speedX;
        private final double speedY;
        private double posX, posY;
        private boolean dangerous;

        Snowball(Bitmap src, double speedX, double speedY) {
            this.src = src;
            this.speedX = speedX;
            this.speedY = speedY;
            dangerous = true;
        }

        void setNotDangerous() {
            dangerous = false;
            src = src.resize(0.3);
        }

        void setPos(int x, int y) {
            posX = x;
            posY = y;
        }

        void setSrcPosition(int x, int y) {
            src.setOffset(x - src.getWidth() / 2, y - src.getHeight() / 2);
        }

        int getX() {
            return src.getOffset()[0] + src.getWidth();
        }

        int getY() {
            return src.getOffset()[1] + src.getHeight();
        }

        boolean isColliding(Rectangle rectangle) {
            return dangerous && rectangle.contains(getX(), getY());
        }

        void tick() {
            posX += speedX;
            posY += speedY;
            setSrcPosition((int) posX, (int) posY);
        }
    }

    @Override
    public boolean consume(int x, int y, int mouseEvent, boolean released) {
        if (mutedInput)
            return true;
        return super.consume(x, y, mouseEvent, released);
    }
}
