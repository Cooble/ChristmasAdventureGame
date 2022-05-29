package cooble.ch.entity;

import cooble.ch.graphics.Bitmap;
import cooble.ch.graphics.BitmapProvider;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.music.MPlayer2;
import cooble.ch.world.IActionRectangle;
import org.newdawn.slick.Font;

import java.awt.*;

/**
 * Created by Matej on 2.8.2017.
 */
public class ButtonShifting extends Button {


    private Bitmap ball;
    private double value;
    private int maxSize;
    private int offsetX;
    private boolean mouseDown;
    private String TextName;

    public ButtonShifting(int posX, int posY, int width, int height, Font font, Runnable clicked, String name) {
        super(posX, posY, width, height, Bitmap.get("gui/button_shift"),null, font, clicked);
        TextName = name;
        ball = Bitmap.get("gui/ball");
        maxSize = width-ball.getWidth();
        offsetX = posX;
        ball.setOffset(posX, posY + height / 2 - ball.getHeight() / 2);

        actionListener = new IActionRectangle() {
            @Override
            public Rectangle getRectangle() {
                return new Rectangle(pos[0], pos[1], width, height);
            }

            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!enabled)
                    return false;
                mouseDown = !released;
                if (released) {
                    MPlayer2.playSound("cvak_0", 0.3);
                }
                return true;
            }


            @Override
            public void mouseDragged(int x, int y) {
                if(!enabled)
                    return;
                x-=ball.getWidth()/2;
                if(mouseDown) {
                    if (x < offsetX)
                        setValue(0);
                    else if (x > offsetX + maxSize)
                        setValue(1);
                    else {
                        x -= offsetX;
                        setValue(x / (double) maxSize);
                    }
                }
            }

            @Override
            public void mouseMove(int x, int y) {
                if(!enabled)
                    return;
                if (mouseDown) {
                    mouseDown = false;
                }
            }
        };

    }

    @Override
    public BitmapProvider[] getBitmaps() {
        BitmapProvider[] out = super.getBitmaps();
        BitmapProvider[] out1 = new BitmapProvider[out.length + 1];
        System.arraycopy(out, 0, out1, 0, out.length);
        textPainter.getBitmaps()[0].getCurrentBitmap().setOffset(pos[0]- textPainter.getFont().getWidth(text)-10, pos[1]);
        out1[out1.length - 1] = ball;
        return out1;
    }

    public void setValue(double value) {
        this.value = value;
        ball.setOffset(offsetX + (int) (value * (double) maxSize), ball.getOffset()[1]);
        setText(TextName + " " +expandTo((int)( value * 100) + "%",true,5));
        clicked.run();

    }

    public double getValue() {
        return value;
    }

    public void setTextName(String textName) {
        TextName = textName;
    }

    private String expandTo(String src,boolean left,int targetChars){
        if(left){
            while (src.length()<targetChars){
                src=" "+src;
            }
        }
        else{
            while (src.length()<targetChars){
                src=src+" ";
            }
        }
        return src;
    }
}
