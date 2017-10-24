package cs.cooble.entity;


import cs.cooble.core.Game;
import cs.cooble.graphics.Bitmap;

/**
 * Created by Matej on 18.7.2016.
 */
public class ButtonNormal extends Button {
    public ButtonNormal(int posX, int posY, Runnable clicked) {
        super(posX, posY, 55*2, 13*2, Bitmap.get("gui/button"),Bitmap.get("gui/button_on"), Game.font, clicked);
    }
}
