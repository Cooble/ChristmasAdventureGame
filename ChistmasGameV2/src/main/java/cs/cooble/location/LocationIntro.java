package cs.cooble.location;

import cs.cooble.core.Game;
import cs.cooble.entity.Button;
import cs.cooble.entity.ButtonNormal;
import cs.cooble.entity.ButtonShifting;
import cs.cooble.event.Event;
import cs.cooble.event.*;
import cs.cooble.graphics.Bitmap;
import cs.cooble.graphics.BitmapProvider;
import cs.cooble.graphics.Renderer;
import cs.cooble.inventory.item.ItemStack;
import cs.cooble.inventory.stuff.Stuff;
import cs.cooble.music.MPlayer2;
import cs.cooble.translate.Translator;
import cs.cooble.world.IAction;
import cs.cooble.world.Location;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Matej on 18.7.2016.
 */
public class LocationIntro extends Location {
    Button buttonPlay;
    Button buttonExit;
    Button buttonClear;
    Button buttonSettings;

    ArrayList<Button> mainButtons = new ArrayList<>();
    ArrayList<Button> settingsButtons = new ArrayList<>();


    Button buttonLang;
    Button buttonFullscreen;
    ButtonShifting sound;
    ButtonShifting music;
    ButtonShifting voice;

    Stuff questionMark;
    Stuff exclamationMark;
    Stuff trash;
    Stuff link;

    Bitmap book;
    Bitmap infoCs, infoEn;
    Bitmap controlsCs, controlsEn;

    Bitmap itnetwork;
    final int itTime;

    private static boolean freshGame = true;
    private boolean gameReady;

    int tickDark;

    public LocationIntro() {
        super("intro");
        itTime = Game.core.TARGET_TPS * 7;
    }


