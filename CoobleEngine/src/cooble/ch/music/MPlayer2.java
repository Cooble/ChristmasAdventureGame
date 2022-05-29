package cooble.ch.music;

import com.sun.istack.internal.NotNull;
import cooble.ch.core.Game;
import cooble.ch.saving.SaverUtil;
import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matej on 14.12.2016.
 */
public final class MPlayer2 {
    private static final String SOUNDS = "sounds/";
    private static final String SUFFIX_WAV = "wav";
    private static final String SUFFIX_OGG = "ogg";
    public static final String MUSIC = "music/";
    private static final String SOUND = "sound/";


    private static Map<String, SlickSound> loadedAudio = new HashMap<>();
    private static final SlickSound[] sounds = new SlickSound[20];
    private static final int[] ticksToStart = new int[sounds.length];
    private static int soundsIndex;

    private static SlickSound currentMusic;
    private static SlickSound loopSound;

    /**
     * Ensures that this sound is played in the background
     * @param soundName
     * @param volume
     */
    public static void loopSound(String soundName, double volume) {
        soundName = getSoundPath(soundName);

        if(loopSound!=null&&soundName.equals(loopSound.getPath()))
            return;
        stopLoop();
        if (loadedAudio.containsKey(soundName)) {
            loopSound = loadedAudio.get(soundName);
            loopSound.setDefaultVolume(volume);

        } else
            loopSound = new SlickSound(soundName, volume, false);

        loopSound.loop(Game.getSettings().getDouble(Game.getSettings().SOUND_VOLUME));
    }

    public static void stopLoop() {
        if (loopSound != null)
            loopSound.stop();
        loopSound=null;
    }

    public static void loadSound(String soundName) {
        String soundPath = getSoundPath(soundName);
        loadAudio(soundName, soundPath);
    }

    public static void loadSong(String songName) {
        String soundPath = getSongPath(songName);
        loadAudio(songName, soundPath);
    }

    private static void loadAudio(String name, String path) {
        soundsIndex++;
        if (soundsIndex == sounds.length)
            soundsIndex = 0;
        loadedAudio.put(name, new SlickSound(path, 1, false));
    }

    public static void playSound(@NotNull String soundName, double volume, int delayTicks) {
        soundName = getSoundPath(soundName);
        soundsIndex++;
        if (soundsIndex == sounds.length)
            soundsIndex = 0;
        if (loadedAudio.containsKey(soundName)) {
            sounds[soundsIndex] = loadedAudio.get(soundName);
            sounds[soundsIndex].setDefaultVolume(volume);

        } else
            sounds[soundsIndex] = new SlickSound(soundName, volume, false);
        if (delayTicks == 0)
            delayTicks = 1;
        if (sounds[soundsIndex] != null)
            sounds[soundsIndex].setVolume(Game.getSettings().getDouble(Game.getSettings().SOUND_VOLUME));
        ticksToStart[soundsIndex] = delayTicks;
    }

    public static void playSound(String soundName, double volume) {
        playSound(soundName, volume, 0);
    }

    public static void playSound(String soundName) {
        playSound(soundName, 1);
    }

    public static void playSoundIfNotExist(String soundName, double volume) {
        String path = getSoundPath(soundName);
        for (SlickSound sound : sounds) {
            if (sound != null && sound.getSound().playing() && path.equals(sound.getPath()))//exists
                return;
        }
        playSound(soundName, volume);

    }

    public static void playSoundIfNotExist(String soundName) {
        playSoundIfNotExist(soundName, 1);
    }

    /**
     * @param songName
     * @param volume
     * @param fadeOutTicksLast how long does it take to stop lastSong and start playing new one (if 0 -> forced new song to start immediately), if no song is playing -> start immediately
     * @param fadeInTicks      how long does it take to set new song to max volume
     */
    public static void playSong(@NotNull String songName, double volume, int fadeOutTicksLast, int fadeInTicks, final boolean loop) {
        if (fadeInTicks == 0)
            fadeInTicks = 1;
        final int fadeIn = fadeInTicks;
        songName = getSongPath(songName);
        final SlickSound currentEnding = currentMusic;
        boolean needWait = false;
        if (currentMusic != null && currentMusic.getMusic().playing()) {
            needWait = true;
            currentMusic.fade(fadeOutTicksLast, 0, true);
            currentMusic.setMusicListener(new MusicListener() {
                @Override
                public void musicEnded(Music music) {
                    currentMusic.setVolume(0);
                    // currentMusic.start();
                    if (loop)
                        currentMusic.getMusic().loop();
                    else currentMusic.start();
                    currentMusic.fade(fadeIn, Game.getSettings().getDouble(Game.getSettings().SONG_VOLUME), false);
                    currentEnding.removeListener();

                }

                @Override
                public void musicSwapped(Music music, Music music1) {

                }
            });
        }
        if (loadedAudio.containsKey(songName)) {
            currentMusic = loadedAudio.get(songName);
            currentMusic.removeListener();
            currentMusic.setDefaultVolume(volume);
        } else {
            currentMusic = new SlickSound(songName, volume, true);
        }
        currentMusic.setVolume(Game.getSettings().getDouble(Game.getSettings().SONG_VOLUME));

        if (!needWait) {
            currentMusic.start();
            currentMusic.fade(fadeInTicks, Game.getSettings().getDouble(Game.getSettings().SONG_VOLUME), false);
        }
    }

