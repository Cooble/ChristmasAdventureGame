package cooble.ch.duck;

import cooble.ch.canvas.Bitmap;
import cooble.ch.fx.Controller;

/**
 * Created by Matej on 18.5.2017.
 */
public class Arrow extends ActionRectangleOwner{
    String location;
    boolean noBitmap;
    boolean big;
    String pos;
    int imageX,imageY;
    private boolean FIXED_SIZE;
    private int xToCome,yToCome;
    private boolean isToCome;
    private int finalX,finalY;
    private Bitmap[] currentBitmap = new Bitmap[1];
    private static Bitmap bitmapStack0 = new Bitmap(Loc.SRC_FOLDER+"/textures\\gui\\arrow\\arrow0.png").scale(Controller.RATIO);
    private static Bitmap bitmapStack1 = bitmapStack0.clone().rotate(Math.PI/2);
    private static Bitmap bitmapStack2 = bitmapStack0.clone().rotate(Math.PI);
    private static Bitmap bitmapStack3 = bitmapStack0.clone().rotate(Math.PI * 3 / 2);

    private static Bitmap sbitmapStack0 = new Bitmap(Loc.SRC_FOLDER+"/textures\\gui\\arrow\\small_arrow0.png").scale(Controller.RATIO);
    private static Bitmap sbitmapStack1 = sbitmapStack0.clone().rotate(Math.PI / 2);
    private static Bitmap sbitmapStack2 = sbitmapStack0.clone().rotate(Math.PI);
    private static Bitmap sbitmapStack3 = sbitmapStack0.clone().rotate(Math.PI * 3 / 2);
    private boolean finalFace;


    public Arrow(){
        pos="LEFT";
        refreshBitmap();
    }



    @Override
    public Type getType() {
        return Type.ARROW;
    }

    @Override
    public void onSelected() {
        this.setActionBitmapActive(Controller.isArMode()&&!noBitmap);
        super.onSelected();
    }

    @Override
    public void onDeselected() {
        super.onDeselected();
        currentBitmap[0].setShouldRender(Controller.ARROWSHOW&&!noBitmap);
    }

    @Override
    public Bitmap[] getBufferedImages() {
        currentBitmap[0].setShouldRender(Controller.ARROWSHOW&&!noBitmap);
        return new Bitmap[]{currentBitmap[0],actionBitmap};
    }

    public void setPos(String pos) {
        this.pos = pos;
        refreshBitmap();
    }

    public String getPos() {
        return pos;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public void setBitmapOffset(int x,int y){
        imageX=x* Controller.RATIO;
        imageY=y*Controller.RATIO;
        refreshBitmap();
    }


    public int getBitmapOffsetX() {
        return imageX/Controller.RATIO;
    }

    public int getBitmapOffsetY() {
        return imageY/Controller.RATIO;
    }

    private String lastPos;
    private Boolean lastBig;
    private void refreshBitmap(){
        if(!pos.equals(lastPos) ||lastBig!=big) {
            if (big) {
                switch (pos) {
                    case "UP":
                        currentBitmap[0] = bitmapStack1.clone();
                        break;
                    case "DOWN":
                        currentBitmap[0] = bitmapStack3.clone();
                        break;
                    case "LEFT":
                        currentBitmap[0] = bitmapStack0.clone();
                        break;
                    default:
                        currentBitmap[0] = bitmapStack2.clone();
                        break;
                }
            } else {
                switch (pos) {
                    case "UP":
                        currentBitmap[0] = sbitmapStack1.clone();
                        break;
                    case "DOWN":
                        currentBitmap[0] = sbitmapStack3.clone();
                        break;
                    case "LEFT":
                        currentBitmap[0] = sbitmapStack0.clone();
                        break;
                    default:
                        currentBitmap[0] = sbitmapStack2.clone();
                        break;
                }

            }
        }
        lastBig=big;
        lastPos=pos;
        currentBitmap[0].setOffset(imageX,imageY);
        currentBitmap[0].setShouldRender(Controller.ARROWSHOW&&!noBitmap);
        if(!Controller.ARROWSHOW){
            setActionBitmapActive(false);
        }
    }

    public void setFIXED_SIZE(boolean FIXED_SIZE) {
        this.FIXED_SIZE = FIXED_SIZE;
    }

    public boolean isFIXED_SIZE() {
        return FIXED_SIZE;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setBig(Boolean big) {
        this.big = big;
    }

    public boolean isBig() {
        return big;
    }

    @Override
    public void setToCome(int x, int y) {
        xToCome=x*Controller.RATIO;
        yToCome=y*Controller.RATIO;
    }

    @Override
    public int getxToCome() {
        return xToCome/Controller.RATIO;
    }

    @Override
    public int getyToCome() {
        return yToCome/Controller.RATIO;
    }

    @Override
    public void setIsToCome(boolean isToCome) {
        this.isToCome=isToCome;
    }

    @Override
    public boolean isToCome() {
        return isToCome;
    }

    public int getFinalX() {
        return finalX/Controller.RATIO;
    }

    public int getFinalY() {
        return finalY/Controller.RATIO;
    }

    public void setFinal(int x,int y){
        finalX=x*Controller.RATIO;
        finalY=y*Controller.RATIO;
    }

    public boolean getFinalFace() {
        return finalFace;
    }

    public void setFinalRight(boolean finalFace) {
        this.finalFace = finalFace;
    }

    public void setNoBitmap(boolean noBitmap) {
        this.noBitmap = noBitmap;
    }
}
