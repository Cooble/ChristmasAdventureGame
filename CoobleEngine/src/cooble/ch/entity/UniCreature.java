package cooble.ch.entity;


import cooble.ch.graphics.BitmapStack;
import cooble.ch.world.NBT;

import java.util.Random;

/**
 * Created by Matej on 11.12.2015.
 * This handles moving of creature -> changing images like creature is moving
 */
public class UniCreature extends Creature implements IMovable, Talkable {


    protected final int[] walkRight = new int[]{5, 3, 2, 3, 5, 1, 0, 1, 5};
    /**
     * Index of currently used pose/bitmapStack from poses - walking
     */
    protected int walkIndex;
    protected int[] walkLeft;
    protected int[] talkRight = new int[]{4, 5};
    protected int[] talkLeft;
    /*private boolean iwalkLeft;
    private boolean iwalkRight;
    private boolean iwalkUp;
    private boolean iwalkDown;*/


    protected boolean talkPos;

    protected short pictureAmount;

    protected BitmapStack bitmapStack;

    protected boolean right = true;
    protected boolean isWalking = false;
    protected boolean isTalking = false;
    protected int goMaxDelay = 8;
    protected int goDelay;
    protected int talkMaxDelay;
    protected int talkDelay;
    protected boolean noAnimate;

    private Random random = new Random();

    public UniCreature() {

    }

    public BitmapStack getBitmapStack() {
        return bitmapStack;
    }

    @Override
    public void tick() {
        if (!noAnimate)
            animateTick();
        bitmapStack.setOffset((int) posX, (int) posY+7);

    }

    private void animateTick() {
        if (right) {
            if (isWalking) {
                goDelay++;
                if (goDelay > goMaxDelay) {
                    goDelay = 0;
                    walkIndex++;
                    if (walkIndex > walkRight.length - 1)
                        walkIndex = 0;
                    bitmapStack.setCurrentIndex(walkRight[walkIndex]);
                }
            } else {
                bitmapStack.setCurrentIndex(talkPos ? 4 : 5);//default right position if!move
            }
        } else {
            if (isWalking) {
                goDelay++;
                if (goDelay > goMaxDelay) {
                    goDelay = 0;
                    walkIndex++;
                    if (walkIndex > walkRight.length - 1)
                        walkIndex = 0;
                    bitmapStack.setCurrentIndex(walkLeft[walkIndex]);
                }
            } else {
                bitmapStack.setCurrentIndex((talkPos ? 4 : 5) + pictureAmount);//default left position if!move
            }

        }
        if (isTalking) {
            talkDelay++;
            if (talkDelay > talkMaxDelay) {
                talkDelay = 0;
                talkMaxDelay = random.nextInt(20) + 3;
                talkPos = !talkPos;
                /*if(right)
                    bitmapStack.setCurrentIndex(talkRight[talkPos?0:1]);
                else
                    bitmapStack.setCurrentIndex(talkLeft[talkPos?0:1]);*/

            }
        } else {
            talkPos = false;
        }
    }

    @Override
    public int getX() {
        return (int) posX + bitmapStack.getWidth()/2;
    }

    @Override
    public int getY() {
        return (int) posY + bitmapStack.getHeight();
    }

    @Override
    public void setX(int x) {
        x -= bitmapStack.getWidth()/2;
        if (x != posX)
            right = x > posX;
        this.posX = x;
    }

    @Override
    public void setY(int y) {
        y -= bitmapStack.getHeight();
        this.posY = y;
    }

    /**
     * will be updated after next tick()
     * @param x
     * @param y
     */
    @Override
    public void setPos(int x, int y) {
        setX(x);
        setY(y);
    }

    @Override
    public void setIsMoving(boolean isMoving) {
        this.isWalking = isMoving;
    }

    @Override
    public void setIsTalking(boolean isTalking) {
        this.isTalking = isTalking;
    }

    /**
     * very important that set right/left must be done after setting position
     * @param right
     */
    public void setRight(boolean right) {
        this.right = right;
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        right = nbt.getBoolean("right");
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("right", right);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
    }

    /**
     * speed inversely proportional
     * @param speed
     */
    public void setAnimationSpeed(int speed) {
        this.goMaxDelay = speed;
    }

    public void setBitmapStack(BitmapStack bitmapStack) {
        this.bitmapStack = bitmapStack;
    }
}
