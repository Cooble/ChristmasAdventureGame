package cooble.ch.graphics.dialog;

import cooble.ch.core.Game;
import cooble.ch.font.FontUtil;
import cooble.ch.graphics.Bitmap;
import cooble.ch.graphics.BitmapProvider;
import cooble.ch.graphics.MultiBitmapProvider;
import cooble.ch.translate.SlovoManager;
import cooble.ch.window.Tickable;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Created by Matej on 20.7.2017.
 */
public class DialogPainter implements MultiBitmapProvider, Tickable {
    private boolean enabled;
    private Bitmap background;
    private Bitmap text;
    private Bitmap[] out;
    private DialogManager manager;
    private Font font;


    private String[] lines;
    private Color[] colors;
    private Boolean[] paragraph;
    private String targetLine;
    private int speed;

    private int maxQuerries;
    // private static final int MAX_LENGTH = 70;
    private static final int MAX_LENGTH = 60;

    private boolean enableBitmapRefresh;

    private Queue<Querry> querries = new LinkedList<>();

    public void writeAtOnce() {
        int oldspeed = speed;
        speed = 0;
        enableBitmapRefresh = false;
        while (isBusy())
            tick();
        enableBitmapRefresh = true;
        speed = oldspeed;
        refreshBitmap();

    }

    public String getFirstLine() {
        return lines[0];
    }

    private class Querry {
        String s;
        int speed;
        Color color;

        Querry(String s, int speed, Color color) {
            this.s = s;
            this.speed = speed;
            this.color = color;
        }
    }

    public DialogPainter(DialogManager manager, Font font) {
        this.manager = manager;
        this.font = font;
        out = new Bitmap[2];

        background = Bitmap.create(Game.renderer.PIXEL_WIDTH,(Game.renderer.PIXEL_HEIGHT), AnswerPainter.backgroundColor);
        text = Bitmap.create(Game.renderer.PIXEL_WIDTH, (Game.renderer.PIXEL_HEIGHT));
        background.setOffset(0, Game.renderer.PIXEL_HEIGHT / 2);

        out[0] = background;
        out[1] = text;
        setMaxLines(6);
        speed = 1;

        enableBitmapRefresh = true;
    }

    private void refreshBitmap() {
        if (!enableBitmapRefresh)
            return;
        text.clear();
        Graphics g = null;
        try {
            g = text.getImage().getGraphics();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        g.setFont(font);
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, text.getWidth(), text.getHeight());
        int nullLine = 0;
        int paragraphNumber = 0;
        int paragraphLength = Game.renderer.PIXEL_HEIGHT / 64;
        for (int i = 0; i < lines.length; i++) {
            if (lines[i] == null || lines[i].equals(""))
                break;
            nullLine = i;
            g.setColor(colors[i]);
            try {//todo nullpointer exception
                g.drawString(FontUtil.translate(lines[i]), Game.renderer.PIXEL_WIDTH / 32 + (paragraph[i] ? Game.renderer.PIXEL_WIDTH / 24 : 0), Game.renderer.PIXEL_HEIGHT - (int) (1.3 * i * font.getHeight(lines[i])) - (int) (font.getLineHeight() * 1.5) + ((paragraphNumber + 1) * paragraphLength));

            }catch (Exception ignored){}
            paragraphNumber++;
        }
        g.flush();
        if(lines[0]==null){
            background.setOffset(0,Game.renderer.PIXEL_HEIGHT);
        }else
        background.setOffset(0, Game.renderer.PIXEL_HEIGHT - (int) (1.3 * nullLine * font.getHeight(lines[nullLine])) - (int) (font.getLineHeight() * 1.5) + ((paragraphNumber + 1) * paragraphLength) - font.getLineHeight() / 4);
    }

    /**
     * @param lines how many lines is possible to render simultaneously
     */
    public void setMaxLines(int lines) {
        this.lines = new String[lines];
        colors = new Color[lines];
        paragraph = new Boolean[lines];
        clearAllText();
    }

    public void clearAllText() {
        targetLine = null;
        lines = new String[lines.length];
        refreshBitmap();
    }

    String lastCommand;

