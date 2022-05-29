package cooble.ch.music;

import cooble.ch.core.Game;
import cooble.ch.logger.Log;
import cooble.ch.saving.Saver;
import cooble.ch.saving.SaverUtil;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Plays various voices.
 * its possible to have more persons talking at once if speakerName is set.
 * Needs to call setLanguage()
 */
public class VPlayer {
    private static String language;
    private static String langsFolder;
    private static String currentLangFolder;
    private static Saver saver;

    private static final boolean consoleLoadOutput = false;
    private static boolean loadIfNotVoice = false;

    private static final Map<String, String> sounds = new HashMap<>();

    private static HashMap<String, SlickSound> personsTalking = new HashMap<>();

    public static void load(Saver saver) {
        sounds.clear();
        VPlayer.saver = saver;
        langsFolder = saver.SOUND_PATH+ "/voice/";
        currentLangFolder = langsFolder + language + "/";
        List<String> voices = saver.getIO().findResource((currentLangFolder), null);
        for (String f : voices) {
            String name = f;
            if (name.contains("voice/"+language))
                name = name.substring(name.indexOf("voice/"+language) +( "voice/"+language).length()+1);
            name = name.replace('/', '.');
            name = name.replace('\\', '.');
            name = name.replace("..", ".");
            name = name.replace("..", ".");
            if (name.startsWith("."))
                name = name.substring(1);
            if (name.startsWith("/"))
                name = name.substring(1);
            sounds.put(SaverUtil.removeSuffix(name), f);
        }
        if (consoleLoadOutput) {
            sounds.forEach((s, file) -> Log.println("sound " + s + " <> " + file));
        }

    }

    public static void setLanguage(String language, Saver saver) {
        VPlayer.language = language;
        currentLangFolder = langsFolder + language + "/";
        load(saver);
    }

    private static boolean secondTry;

    /**
     *
     * @param speakerName
     * @param name
     * @param volume
     * @return duration
     */
    public static int speak(String speakerName, String name, double volume) {
        if (volume == 0)
            volume = 1;
        mute(speakerName);
        String fileName = name;
        String file = sounds.get(fileName);
        if (file != null) {
            SlickSound sound = new SlickSound(file, volume, false);
            sound.setVolume(Game.getSettings().getDouble(Game.getSettings().VOICE_VOLUME));
            if (personsTalking.get(speakerName) != null) {
                personsTalking.get(speakerName).stop();
            }
            personsTalking.put(speakerName, sound);
            sound.start();
            return getDuration(file);

        } else {
            if (loadIfNotVoice) {
                load(saver);
                loadIfNotVoice = false;
                secondTry = true;
                int out = speak(speakerName, name, volume);
                secondTry = false;
                loadIfNotVoice = true;
                if (out == 0) {
                    Log.println("voice " + language + "/" + name + " does not exist");
                }
                return out;

            } else {
                if (!secondTry)
                    Log.println("voice " + language + "/" + name + " does not exist");
            }
        }
        return 0;
    }

    public static int speakFromPath(String speakerName, String file) {
        file = chooseRightFile(file);
        SlickSound sound = new SlickSound(Game.saver.getIO().makeResString(file), Game.getSettings().getDouble(Game.getSettings().VOICE_VOLUME), false);
        if (personsTalking.get(speakerName) != null) {
            personsTalking.get(speakerName).stop();
        }
        personsTalking.put(speakerName, sound);
        sound.start();
        return getDuration(file);
    }

    public static int getDuration(String file) {

        file = chooseRightFile(file);
        double seconds = 0;
        try {
            InputStream stream = Game.saver.getIO().getResourceAsStream(file);
            if (file.endsWith("wav")) {
                seconds = getWavDuration(stream);
            } else if (file.endsWith("ogg")) {
                seconds = getOggDuration(stream);
            } else throw new Exception();
        } catch (Exception e) {
            Log.println("Cannot load sound: " + file, Log.LogType.ERROR);
        }
        return (int) (seconds * Game.core.TARGET_TPS);

    }

    public static void mute(String speakerName) {
        SlickSound currentSound = personsTalking.get(speakerName);

        if (currentSound != null) {
            currentSound.stop();
        }
    }

    private static String chooseRightFile(String file) {
        file = SaverUtil.setSuffix(file, "wav");
        if (!Game.saver.getIO().existResource(file))
            file = SaverUtil.setSuffix(file, "ogg");
        return file;
    }

    /**
     * @param stream
     * @return number in seconds
     * @throws Exception
     */
    private static double getOggDuration(InputStream stream) throws Exception {
        String oggsMark = "";
        int samples = 0;
        int rate = 0;
        while (stream.available() > 0) {
            oggsMark = fifo(oggsMark, (char) stream.read(), 6);
            if (oggsMark.endsWith("OggS")) {
                oggsMark = "";
                stream.skip(2);
                byte[] byteArray = new byte[8];
                stream.read(byteArray);
                ByteBuffer bb = ByteBuffer.wrap(byteArray);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                samples = bb.getInt(0);
            } else if (oggsMark.endsWith("vorbis") && rate == 0) {
                oggsMark = "";
                stream.skip(5);
                byte[] byteArray = new byte[4];
                stream.read(byteArray);
                ByteBuffer bb = ByteBuffer.wrap(byteArray);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                rate = bb.getInt(0);
            }
        }
        stream.close();
        return (double) samples / (double) rate;
    }

    /**
     * @param stream
     * @return number in seconds
     * @throws Exception
     */
    private static double getWavDuration(InputStream stream) throws Exception {
        AudioInputStream audioInputStream = null;
        audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(stream));
        AudioFormat format = audioInputStream.getFormat();
        long frames = audioInputStream.getFrameLength();
        double durationInSeconds = (frames + 0.0) / format.getFrameRate();
        return durationInSeconds;
    }

    private static String fifo(String src, char apend, int size) {
        if (src.length() < size) {
            return src + apend;
        }
        return src.substring(1) + apend;
    }

}
