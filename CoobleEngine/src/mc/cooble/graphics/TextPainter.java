package mc.cooble.graphics;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import mc.cooble.font.FontUtil;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

/**
 * Class which takes care of rendering a string in font on bitmapStack
 * handles one line texts as well as poly-line
 */
public class TextPainter implements MultiBitmapProvider {
    private static final Color whiteBlank = new Color(255,255,255,100);
    private static final Color blackBlank = new Color(0,0,0,120);

    private Bitmap[] bitmap = new Bitmap[1];
    private Font font;
    private boolean shouldDraw;

    public TextPainter(int pixelWidth, int pixelHeight, Font font) {
        this.font = font;
        bitmap[0] = Bitmap.create(pixelWidth, pixelHeight);
    }


    /**
     * called on change in rows
     */
    public void writeOnBitmap(@Nullable String[] rows) {
        shouldDraw = rows != null;
        if (!shouldDraw) {
            return;
        }
        Graphics g = null;
        try {
            g = bitmap[0].getImage().getGraphics();
        } catch (SlickException e) {
            e.printStackTrace();
        }
        g.clear();
        g.setColor(mc.cooble.core.Game.renderer.isShadowHandled()?whiteBlank:blackBlank);
        g.fillRect(mc.cooble.core.Game.getWorld().inventory().isOpen() ? 20*2 : 0,bitmap[0].getHeight() - 7 - rows.length * 9,bitmap[0].getWidth(),bitmap[0].getHeight());
        g.setColor(!mc.cooble.core.Game.renderer.isShadowHandled()?Color.white:Color.black);
        g.setFont(font);
        for (int i = 0; i < rows.length; i++) {
            if (rows[i] == null)
                break;
            g.drawString(FontUtil.translate(rows[i]), mc.cooble.core.Game.getWorld().inventory().isOpen() ? 22*2 : 2*2, bitmap[0].getHeight() - 15 - i * 9);
        }
        g.flush();
    }

    public void writeOnBitmap(@NotNull String s) {
        writeOnBitmap(s, Color.black);
    }

    public void writeOnBitmap(@NotNull String s, Color color) {
        this.writeOnBitmap(s,color,0,0);
    }
    public void writeOnBitmap(@NotNull String s, Color color,int posX,int posY) {
        bitmap[0].setOffset(posX,posY);
        try {
            Graphics g = bitmap[0].getImage().getGraphics();
            g.clear();
            g.setColor(color);
            g.setFont(font);
            if(s!=null) {
                g.drawString(FontUtil.translate(s), 0, 2);
            }
            g.flush();
        } catch (SlickException e) {
            e.printStackTrace();
        }

    }

    @Override
    public BitmapProvider[] getBitmaps() {
        return bitmap;
    }

    @Override
    public boolean shouldRender() {
        return shouldDraw;
    }

    public Font getFont() {
        return font;
    }
}