    /**
     * @param songName
     * @param volume
     * @param fadeInTicks how long does it take to set new song to max volume
     */
    public static void playSong(@NotNull String songName, double volume, int fadeInTicks) {
        playSong(songName, volume, 1, fadeInTicks, false);
    }

    public static void playSong(String song) {
        playSong(song, 1, 0);
    }

    /**
     * Checks whether isSongPlaying(). if no plays it
     *
     * @param songName
     * @param volume
     * @param fadeInTicks
     * @return !isSongPlaying()
     */
    public static boolean playSongIfSilence(@NotNull String songName, double volume, int fadeInTicks) {
        if (!isSongPlaying()) {
            playSong(songName, volume, 1, fadeInTicks, false);
            return true;
        }
        return false;
    }

    /**
     * with this you can be sure that this song is played, if is not -> it will start
     *
     * @param songName
     * @param volume
     * @param fadeInTicks how long does it take to reach max volume
     */
    public static void playSongIfNot(@NotNull String songName, double volume, int fadeInTicks) {
        if (currentMusic == null || !currentMusic.getMusic().playing() || !currentMusic.getPath().equals(getSongPath(songName))) {
            playSong(songName, volume, fadeInTicks);
        }
    }

    public static void stopSong(int fadeOut) {
        if (fadeOut == 0) {
            stopSong();
            return;
        }
        if (currentMusic != null && currentMusic.getMusic().playing()) {
            currentMusic.getMusic().fade(fadeOut, 0, true);
            currentMusic = null;
        }
    }

    public static void stopSong() {
        if (currentMusic != null && currentMusic.getMusic().playing()) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    private static String getSongPath(String name) {
        String real = Game.saver.SOUND_PATH + MUSIC + name + "." + SUFFIX_WAV;
        if (!Game.saver.getIO().existResource(real)) {
            real = SaverUtil.setSuffix(real, SUFFIX_OGG);
        }
        return real;
    }

    private static String getSoundPath(String name) {
        String real = Game.saver.SOUND_PATH + SOUND + name + "." + SUFFIX_WAV;
        if (!Game.saver.getIO().existResource(real)) {
            real = SaverUtil.setSuffix(real, SUFFIX_OGG);
        }
        return real;
    }

    public static boolean isSongPlaying() {
        return currentMusic != null && currentMusic.getMusic().playing();
    }

    private static double oldMusicVolume = -1;

    public static void tick() {
        if (oldMusicVolume != Game.getSettings().getDouble(Game.getSettings().SONG_VOLUME)) {
            oldMusicVolume = Game.getSettings().getDouble(Game.getSettings().SONG_VOLUME);
            if (currentMusic != null && currentMusic.getMusic().playing()) {
                currentMusic.setVolume((float) oldMusicVolume);
            }
        }
        for (int i = 0; i < ticksToStart.length; i++) {
            if (ticksToStart[i] > 0) {
                ticksToStart[i]--;
                if (ticksToStart[i] == 0) {
                    if (sounds[i] != null && !sounds[i].getSound().playing()) {
                        sounds[i].setVolume(Game.getSettings().getDouble(Game.getSettings().SOUND_VOLUME));
                        sounds[i].start();
                    }

                }

            }
        }
    }

    public static void stopSound(String soundName) {
        soundName = getSoundPath(soundName);
        for (int i = 0; i < sounds.length; i++) {
            SlickSound sound = sounds[i];
            if (sound == null)
                continue;
            // System.out.println("reading sound to remove "+sound.getPath()+" tagret: "+soundName);
            if (sound.getPath().equals(soundName)) {
                sound.stop();
                sounds[i] = null;
            }
        }
    }

    @Deprecated
    public static void playSongForced(String song, double volume) {
        playSong(song, volume, 0);

    }


    public static void playSongIfNot(String song) {
        playSongIfNot(song, 1, 0);
    }

    public static void setSongVolume(double songVolume, int fadeTicks) {
        if (currentMusic != null) {
            if (currentMusic.getMusic().playing()) {
                currentMusic.fade(fadeTicks, songVolume * Game.getSettings().getDouble(Game.getSettings().SONG_VOLUME), false);
            }
        }
    }
}
