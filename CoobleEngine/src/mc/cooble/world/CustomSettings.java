package mc.cooble.world;

/**
 * Created by Matej on 22.7.2016.
 */
public class CustomSettings extends Settings {

    public final String SONG_VOLUME = "song_volume";
    public final String SOUND_VOLUME = "sound_volume";
    public final String VOICE_VOLUME = "voice_volume";
    public final String LANG = "lang";
    public final String FULLSCREEN = "fullscreen";

    public CustomSettings(){
        setAttribute(SONG_VOLUME, 0.4);
        setAttribute(SOUND_VOLUME, 1);
        setAttribute(VOICE_VOLUME, 1);
        setAttribute(LANG, "en");
        setAttribute(FULLSCREEN, false);
    }


}
