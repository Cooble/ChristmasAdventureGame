package mc.cooble.music;

import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/**
 * Created by Matej on 2.2.2017.
 */
public class SlickSound {
    private String path;
    private double defaultVolume;
    private Sound sound;
    private Music music;
    private double volume;

    private MusicListener listener;

    /**
     * @param path
     * @param volume         from 0->1.0
     * @param music_or_sound
     */
    public SlickSound(String path, double volume, boolean music_or_sound) {
        this.defaultVolume = volume;
        this.path = path;
        this.volume = 1;
        try {
            if (music_or_sound) {
                music = new Music(path);
            } else
                sound = new Sound(path);
        } catch (SlickException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        if (isMusicOrSound()) {
            music.setVolume((float) volume);
            music.play();
        } else
            sound.play(1, (float) volume);
    }

    public void stop() {
        if (isMusicOrSound()) {
            music.stop();
        } else
            sound.stop();
    }

    public Music getMusic() {
        return music;
    }

    public Sound getSound() {
        return sound;
    }

    public boolean isMusicOrSound() {
        return music != null;
    }

    public String getPath() {
        return path;
    }

    public double getDefaultVolume() {
        return defaultVolume;
    }

    public void setDefaultVolume(double defaultVolume) {
        this.defaultVolume = defaultVolume;
    }

    public void setMusicListener(MusicListener listener) {
        removeListener();
        if (music != null&&listener!=null) {
            this.listener = listener;
            music.addListener(listener);
        }
    }

    public void removeListener() {
        if(music!=null&&listener!=null) {
            music.removeListener(listener);
            this.listener = null;
        }
    }

    public void setVolume(double volume) {
       // if(volume*defaultVolume==this.volume)
       //     return;
        this.volume = volume*defaultVolume;
        if(music!=null)
            music.setVolume((float) volume);
    }
    public void fade(int duration,double endVolume,boolean stopAfterFade){
        if(music!=null)
            music.fade(duration, (float) (endVolume*getDefaultVolume()),stopAfterFade);
    }

    public void loop(double volume) {
        this.volume = volume*defaultVolume;
        if(music!=null)
            music.loop(1, (float) this.volume);
        else
            sound.loop(1, (float) this.volume);
    }
}
