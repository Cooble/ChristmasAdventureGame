package cs.cooble.entity;

import cs.cooble.core.Game;
import cs.cooble.entity.UniCreature;
import cs.cooble.graphics.BitmapStack;

/**
 * Created by Matej on 2.10.2016.
 */
public class Joe extends UniCreature {

    private boolean dance;
    private int ticksToChangePosture;
    private int currentDanceIndex;
    private int[] danceMap;
    private double speed;
    public Joe() {
        this.posY = 90;
        this.speed = Game.isDebugging?8:4;
        goMaxDelay=3;
        ticksToChangePosture=8;
        //BitmapStack bitmapStackRight = BitmapStack.getBitmapStack(ResourceStackBuilder.buildResourcesStack("character/joe/joe_", ".png", "0", "1", "2", "3", "4", "5"));
    }
    @Override
    public void loadTextures(){
        BitmapStack bitmapStackRight = BitmapStack.getBitmapStack("character/joe/joe_6");

        BitmapStack bitmapStackLeft = bitmapStackRight.flipBitmaps(false);
        bitmapStack = bitmapStackRight.addBitmapStack(bitmapStackLeft);
        pictureAmount = (short) (bitmapStackLeft.getMaxLength());

        walkLeft = new int[walkRight.length];
        for (int i = 0; i < walkRight.length; i++) {
            walkLeft[i] = walkRight[i] + pictureAmount;
        }

        talkLeft = new int[talkRight.length];
        for (int i = 0; i < talkRight.length; i++) {
            talkLeft[i] = talkRight[i] + pictureAmount;
        }
        danceMap = new int[walkLeft.length + walkRight.length];
        System.arraycopy(walkLeft, 0, danceMap, 0, walkLeft.length);
        System.arraycopy(walkRight, 0, danceMap, walkLeft.length - 1, walkRight.length);
    }
    public void setIsDancing(boolean dance) {
        this.dance = dance;
        noAnimate=dance;
    }
    @Override
    public void tick() {
        super.tick();
        if(dance) {
            ticksToChangePosture--;
            if (ticksToChangePosture == 0) {
                ticksToChangePosture = goMaxDelay;
                currentDanceIndex++;
                if (currentDanceIndex == danceMap.length)
                    currentDanceIndex = 0;
                bitmapStack.setCurrentIndex(danceMap[currentDanceIndex]);
            }
        }
    }
    public void setBitmapStack(BitmapStack stack){
        this.bitmapStack=stack;
    }

    @Override
    public double getSpeed() {
        return this.speed;
    }
}
