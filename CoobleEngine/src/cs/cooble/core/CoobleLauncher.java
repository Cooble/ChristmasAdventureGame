package cs.cooble.core;

import cs.cooble.logger.Log;
import cs.cooble.world.CustomSettings;

/**
 * Reads all the program arguments and sets the game atributes
 */
public class CoobleLauncher {

    private static final String prefixScreen = "screen:";
    private static final String prefixDebug = "debug:";
    private static final String prefixLang = "lang:";
    private static final String prefixNoSave = "ns";
    private static final String prefixSound = "sound:";
    private static final String prefixVoice = "voice:";
    private static final String prefixMusic = "music:";
    private static final String prefixAudio = "audio:";
    private static final String prefixFPS = "fps";


    public static void printMemory() {
        Log.println("Memory: " + Runtime.getRuntime().totalMemory() / 1000000 + "MB");

    }

    /**
     * Sets default values for Game from program args
     * @param args
     */
    public static void main(String[] args) {
        Log.println("Program has started with memory of: " + Runtime.getRuntime().totalMemory() / 1000000 + "MB");
        if (args.length > 0) {
            String enableIT = System.getProperty("enableIT");
            if("false".equals(enableIT)){
                Game.enableIT=false;
            }
            Log.println("Args for program loaded:");
            Log.println("************************");

            for (String arg : args) {
                Log.println("-> " + arg);
            }
            Log.println("========================");

            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith(prefixScreen)) {
                    args[i] = args[i].substring(prefixScreen.length());
                    try {
                        Game.setScreenSize(Integer.parseInt(args[i]));
                    } catch (Exception e) {
                        if (args[i].toLowerCase().startsWith("full")) {
                            Game.setScreenSize(Game.FULL_SCREEN);
                        }
                    }
                }
                if (args[i].startsWith(prefixDebug)) {
                    args[i] = args[i].substring(prefixDebug.length()).toLowerCase();
                    Game.isDebugging = args[i].equals("true") || args[i].equals("on") || args[i].equals("1");
                }
                if (args[i].startsWith(prefixFPS)) {
                  Game.isFPS=true;
                }
                if (args[i].startsWith(prefixLang)) {
                    args[i] = args[i].substring(prefixDebug.length() - 1).toLowerCase();
                    Game.setLanguage(args[i]);
                }
                if (args[i].startsWith(prefixNoSave)) {
                    Game.noSave = true;
                    Log.println("!!Game is run in NoSave MODE!!", Log.LogType.WARN);
                }
                CustomSettings settings = Game.getSettings();
                if (args[i].startsWith(prefixMusic)) {
                    args[i] = args[i].substring(prefixMusic.length());
                    double d = Double.parseDouble(args[i]);
                    settings.setAttribute(settings.SONG_VOLUME,d);
                }
                if (args[i].startsWith(prefixSound)) {
                    args[i] = args[i].substring(prefixSound.length());
                    double d = Double.parseDouble(args[i]);
                    settings.setAttribute(settings.SOUND_VOLUME,d);
                }
                if (args[i].startsWith(prefixVoice)) {
                    args[i] = args[i].substring(prefixVoice.length());
                    double d = Double.parseDouble(args[i]);
                    settings.setAttribute(settings.VOICE_VOLUME,d);
                }
                if (args[i].startsWith(prefixAudio)) {
                    args[i] = args[i].substring(prefixAudio.length());
                    double d = Double.parseDouble(args[i]);
                    settings.setAttribute(settings.SONG_VOLUME,d);
                    settings.setAttribute(settings.SOUND_VOLUME,d);
                    settings.setAttribute(settings.VOICE_VOLUME,d);
                }

            }
        }
    }
}