    @Override
    public void loadTextures() {
        itnetwork = Bitmap.get("item/itnetwork");
        foreground = new BitmapProvider() {
            @Override
            public Bitmap getCurrentBitmap() {
                return itnetwork;
            }

            @Override
            public int[] getOffset() {
                return new int[]{0, 0};
            }

            @Override
            public BitmapProvider[] getBitmaps() {
                return new BitmapProvider[]{itnetwork};
            }
        };
        setForeground(foreground);

        book = Bitmap.get("location/book").resize(2);
        book.setShouldRender(false);

        infoCs = Bitmap.get("item/info_cs");
        infoEn = Bitmap.get("item/info_en");
        infoCs.setShouldRender(false);
        infoEn.setShouldRender(false);

        controlsCs = Bitmap.get("item/controls_cs");
        controlsEn = Bitmap.get("item/controls_en");
        controlsCs.setShouldRender(false);
        controlsEn.setShouldRender(false);

        int yoffset = -10;
        setJoeProhibited(true);
        buttonPlay = new ButtonNormal(6 * 2, 45 * 2 + yoffset, () -> {
            if (Game.paused) {
                Game.paused = false;
                Game.core.EVENT_BUS.addEvent(new LocationLoadEvent(Game.pauseLOCID, Game.pauseMID));
            } else
                Game.core.EVENT_BUS.addEvent(new WorldLoadEvent());
        });
        buttonPlay.setText(Translator.translate("button.play.name"));
        this.actionRectangleManager.register(buttonPlay.getActionListener());


        sound = new ButtonShifting(165, 20, 55 * 2, 13 * 2, Game.font, new Runnable() {
            @Override
            public void run() {
                Game.getSettings().soundVolume = sound.getValue();
            }
        }, "Sound");
        this.actionRectangleManager.register(sound.getActionListener());
        sound.setEnabled(false);

        music = new ButtonShifting(165, 20 * 2, 55 * 2, 13 * 2, Game.font, new Runnable() {
            @Override
            public void run() {
                Game.getSettings().songVolume = music.getValue();
            }
        }, "Music");
        this.actionRectangleManager.register(music.getActionListener());
        music.setEnabled(false);

        voice = new ButtonShifting(165, 20 * 3, 55 * 2, 13 * 2, Game.font, new Runnable() {
            @Override
            public void run() {
                Game.getSettings().voiceVolume = voice.getValue();
            }
        }, "Voice");
        this.actionRectangleManager.register(voice.getActionListener());
        voice.setEnabled(false);


        buttonLang = new ButtonNormal(170, 60 * 2, new Runnable() {
            @Override
            public void run() {
                Game.setLoadingScreen(true);
                MPlayer2.playSound("cvak_0");

                Game.core.EVENT_BUS.addDelayedEvent(10, new Event() {
                    @Override
                    public void dispatchEvent() {
                        String lang = Translator.LANGUAGE_EN;
                        if (Translator.getLanguage().equals(Translator.LANGUAGE_EN))
                            lang = Translator.LANGUAGE_CS;
                        String lastMID = Game.getWorld().getModule().MID;
                        Game.saveGame();
                        Game.setLanguage(lang);

                        Game.getWorld().setModule(Game.getWorld().modules().getModule(lastMID));//reload module
                        Game.core.EVENT_BUS.addDelayedEvent(1, new LocationLoadEvent("intro"));
                        buttonLang.setText("Lang: " + Translator.getLanguage());
                        Game.core.EVENT_BUS.addDelayedEvent(60, () -> Game.setLoadingScreen(false));
                    }
                });

            }
        });
        buttonLang.setText("Lang: " + Translator.getLanguage());
        buttonLang.setDefaultLore("lang");
        this.actionRectangleManager.register(buttonLang.getActionListener());
        buttonLang.setEnabled(false);


        buttonFullscreen = new ButtonNormal(170, 60 * 2 - 30, () -> {
            GameExitEvent restart = new GameExitEvent();
            restart.setRestart(true);
            Game.getSettings().fullScreen = !Game.getSettings().fullScreen;
            Game.core.EVENT_BUS.addEvent(restart);
        });
        buttonFullscreen.setDefaultLore("fullscreen");
        buttonFullscreen.setText((!Game.getSettings().fullScreen ? "F-Screen" : "N-Screen"));
        this.actionRectangleManager.register(buttonFullscreen.getActionListener());
        buttonFullscreen.setEnabled(false);

        buttonSettings = new ButtonNormal(6 * 2, 60 * 2 + yoffset, new Runnable() {
            @Override
            public void run() {
                sound.setValue(Game.getSettings().soundVolume);
                music.setValue(Game.getSettings().songVolume);
                voice.setValue(Game.getSettings().voiceVolume);
                if (!buttonLang.isEnabled()) {
                    disable(mainButtons);
                    enable(settingsButtons);
                    buttonLang.setEnabled(true);
                    questionMark.setVisible(false);
                    exclamationMark.setVisible(false);
                    trash.setVisible(false);
                    buttonSettings.setText(Translator.translate("back"));
                } else {
                    trash.setVisible(true);
                    questionMark.setVisible(true);
                    exclamationMark.setVisible(true);
                    disable(settingsButtons);
                    enable(mainButtons);
                    buttonLang.setEnabled(false);

                    // Game.saveGame();
                    // Game.getWorld().setModule(Modules.moduleIntro);
                    Game.core.EVENT_BUS.addEvent(new LocationLoadEvent("intro"));
                    buttonSettings.setText(Translator.translate("button.settings.name"));
                }
                book.setShouldRender(buttonLang.isEnabled());
            }
        });
        buttonSettings.setText(Translator.translate("button.settings.name"));
        this.actionRectangleManager.register(buttonSettings.getActionListener());

        buttonExit = new ButtonNormal(6 * 2, 75 * 2 + yoffset, () -> Game.core.EVENT_BUS.addEvent(new GameExitEvent()));
        buttonExit.setText(Translator.translate("button.exit.name"));
        this.actionRectangleManager.register(buttonExit.getActionListener());

        buttonClear = new ButtonNormal(6 * 2, 60 * 2 + yoffset, () -> {
            GameExitEvent event = new GameExitEvent();
            event.setClear(true);
            Game.core.EVENT_BUS.addEvent(event);
        });
        buttonClear.setText(Translator.translate("button.clear.name"));
        buttonClear.setDefaultLore("clear");
        buttonClear.setEnabled(false);
        this.actionRectangleManager.register(buttonClear.getActionListener());

        mainButtons.add(buttonPlay);
        mainButtons.add(buttonSettings);
        mainButtons.add(buttonExit);
       // mainButtons.add(buttonClear);

        settingsButtons.add(buttonSettings);
        settingsButtons.add(buttonLang);
        settingsButtons.add(music);
        settingsButtons.add(voice);
        settingsButtons.add(sound);
        settingsButtons.add(buttonFullscreen);

        questionMark = getStuffByID("question_mark");
        questionMark.setVisible(true);
        questionMark.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (released || right_button)
                    return true;
                MPlayer2.playSound("paper");
                if (!infoMode) {
                    exclamationMark.setVisible(false);
                    trash.setVisible(false);
                    actionRectangleManager.register(link.getActionListener());
                    infoMode = true;
                    Game.renderer.removeBitmapProvider(questionMark.getBitmapProvider());
                    Game.renderer.registerBitmapProvider(questionMark.getBitmapProvider());
                    disable(mainButtons);
                    if (Translator.getLanguage().equals(Translator.LANGUAGE_EN))
                        infoEn.setShouldRender(true);
                    else infoCs.setShouldRender(true);
                } else {
                    exclamationMark.setVisible(true);
                    trash.setVisible(true);
                    actionRectangleManager.remove(link.getActionListener());
                    infoMode = false;
                    enable(mainButtons);
                    infoCs.setShouldRender(false);
                    infoEn.setShouldRender(false);
                }
                return true;
            }
        });
        link = getStuffByID("link");
        link.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (right_button || released)
                    return true;
                try {
                    openWebpage(new URL("http://www.itnetwork.cz/nezarazene/letni-programatorska-soutez-2017"));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        actionRectangleManager.remove(link.getActionListener());

        exclamationMark = getStuffByID("exclamation_mark");
        exclamationMark.setVisible(true);
        exclamationMark.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!IAction.onlyPressed(right_button, released, item))
                    return true;
                MPlayer2.playSound("paper");
                if (!controlsMode) {
                    questionMark.setVisible(false);
                    trash.setVisible(false);
                    controlsMode = true;
                    Game.renderer.removeBitmapProvider(exclamationMark.getBitmapProvider());
                    Game.renderer.registerBitmapProvider(exclamationMark.getBitmapProvider());
                    disable(mainButtons);
                    if (Translator.getLanguage().equals(Translator.LANGUAGE_EN))
                        controlsEn.setShouldRender(true);
                    else controlsCs.setShouldRender(true);
                } else {
                    questionMark.setVisible(true);
                    trash.setVisible(true);
                    controlsMode = false;
                    enable(mainButtons);
                    controlsCs.setShouldRender(false);
                    controlsEn.setShouldRender(false);
                }
                return true;
            }
        });
        trash = getStuffByID("trash");
        trash.setVisible(true);
        trash.setAction(new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                btns(!released);
                return true;
            }

            @Override
            public void onFocusLost() {
                btns(false);
            }
            private void btns(boolean clearMode){
                buttonClear.setEnabled(clearMode);
                if(clearMode)
                    disable(mainButtons);
                else enable(mainButtons);
                exclamationMark.setVisible(!clearMode);
                questionMark.setVisible(!clearMode);
            }
        });
    }

    boolean infoMode;
    boolean controlsMode;

    @Override
    public void onStart() {
        super.onStart();
        Game.input.muteInput(false);
        infoMode = false;
        controlsMode = false;
        if (!Game.enableIT)
            freshGame = false;
        if (!Game.paused)
            MPlayer2.playSongIfNot("carol_of_bells", 0.15, 0);

        Game.core.EVENT_BUS.addEvent(() -> gameReady = true);
        exclamationMark.setVisible(true);
        questionMark.setVisible(true);
        trash.setVisible(true);
        buttonClear.setEnabled(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        Game.setLoadingBitmap(Bitmap.get("gui/loading"));
    }

    @Override
    public void onStartRendering(Renderer renderer) {
        super.onStartRendering(renderer);


        if (!freshGame) {
            Game.renderer.setForeground(null);
            setForeground((BitmapProvider) null);
            startRender2(renderer);
        } else {
            Game.setLoadingBitmap(itnetwork);
            Game.setLoadingScreen(true);
        }
    }

    private void startRender2(Renderer renderer) {
        renderer.registerMultiBitmapProvider(book);
        renderer.registerBitmapProvider(infoCs);
        renderer.registerBitmapProvider(infoEn);
        renderer.registerBitmapProvider(controlsCs);
        renderer.registerBitmapProvider(controlsEn);

        renderer.registerGUIProvider(buttonPlay);
        renderer.registerGUIProvider(buttonExit);
        renderer.registerGUIProvider(buttonClear);
        renderer.registerGUIProvider(buttonLang);
        renderer.registerGUIProvider(buttonSettings);

        renderer.registerGUIProvider(sound);
        renderer.registerGUIProvider(music);
        renderer.registerGUIProvider(voice);
        renderer.registerGUIProvider(buttonFullscreen);
        sound.setValue(Game.getSettings().soundVolume);
        music.setValue(Game.getSettings().songVolume);
        voice.setValue(Game.getSettings().voiceVolume);
    }

    @Override
    public void onStopRendering(Renderer renderer) {
        super.onStopRendering(renderer);
        renderer.removeMultiProvider(book);
        renderer.removeGuiProvider(buttonPlay);
        renderer.removeGuiProvider(buttonExit);
        renderer.removeGuiProvider(buttonClear);
        renderer.removeGuiProvider(buttonSettings);
        renderer.removeGuiProvider(buttonLang);
        renderer.removeGuiProvider(buttonFullscreen);

        renderer.removeGuiProvider(sound);
        renderer.removeGuiProvider(music);
        renderer.removeGuiProvider(voice);
    }

    @Override
    public void tick() {
        super.tick();
        if (freshGame && gameReady) {
            if (tickDark < itTime) {
                tickDark++;
                if (tickDark >= itTime) {
                    setForeground((BitmapProvider) null);
                    Game.renderer.setForeground(null);
                    freshGame = false;
                    startRender2(Game.renderer);
                    Game.setLoadingBitmap(Bitmap.get("gui/loading"));
                }
            }
        }
    }

    @Override
    public boolean consume(int x, int y, int mouseEvent, boolean released) {
        if (freshGame) {
            if (mouseEvent == MouseEventConsumer.CLICKED_LEFT && released) {
                tickDark = itTime - 10;
            }
            return true;
        }
        boolean clearMode = buttonClear.isEnabled();
        boolean success= super.consume(x, y, mouseEvent, released);
        if(clearMode&&released){
            buttonClear.setEnabled(false);
        }
        return success;
    }

    private void disable(ArrayList<Button> list) {
        for (Button b : list) {
            b.setEnabled(false);
        }
    }

    private void enable(ArrayList<Button> list) {
        for (Button b : list) {
            b.setEnabled(true);
        }
    }

    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
