package cs.cooble.graphics;

import com.sun.istack.internal.Nullable;
import cs.cooble.core.Game;
import cs.cooble.translate.SlovoManager;
import cs.cooble.window.Tickable;

import java.util.ArrayList;

@Deprecated
/**
 * Created by Matej on 20.12.2015.
 * Handles showing text to player via TextPainter
 * creates effects like word typing
 */
public final class TextManagerOLD implements Tickable {
    /**
     * Text which is shown on screen
     */
    private String[] text;//todo convert text to array of strings which are on some lines
    /**
     * Text which is to be shown on the screen
     */
    private String[] wantedText;

    /**
     * each maxTick it ticks
     */
    private int maxTicks = 1;
    private int currentTick;
    private int currentCharIndex;
    private int currentRowIndex;
    /**
     * each tick decrement, when reach zero then text=null
     */
    private int lifeTime;
    /**
     * how long the full written text will ve there before removed
     */
    private int textLife;

    /**
     * when its bigger than 0 ticking will be paused, each tick decrement wait, if wait = 0 normal ticking
     */
    private int wait;

    private String[] oldText;
    private boolean eraseText;


    private TextPainter textPainter;
    private boolean isBusy;

    public TextPainter getTextPainter() {
        return textPainter;
    }

    public TextManagerOLD(int pixelWidth, int pixelHeight) {
        oldText = new String[0];
        textPainter = new TextPainter(pixelWidth, pixelHeight, Game.smallFont);
    }

    /**
     * sets Immediately text on screen
     *
     * @param text will be there, until you call setText(null)
     */
    public void setText(@Nullable String text[]) {
        Game.dialog.setText(null);

      /* clearAllProcces();
        lifeTime = 0;
        this.text = text;*/
    }

    /**
     * sets Immediately text on screen
     *
     * @param text will be there, until you call setText(null)
     */
    public void setTextLine(@Nullable String text) {
       Game.dialog.setText(text);
        /*
        clearAllProcces();
        lifeTime = 0;
        this.text = new String[1];
        this.text[0] = text;*/

    }


    /**
     * clears everything which has something to do with current text spelling/showing
     */
    private void clearAllProcces() {
        currentTick = 0;
        currentCharIndex = 0;
        currentRowIndex = 0;
        wantedText = null;
        text = null;
        wait = 0;
    }

    public void setTextWithDelay(String text, int ticks, int textLife) {
        clearAllProcces();
        this.wantedText = stringToArray(text);

        this.text = new String[wantedText.length];
        this.maxTicks = ticks;
        this.textLife = textLife;
        this.isBusy=true;
    }

    private String[] stringToArray(String s) {
        final int maxRowLength = 37;

        if (s == null) return null;
        String[] words = SlovoManager.getWordsFromSentence(s);
        ArrayList<String> rows = new ArrayList<>();
        String row = "";
        for (String word : words) {
            if ((row.length() + word.length() + 1) < maxRowLength) {//+1->space
                row += " " + word;
            } else {
                rows.add(row);
                row = word;
            }
        }
        rows.add(row);
        String[] out = new String[rows.size()];
        out = rows.toArray(out);
        if (out[0].startsWith(" "))
            out[0] = out[0].substring(1);
        return out;
    }

    /**
     * @return text which is currently on screen with index 0->primary row
     */
    public String getFirstLineText() {
        if (text == null)
            return null;
        return text[0];
    }

    public String[] getFullText() {
        return text;
    }

    /**
     * @return text which is currently on screen
     */
    public String getText(byte index) {
        return text[index];
    }

    private int getDelayInReading(String s) {
        return 100;//todo
    }

    @Override
    public void tick() {//todo convert text to text[]
        if (wait > 0) {
            wait--;
        } else {
            if (wantedText != null) {
                currentTick++;
                if (currentTick == maxTicks) {
                    currentTick = 0;
                    currentCharIndex++;
                    if (currentCharIndex == wantedText[currentRowIndex].length() + 1) {
                        currentCharIndex = -1;
                        if(currentRowIndex==wantedText.length-1) {
                            wait = getDelayInReading(wantedText[currentRowIndex]);//delay after one line of text
                        }
                        wait=0;
                        if (wantedText.length != currentRowIndex + 1) {
                            currentRowIndex++;
                        } else {
                            eraseText = true;
                            wait=this.textLife;
                            wantedText=null;
                            isBusy=false;
                        }
                    } else {
                        text[currentRowIndex] = wantedText[currentRowIndex].substring(0, currentCharIndex);
                    }

                }

            } else if (lifeTime > 0) {
                lifeTime--;
                if (lifeTime == 0) {
                    text = null;
                    lifeTime = 0;
                }
            } else if (eraseText) {
                text = null;
                eraseText = false;
                wantedText = null;
                currentRowIndex = 0;
            }
        }
        if (text != null) {

            if (oldText != null && oldText.length == text.length) {
                boolean isChange = false;
                for (int i = 0; i < text.length; i++) {
                    if (text[i] != null)
                        if (!(text[i].equals(oldText[i]))) {
                            isChange = true;
                            break;
                        }
                }
                if (!isChange)
                    return;
            }
            oldText = new String[text.length];
            System.arraycopy(text, 0, oldText, 0, text.length);
            textPainter.writeOnBitmap(text);

        } else {
            textPainter.writeOnBitmap(text);
            oldText = null;
        }
    }

    /**
     * is talking and should not be interrupted
     * @return
     */
    public boolean isBusy(){
        return isBusy;
    }

}
