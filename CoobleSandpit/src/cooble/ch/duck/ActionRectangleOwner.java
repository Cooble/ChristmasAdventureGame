package cooble.ch.duck;

import cooble.ch.canvas.Bitmap;
import cooble.ch.fx.Controller;

import java.awt.*;

/**
 * Created by Matej on 21.5.2017.
 */
public abstract class ActionRectangleOwner implements ListViewItem {
    private int x,y,width,height;
    protected Bitmap actionBitmap;
    protected boolean actionBitmapActive;
    private static final Color selectColor = new Color(54, 0, 255, 100);
    protected String id;

    public ActionRectangleOwner(){
        actionBitmap=Bitmap.create(10,10,selectColor);
    }

    public void setActionDimensions(int width, int height) {
        this.width = width * Controller.RATIO;
        this.height = height * Controller.RATIO;
        refreshActionBitmap();
    }

    public void setActionOffset(int x, int y) {
        this.x = x * Controller.RATIO;
        this.y = y * Controller.RATIO;
        actionBitmap.setOffset(this.x, this.y);
    }
    protected void refreshActionBitmap() {
        actionBitmap = Bitmap.create(width, height, selectColor);
        actionBitmap.setOffset(x, y);
        actionBitmap.setShouldRender(actionBitmapActive);
    }


    @Override
    public void onSelected() {
        if (actionBitmapActive) {
            actionBitmap.setShouldRender(true);
        }
    }
    @Override
    public void onDeselected() {
        actionBitmap.setShouldRender(false);
    }

    public int getHeight() {
        return height / Controller.RATIO;
    }

    public int getY() {
        return y / Controller.RATIO;
    }

    public int getX() {
        return x / Controller.RATIO;
    }

    public int getWidth() {
        return width / Controller.RATIO;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setActionBitmapActive(boolean actionBitmapActive) {
        this.actionBitmapActive = actionBitmapActive;
        actionBitmap.setShouldRender(actionBitmapActive);

    }
    public abstract int getBitmapOffsetX();
    public abstract int getBitmapOffsetY();
    public abstract void setBitmapOffset(int x,int y);
    public abstract void setToCome(int x, int y);
    public abstract int getxToCome();


    public abstract int getyToCome();

    public abstract  void setIsToCome(boolean isToCome);

    public abstract boolean isToCome();
}
