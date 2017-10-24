package cs.cooble.inventory.stuff;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import cs.cooble.core.Game;
import cs.cooble.event.*;
import cs.cooble.graphics.Animation;
import cs.cooble.graphics.BitmapProvider;
import cs.cooble.graphics.BitmapStack;
import cs.cooble.inventory.item.ItemStack;
import cs.cooble.resources.StringStream;
import cs.cooble.translate.Translator;
import cs.cooble.window.Tickable;
import cs.cooble.world.*;

import java.awt.*;
import java.util.function.Supplier;

/**
 * Created by Matej on 14.12.2015.
 * Thing on which can be clicked on
 * has actionrectangle
 * tickable
 * can react to clicking
 * can have bitmapStack
 * can be saved with nbt
 */
public class Stuff implements Tickable, NBTSaveable, Supplier<IActionRectangle> {

    private BitmapProvider locationTexture;
    private final String name;
    private IActionRectangle innerActionListener;
    protected IRectangle rectangle;
    private boolean markDeath;
    private String textName;
    private boolean death;
    protected Supplier<String> lore;

    private Animation animation;

    private boolean isItemVisible = true;
    private static final Rectangle BLANK_RECTANGLE = new Rectangle(0, 0, 0, 0);

    public Stuff(String name) {
        this.name = name;
        markDeath = false;
        setTextNameAsName();
        setLore(new StringStream(StringStream.getArray(getFullName() + ".comment.")));
        setRectangle(0, 0, 0, 0);
        setAction(new IAction() {});
    }

    public final void setLocationTexture(BitmapProvider locationTexture) {
        this.locationTexture = locationTexture;
        if (locationTexture instanceof Animation) {
            animation = (Animation) locationTexture;
        } else animation = null;
    }

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void setRectangle(int x, int y, int width, int height) {
        final Rectangle rect = new Rectangle(x, y, width, height);
        rectangle = new IRectangle() {
            @Override
            public Rectangle getRectangle() {
                return rect;
            }
        };
    }

    /**
     * if not consumed onClicked() (means returned false)
     *  -> called SpeakEvent
     *  else nothing
     * @param actionListener
     */
    public final void setAction(@NotNull IAction actionListener) {
        innerActionListener = new MouseFocusGainIActionListenerWrapper(new IActionRectangle() {
            @Override
            public Rectangle getRectangle() {
                if (isItemVisible) {
                    return rectangle.getRectangle();
                } else return BLANK_RECTANGLE;
            }

            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!isItemVisible)
                    return false;
                if (!actionListener.onClicked(x, y, right_button, released, item)) {
                    if (lore != null&&released) {
                        SpeakEvent event = new SpeakEvent(lore.get());
                        Game.core.EVENT_BUS.addEvent(event);
                    }
                }
                return true;
            }

            @Override
            public void mouseMove(int x, int y) {
                if (isItemVisible)
                    actionListener.mouseMove(x, y);
            }

            @Override
            public void clickElsewhere() {
                if (isItemVisible)
                    actionListener.clickElsewhere();
            }

            @Override
            public void onFocusGiven() {
                if (!isItemVisible)
                    return;
                if (textName != null && !Game.dialog.isBusy()) {
                    Game.dialog.setText(textName);
                }
                actionListener.onFocusGiven();
            }

            @Override
            public void onFocusLost() {
                if (!isItemVisible)
                    return;
                if (textName != null) {
                    if (textName.equals(Game.dialog.getFirstLineText()) && !Game.dialog.isBusy()) {
                        Game.dialog.setText(null);
                    }
                }
                actionListener.onFocusLost();
            }
        });
    }

    public final void setTextName(String textName) {
        this.textName = textName;
    }

    public final void setTextNameAsName() {
        this.textName = Translator.translate(getFullName() + ".name");
    }

    public final void setLore(@Nullable Supplier<String> lore) {
        this.lore = lore;
    }

    public final boolean isItemVisible() {
        return isItemVisible;
    }

    public final void setVisible(boolean b) {
        isItemVisible = b;
        if (animation != null)
            animation.setShouldRender(b);
        else if(locationTexture!=null){
            if(locationTexture instanceof BitmapStack){
                ((BitmapStack)(locationTexture)).setCurrentIndex(isItemVisible?0:-1);
            }
            else
                locationTexture.getCurrentBitmap().setShouldRender(isItemVisible);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public void tick() {
        if (animation != null)
            animation.tick();
    }

    public BitmapProvider getBitmapProvider() {
        return locationTexture;
    }

    public IActionRectangle getActionListener() {
        return innerActionListener;
    }

    /**
     * used to remove item from location
     * after calling this method, next tick item will be removed from location
     * next game load item wont be loaded
     */
    public final void markDeath() {
        this.markDeath = true;
    }

    public final boolean isMarkedDeath() {
        return markDeath;
    }

    public final void setDeath() {
        setVisible(false);
        death = true;
    }
    public boolean isDeath() {
        return death;
    }

    @Override
    public void readFromNBT(NBT nbt) {
        markDeath = nbt.getBoolean("markedDeath", false);
        isItemVisible = nbt.getBoolean("visible", true);
    }

    @Override
    public void writeToNBT(NBT nbt) {
        nbt.putBoolean("markedDeath", markDeath);
        nbt.putBoolean("visible", isItemVisible);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    public final String getFullName() {
        return name;
    }

    public String getOnlyName() {
        return name;
    }

    @Override
    public IActionRectangle get() {
        return innerActionListener;
    }
}
