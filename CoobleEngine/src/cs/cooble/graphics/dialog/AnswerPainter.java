package cs.cooble.graphics.dialog;

import cs.cooble.core.Game;
import cs.cooble.event.MouseEventConsumer;
import cs.cooble.font.FontUtil;
import cs.cooble.graphics.Bitmap;
import cs.cooble.graphics.BitmapProvider;
import cs.cooble.graphics.MultiBitmapProvider;
import cs.cooble.music.MPlayer2;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Matej on 20.7.2017.
 */
public class AnswerPainter implements MultiBitmapProvider, MouseEventConsumer {
    private Font font;
    private DialogManager manager;
    private boolean enabled;
    private ArrayList<Color> answerColors = new ArrayList<>();
    public static final Color backgroundColor = new Color(0, 0, 0, 210);

    private Bitmap[] out;
    /**
     * Each caret represents one possible answer
     */
    private ArrayList<Bitmap> carets = new ArrayList<>();
    private Rectangle backRectangle, exitRectangle;
    private ArrayList<Rectangle> rectagles = new ArrayList<>();

    private Bitmap exitBitmap;
    private Bitmap backBitmap;
    private Bitmap backgroundBitmap;

    public AnswerPainter(Font font, DialogManager manager) {
        this.font = font;
        this.manager = manager;
     /*   java.awt.Color color = new java.awt.Color(50, 145, 255);
        java.awt.Color color2 = new java.awt.Color(93, 255, 65);
        java.awt.Color color3 = new java.awt.Color(255, 150, 85);
        java.awt.Color color3 = new java.awt.Color(255, 39, 42);
        java.awt.Color color3 = new java.awt.Color(255, 255, 255);*/
        answerColors.add(new Color(50, 145, 255));
        answerColors.add(new Color(93, 255, 65));
        answerColors.add(new Color(255, 150, 85));
        answerColors.add(new Color(255, 39, 42));
        answerColors.add(new Color(255, 255, 255));
        listToArray();

        exitBitmap = Bitmap.get("gui/exit");
        backBitmap = Bitmap.get("gui/back");

        backgroundBitmap = Bitmap.create(Game.renderer.PIXEL_WIDTH, (int) (Game.renderer.PIXEL_HEIGHT * 0.5), backgroundColor);
        backgroundBitmap.setOffset(0, (int) (Game.renderer.PIXEL_HEIGHT * 0.7));

        exitBitmap.setOffset(Game.renderer.PIXEL_WIDTH - exitBitmap.getWidth(), backgroundBitmap.getOffset()[1]);
        backBitmap.setOffset((int) (Game.renderer.PIXEL_WIDTH - backBitmap.getWidth() - exitBitmap.getWidth() * 1.5), backgroundBitmap.getOffset()[1]);

        exitRectangle = new Rectangle(exitBitmap.getOffset()[0], exitBitmap.getOffset()[1], exitBitmap.getWidth(), exitBitmap.getHeight());
        backRectangle = new Rectangle(backBitmap.getOffset()[0], backBitmap.getOffset()[1], backBitmap.getWidth(), backBitmap.getHeight());
    }

