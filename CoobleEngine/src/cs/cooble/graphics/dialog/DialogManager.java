package cs.cooble.graphics.dialog;

import cs.cooble.core.Game;
import cs.cooble.event.Event;
import cs.cooble.event.MouseEventConsumer;
import cs.cooble.music.MPlayer2;
import cs.cooble.window.Tickable;
import org.newdawn.slick.Color;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Matej on 20.7.2017.
 * Used to ask for answers for player (user input)
 * gui thing
 */
public class DialogManager implements MouseEventConsumer, Tickable {

    private boolean enabled;

    public static final int BACK = -1;
    public static final int EXIT = -2;

    private AnswerPainter answerPainter;
    private DialogPainter dialogPainter;


    public DialogManager() {
        answerPainter = new AnswerPainter(Game.smallFont, this);
        dialogPainter = new DialogPainter(this, Game.smallFont);
        dialogPainter.setMaxQuerries(0);
        answer = -100;
    }

    /**
     * @param possibilities
     * @return index of chosen one or magic constants
     */
    public void ask(String... possibilities) {
        dialogPainter.setEnabled(false);
        dialogPainter.clearAllText();
        answerPainter.setEnabled(true);
        answer = -100;
        ArrayList<String> list = new ArrayList<>();
        Collections.addAll(list, possibilities);
        answerPainter.setCarets(list);
    }

    public int waitForAnswer() {
        while (answer == -100) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        answerPainter.setEnabled(false);
        return answer;
    }

    /**
     * @param s
     * @param speed
     * @param color
     * @return time for which it will be drawn for
     */
    public int say(String s, int speed, Color color) {
        sayID++;
        answerPainter.setEnabled(false);
        dialogPainter.setEnabled(true);
        dialogPainter.writeLine(s, speed, color);
        return s.length() * speed;

    }

    private int sayID;

    /**
     * @param s
     * @param speed
     * @param color
     * @return time for which it will be drawn for
     */
    public int say(String s, int speed, Color color, int textLife) {
        int delay = say(s, speed, color);
        sayID++;
        final int id = sayID;
        if (textLife != 0)
            Game.core.EVENT_BUS.addDelayedEvent(textLife + delay, new Event() {
                @Override
                public void dispatchEvent() {
                    if (sayID == id)//if nobody has added new lines
                        dialogPainter.clearAllText();
                }
            });
        return delay;

    }

    private void setDialogEnabled(boolean enabled) {
        dialogPainter.setEnabled(enabled);
    }


    public AnswerPainter getAnswerPainter() {
        return answerPainter;
    }

    @Override
    public boolean consume(int x, int y, int state, boolean released) {
        if (!answerPainter.shouldRender())
            return false;
        return answerPainter.consume(x, y, state, released);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void tick() {
        dialogPainter.tick();
    }

    private int answer;

    void answered(int indexOfAnswer) {
        answer = indexOfAnswer;
        if (indexOfAnswer == EXIT) {
            answerPainter.setEnabled(false);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public DialogPainter getDialogPainter() {
        return dialogPainter;
    }

    public void stopSpeaking() {
        dialogPainter.killCurrentQuerry();
    }

    /**
     * skips boring long writing and writes whole line at once
     */
    public void writeAtOnce() {
        dialogPainter.writeAtOnce();
    }

    public void clearText() {
        dialogPainter.clearAllText();
    }

    public String getFirstLineText() {
        if (!dialogPainter.shouldRender())
            return null;
        return dialogPainter.getFirstLine();
    }

    public void setText(String text, Color color, boolean paragraph) {
        dialogPainter.setText(text, color, paragraph);
    }

    public void setText(String text) {
        dialogPainter.setText(text, Color.white, false);
    }

    /**
     * @return true if writing something now
     */
    public boolean isBusy() {
        return dialogPainter.isBusy();
    }
}
