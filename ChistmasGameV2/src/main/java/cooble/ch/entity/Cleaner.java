package cooble.ch.entity;

import cooble.ch.graphics.BitmapStack;

/**
 * Created by Matej on 27.7.2017.
 */
public class Cleaner extends Helper {
    private boolean cleanActive;
    private BitmapStack cleaningStack;
    private int currentCleanIndex;
    private final int cleanSpeed =10;
    private int cleanDelay =10;
    private boolean up=true;
    private int stackOffset;
    public Cleaner(String name) {
        super(name);
    }

    @Override
    public void tick() {
        super.tick();
        if(cleanActive){
            cleanDelay++;
            if(cleanDelay==cleanSpeed){
                cleanDelay=0;
                if(up) {
                    currentCleanIndex++;
                    if (currentCleanIndex >= 2) {
                        up=false;
                    }
                }
                else{
                    currentCleanIndex--;
                    if(currentCleanIndex<=0){
                        up=true;
                    }
                }
                super.getBitmapStack().setCurrentIndex(stackOffset+(right?currentCleanIndex:(currentCleanIndex+3)));
            }
        }
        noAnimate=cleanActive;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        cleaningStack=BitmapStack.getBitmapStackFromFolder("character/cleanerCleaning/");
        cleaningStack=cleaningStack.addBitmapStack(cleaningStack.flipBitmaps(false));
        stackOffset=getBitmapStack().getMaxLength();
        bitmapStack=        getBitmapStack().addBitmapStack(cleaningStack);

    }

    @Override
    public BitmapStack getBitmapStack() {
        return super.getBitmapStack();
    }

    public void setCleanActive(boolean cleanActive) {
        this.cleanActive = cleanActive;
        cleanDelay=0;
    }
}