    void setCarets(ArrayList<String> carets) {
        //  if (carets.size() > 3) {
        //     new Exception("Too much carets to handle max is 3. Current is:" + carets.size()).printStackTrace();
        //      return;
        //  }
        this.carets.clear();
        this.rectagles.clear();
        highlights.clear();
        for (int i = 0; i < carets.size(); i++) {
            highlights.add(false);
        }
        int allTextLength = 0;
        for (String caret : carets) {
            if(caret==null){
                throw new NullPointerException("[AnswerPainter]: caret is null!");
            }
            allTextLength += (int) (font.getWidth(caret) * 1.1);
        }
        for (int i = 0; i < carets.size(); i++) {
            String caret = carets.get(i);
            Bitmap card = Bitmap.create((int) (font.getWidth(caret) * 1.1), (int) (font.getHeight(caret) * 1.1), new Color(0, 0, 0, 0));
            int space = Game.renderer.PIXEL_WIDTH - allTextLength;
            int gap = space / (carets.size());
            card.setOffset(gap / 2 + gap * i + getSumLengthOfCarets(i - 1), (int) (Game.renderer.PIXEL_HEIGHT * 0.8));
            rectagles.add(new Rectangle(card.getOffset()[0], card.getOffset()[1], card.getWidth(), card.getHeight()));
            Graphics g = null;
            try {
                g = card.getImage().getGraphics();
            } catch (SlickException e) {
                e.printStackTrace();
            }
            g.clear();
            g.setColor(new Color(0, 0, 0, 0));
            g.fillRect(0, 0, card.getWidth(), card.getHeight());
            g.setColor(answerColors.get(i));
            g.setFont(font);
            g.drawString(FontUtil.translate(caret), /*Game.getWorld().inventory().isOpen() ? 22*2 :*/ 0, 0);
            g.flush();
            this.carets.add(card);

        }
        listToArray();
    }

    /**
     * @param maxIndex inclusive
     * @return pixel length
     */
    public int getSumLengthOfCarets(int maxIndex) {
        int out = 0;
        for (int i = 0; i < maxIndex + 1; i++) {
            out += carets.get(i).getWidth();
        }
        return out;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    private void listToArray() {

        out = new Bitmap[carets.size() + 3];
        out[0] = backgroundBitmap;

        for (int i = 0; i < carets.size(); i++) {
            out[i + 1] = carets.get(i);
        }
        out[carets.size() + 1] = exitBitmap;
        out[carets.size() + 2] = backBitmap;
    }

    @Override
    public BitmapProvider[] getBitmaps() {
        return out;
    }

    @Override
    public boolean shouldRender() {
        return enabled;
    }

    @Override
    public boolean consume(int x, int y, int state, boolean released) {
        x /= Game.renderer.PIXEL_SIZE;
        y /= Game.renderer.PIXEL_SIZE;
        if (state == MouseEventConsumer.CLICKED_LEFT) {
            if (!released)
                return true;
            if (backRectangle.contains(x, y)) {
                manager.answered(DialogManager.BACK);
                MPlayer2.playSound("pop_2",0.5);
                return true;
            }
            if (exitRectangle.contains(x, y)) {
                manager.answered(DialogManager.EXIT);
                MPlayer2.playSound("pop_2",0.5);
                return true;
            }
            int indexOfAnswer = -1;
            for (int i = 0; i < rectagles.size(); i++) {
                if (rectagles.get(i).contains(x, y)) {
                    indexOfAnswer = i;
                    break;
                }
            }
            if (indexOfAnswer == -1)
                return true;
            manager.answered(indexOfAnswer);
            return true;
        }
        if (state == MouseEventConsumer.MOUSE_MOVED) {
            for (int i = 0; i < rectagles.size(); i++) {
                Rectangle rectangle = rectagles.get(i);
                if (rectangle.contains(x, y)) {
                    setHighlighted(i, true);
                } else setHighlighted(i, false);
            }
        }
        return true;
    }

    private ArrayList<Boolean> highlights = new ArrayList<>();

    private void setHighlighted(int index, boolean highlight) {
        if (highlights.get(index) && !highlight) {//clear highlight
            highlights.set(index, false);
            Bitmap card = carets.get(index);
            card.setOffset(card.getOffset()[0], (int) (card.getOffset()[1] + Game.renderer.PIXEL_HEIGHT / 32));
            return;
        }
        if (!highlights.get(index) && highlight) {
            highlights.set(index, true);
            Bitmap card = carets.get(index);
            card.setOffset(card.getOffset()[0], (int) (card.getOffset()[1] - Game.renderer.PIXEL_HEIGHT / 32));
            MPlayer2.playSound("pop_2", 0.5);


        }

    }
}
