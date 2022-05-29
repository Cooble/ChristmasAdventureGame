package mc.cooble.event;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import mc.cooble.core.Game;
import mc.cooble.entity.Talkable;
import mc.cooble.music.VPlayer;
import mc.cooble.translate.Translator;
import org.newdawn.slick.Color;

/**
 * Created by Matej on 31.12.2015.
 * Take care of visual/text/audio representation of speaking
 */
public class SpeakEvent implements Event {
    private static final int CHAR_LIFE = 2;
    private String id;

    private int maxCounting;

    private int speedDelay = 5;
    private Talkable talkable;

    private int textLife;

    private Color color;

    @Nullable
    private Runnable onEnd;
    private String speakerName;

    public static final int TEXT_LIFE = 100;

    private boolean stopped;

    private String voicePath;
    private int volume;

    /**
     * default speaker - joe
     *
     * @param id if id==null -> mutes talkable
     */
    public SpeakEvent(String id) {
        this(Game.getWorld().getUniCreature().toString(), id);
    }

    public SpeakEvent(String speakerName, String id) {
        this.id = id;
        talkable = Game.getWorld().getUniCreature();
        this.speakerName = speakerName;
        setColor(Color.white);
        volume=1;
    }

    public void setColor(@NotNull Color color) {
        this.color = color;
    }

    /**
     * this runnable will be called when talking ends
     * it won't be called if stop()
     *
     * @param onEnd
     */
    public void setOnEnd(Runnable onEnd) {
        this.onEnd = onEnd;
    }

    public void setSpeedDelay(int speedDelay) {
        this.speedDelay = speedDelay;
    }

    public void setTalkable(Talkable talkable) {
        this.talkable = talkable;
    }

    public void setTextLife(int textLife) {
        this.textLife = textLife;
    }

    public void setSpeaker(String speakID) {
        this.speakerName = speakID;
    }

    public void setCustomVoicePath(String path){
        voicePath=path;
    }

    @Override
    public void dispatchEvent() {
        if(stopped)
            return;
        if (id == null) {
            VPlayer.mute(speakerName);
            Game.dialog.clearText();
            if(talkable!=null)
                talkable.setIsTalking(false);
            return;
        }
        String translatedText = Translator.translate(id);
        if (translatedText == null)
            translatedText = id;

        if (textLife == 0) {//nespecifikovano kdy ma text zmizet
               textLife = translatedText.length() * CHAR_LIFE;
           // textLife = TEXT_LIFE;
        }
        if(textLife<0){
            textLife=0;
        }

        int speakTime;
        if(voicePath!=null){
            speakTime = VPlayer.speakFromPath(speakerName, voicePath);
        }
        else speakTime = VPlayer.speak(speakerName, id, volume);
        maxCounting = Game.dialog.say(translatedText, speakTime != 0 ? speakTime / translatedText.length() : speedDelay, color, textLife);

        talkable.setIsTalking(true);
        Game.core.EVENT_BUS.addDelayedEvent(maxCounting + textLife, new Event() {//todo remove +textlife
            @Override
            public void dispatchEvent() {
                if(stopped)
                    return;
                if (onEnd != null)
                    onEnd.run();
                talkable.setIsTalking(false);
            }
        });
    }

    public int getVoiceTimeTicks() {
        String translatedText = Translator.translate(id);
        if (translatedText == null)
            translatedText = id;
        int speak;
        if(voicePath!=null){
            speak= VPlayer.speakFromPath(speakerName, voicePath);
        }
        else speak = VPlayer.getDuration(id);

        if (speak == 0)
            return speedDelay * translatedText.length();
        return speak;

    }

    public int getTextLife() {
        return textLife;
    }

    public void stop() {
        stopped=true;
        Game.dialog.writeAtOnce();
        VPlayer.mute(speakerName);
    }
    public void stopWithErase(){
        stop();
        Game.dialog.getDialogPainter().setEnabled(false);
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }
}
