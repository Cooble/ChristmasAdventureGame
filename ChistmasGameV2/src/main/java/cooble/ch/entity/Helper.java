package cooble.ch.entity;

import cooble.ch.core.Game;
import cooble.ch.graphics.BitmapStack;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.translate.Translator;
import cooble.ch.world.IAction;
import cooble.ch.world.IActionRectangle;
import cooble.ch.world.MouseFocusGainIActionListenerWrapper;

import java.awt.*;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by Matej on 25.7.2017.
 */
public class Helper extends UniCreature {

    private String name;
    private boolean dance;
    private int ticksToChangePosture;
    private int[] danceMap = new int[]{4, 3, 2, 3, 4, 1, 0, 1, 4};
    private int currentDanceIndex;
    private IActionRectangle rectangle;
    private Supplier<String> nameSupplier;
    private String lastString;
    private IAction action;

    private Rectangle rec;


    public Helper(String name) {
        this.name = name;
        ticksToChangePosture=goMaxDelay;
        nameSupplier=new Supplier<String>() {
            @Override
            public String get() {
                return Translator.translate(name);
            }
        };
    }

    public IActionRectangle getActionRectangle() {
        return rectangle;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        BitmapStack bitmapStackRight = BitmapStack.getBitmapStack("character/" + name + "/joe_6");

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
        for (int i = 0; i < walkRight.length; i++) {
            danceMap[walkLeft.length - 1 + i] = walkRight[i];
        }

        rectangle=new MouseFocusGainIActionListenerWrapper(new IActionRectangle() {
            @Override
            public Rectangle getRectangle() {
                if(rec!=null)
                    return new Rectangle(bitmapStack.getOffset()[0],bitmapStack.getOffset()[1],rec.width,rec.height);
                return new Rectangle(bitmapStack.getOffset()[0],bitmapStack.getOffset()[1],bitmapStack.getWidth(),bitmapStack.getHeight());
            }

            @Override
            public void onFocusGiven() {
                lastString = nameSupplier.get();
                if(!Game.dialog.isBusy()&&lastString!=null) {
                    Game.dialog.setText(lastString);
                }
            }

            @Override
            public void onFocusLost() {
                if(Objects.equals(Game.dialog.getFirstLineText(),lastString)){
                    Game.dialog.setText(null);
                }
            }

            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if(action==null)
                    return false;
                return action.onClicked(x,y,right_button,released,item);
            }
        });
    }

    public void setIsDancing(boolean dance) {
        noAnimate=dance;
        this.dance = dance;
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

    public void setNameSupplier(Supplier<String> nameSupplier) {
        this.nameSupplier = nameSupplier;
    }

    public void setAction(IAction action) {
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public void setRectangle(int x,int y,int width,int height) {
        rec=new Rectangle(x,y,width,height);
    }

    public void setBitmapStack(BitmapStack stack){
        this.bitmapStack=stack;
    }
}
