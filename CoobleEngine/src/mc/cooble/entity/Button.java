package mc.cooble.entity;

import com.sun.istack.internal.Nullable;
import mc.cooble.core.Game;
import mc.cooble.graphics.*;
import mc.cooble.inventory.item.ItemStack;
import mc.cooble.music.MPlayer2;
import mc.cooble.translate.Translator;
import mc.cooble.window.Tickable;
import mc.cooble.world.IActionRectangle;
import mc.cooble.world.MouseFocusGainIActionListenerWrapper;
import org.newdawn.slick.Font;

import java.awt.*;


/**
 * Created by Matej on 18.7.2016.
 */

public class Button implements Tickable, MultiBitmapProvider {
    protected BitmapStack bitmapStack;
    protected int[] pos;
    protected IActionRectangle actionListener;
    protected String text;
    protected String lore;
    protected TextPainter textPainter;
    protected boolean enabled;
    protected Runnable clicked;
    private static final int xTextOffset=10;

    public Button(int posX, int posY, int width, int height, Bitmap bitmap,@Nullable Bitmap onBitmap, Font font, Runnable clicked) {
        if(onBitmap==null)
            onBitmap=bitmap;
        this.bitmapStack = new BitmapStack(bitmap,onBitmap);
        this.clicked = clicked;
        textPainter = new TextPainter(width, height, font);
        bitmapStack.setOffset(posX, posY);
        pos = bitmapStack.getOffset();
        enabled = true;

        actionListener = new MouseFocusGainIActionListenerWrapper(new IActionRectangle() {
            @Override
            public Rectangle getRectangle() {
                return new Rectangle(posX, posY, width, height);
            }

            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!enabled)
                    return false;
                if (released) {
                    MPlayer2.playSound("click", 0.3);
                    clicked.run();
                }
                return true;
            }

            @Override
            public void onFocusLost() {
                if (!enabled)
                    return;
                if (lore != null) {
                    String translatedLore = Translator.translateTry(lore);
                    if (translatedLore != null) {
                        if (translatedLore.equals(Game.dialog.getFirstLineText()) && !Game.dialog.isBusy()) {
                            Game.dialog.setText(null);
                        }
                    }
                }
                bitmapStack.setCurrentIndex(0);

            }

            @Override
            public void onFocusGiven() {
                if (!enabled)
                    return;
                if (lore != null && !Game.dialog.isBusy()) {
                    Game.dialog.setText(Translator.translate(lore));
                }
                bitmapStack.setCurrentIndex(1);
            }
        });
    }

    public void setDefaultLore(String btnName) {
        this.lore = "button." + btnName + ".focus";
    }

    @Override
    public void tick() {

    }

    @Override
    public BitmapProvider[] getBitmaps() {
        textPainter.getBitmaps()[0].getCurrentBitmap().setOffset(pos[0] + xTextOffset, pos[1]);
        return new BitmapProvider[]{bitmapStack, textPainter.getBitmaps()[0]};
    }

    public void setText(String text) {
        this.text = text;
        textPainter.writeOnBitmap(text);
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean shouldRender() {
        return enabled;
    }

    public IActionRectangle getActionListener() {
        return actionListener;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled)
            actionListener.onFocusLost();
    }

    public boolean isEnabled() {
        return enabled;
    }
}
