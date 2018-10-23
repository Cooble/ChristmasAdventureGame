package mc.cooble.duck;

import mc.cooble.canvas.Bitmap;
import mc.cooble.canvas.BitmapProvider;
import mc.cooble.fx.Controller;

/**
 * Created by Matej on 25.5.2017.
 */
public class Joe implements BitmapProvider {
    private Bitmap joeBitmap;
    private Bitmap joeBitmapLeft;
    private int posX,posY;

    private boolean right;
    private boolean enable;
    private boolean freeze;


    public Joe(){
        joeBitmap=Bitmap.load("C:/Users/Matej/Dropbox/Programming/Java/ChistmasGame/ChistmasGameV2/src/main/resources/mainGameResource/res\\textures\\character\\joe\\joe_1.png").scale(Controller.RATIO);
        joeBitmapLeft=joeBitmap.clone().flip();
        joeBitmapLeft.setShouldRender(false);
    }
    @Override
    public Bitmap[] getBufferedImages() {
        return new Bitmap[]{joeBitmap,joeBitmapLeft};
    }

    @Override
    public int getLevel() {
        return 0;
    }
    public void setRight(boolean right){
        this.right=right;
        refresh();
    }

    public void setPos(int x,int y){
        this.posX=x*Controller.RATIO;
        this.posY=y*Controller.RATIO;
        joeBitmap.setOffset(this.posX-joeBitmap.getWidth()/2,this.posY-joeBitmap.getHeight());
        joeBitmapLeft.setOffset(joeBitmap.getOffsetX(),joeBitmap.getOffsetY());
    }

    public void setEnabled(boolean enable){
        this.enable = enable;
        refresh();
    }
    private void refresh(){
        if(enable){
            joeBitmap.setShouldRender(right);
            joeBitmapLeft.setShouldRender(!right);
        }
        else {
            joeBitmap.setShouldRender(false);
            joeBitmapLeft.setShouldRender(false);
        }
    }

    public int getPosX() {
        return posX/Controller.RATIO;
    }

    public int getPosY() {
        return posY/Controller.RATIO;
    }

    public boolean isEnabled() {
        return enable;
    }
    public void setFreeze(boolean freeze){
        this.freeze = freeze;
    }

    public boolean isFrozen() {
        return freeze;
    }

    @Override
    public String toString() {
        return "joe";
    }

    public boolean isRight() {
        return right;
    }
}