    public void writeLine(String string, int speed, Color color) {
        if (isBusy()) {
            if (querries.size() >= maxQuerries) {//if alreadymax querries-> dump all
                clearAllText();
                querries.clear();
            }
            querries.add(new Querry(string, speed, color));
        } else {
            if(isSetHardText)
                clearAllText();
            isSetHardText=false;
            isBusy = true;
            this.speed = speed;
            if (!string.equals(lastCommand)) {//caling thee same text again -> dont want more lines with same -> budeme psat na tu samou lineu
                enter();
            }
            colors[0] = color;
            targetLine = string;
            if (!string.equals(lastCommand)) {//caling thee same text again -> dont want more lines with same -> budeme psat na tu samou lineu
                writeSmallLine();
            }

        }
    }

    public void killCurrentQuerry() {
        isBusy = false;
    }

    /**
     * really small line to make boundary to some speaker (color)
     * =paragraph
     */
    public void writeSmallLine() {
        paragraph[0] = true;
    }

    @Override
    public BitmapProvider[] getBitmaps() {
        return out;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled)
            clearAllText();
    }

    @Override
    public boolean shouldRender() {
        return enabled;
    }

    private boolean isBusy;
    private int spee;

    @Override
    public void tick() {
        if (!isBusy()) {
            if (querries.size() == 0)
                return;
            Querry querry = querries.remove();
            if (querry != null) {
                writeLine(querry.s, querry.speed, querry.color);
                lastCommand = querry.s;
                return;
            }
        }
        spee++;
        if (spee < speed)
            return;
        spee = 0;
        if (!Objects.equals(targetLine, line()) && targetLine != null & !Objects.equals(targetLine, "")) {
            int currentPocetSlov = SlovoManager.pocetWords(line());
            int newPocetSlov = SlovoManager.pocetWords(theoreticalPlus());
            if (currentPocetSlov == newPocetSlov) {
                plus();
                refreshBitmap();
                return;
            }
            String[] targetLineVeta = SlovoManager.getWordsFromSentence(targetLine);
            if (targetLineVeta.length == newPocetSlov) {//pocet slov sedi nutno dopsat.
                // lines[0]=targetLine;
                // targetLine = null;
                plus();
                refreshBitmap();
                return;
            }
            String vetaOSlovoNavic = SlovoManager.createVetaFromArray(targetLineVeta, 0, newPocetSlov);
            if (vetaOSlovoNavic.length() > MAX_LENGTH) {
                enter();
                targetLine = SlovoManager.createVetaFromArray(targetLineVeta, newPocetSlov - 1);
            }
            plus();
            refreshBitmap();
        } else isBusy = false;
    }

    private String line() {
        if(lines[0]==null)
            return "";
        return lines[0];
    }

    private String theoreticalPlus() {
        try {
            return targetLine.substring(0, line().length() + 1);

        }
        catch (Exception e){
            return " ";
        }
    }

    private void plus() {
        lines[0] = theoreticalPlus();
    }

    private void enter() {
        String[] newLines = new String[lines.length];
        System.arraycopy(lines, 0, newLines, 1, newLines.length - 1);
        lines = newLines;
        newLines[0] = "";

        Color[] newColors = new Color[colors.length];
        System.arraycopy(colors, 0, newColors, 1, newColors.length - 1);
        colors = newColors;
        if(newColors.length!=1) {
            newColors[0] = newColors[1];
        }
        Boolean[] newBooleans = new Boolean[paragraph.length];
        System.arraycopy(paragraph, 0, newBooleans, 1, newBooleans.length - 1);
        paragraph = newBooleans;
        newBooleans[0] = false;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setMaxQuerries(int maxQuerries) {
        this.maxQuerries = maxQuerries;
    }

    private boolean isSetHardText;
    public void setText(String text,Color color,boolean paragraph){
        querries.clear();
        isBusy=false;
        this.paragraph[0]=paragraph;
        clearAllText();
        if(text!=null) {
            isSetHardText=true;
            this.setEnabled(true);
            querries.add(new Querry(text, 1, color));
            tick();
            writeAtOnce();
        }
        else {
            this.setEnabled(false);
            isSetHardText=false;
        }
    }
}
