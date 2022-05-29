package cooble.ch.entity;

import com.sun.istack.internal.Nullable;
import cooble.ch.core.Game;
import cooble.ch.event.Event;
import cooble.ch.event.LocationLoadEvent;
import cooble.ch.graphics.BitmapProvider;
import cooble.ch.graphics.BitmapStack;
import cooble.ch.graphics.BoolMap;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.resources.ResourceStackBuilder;
import cooble.ch.window.Tickable;
import cooble.ch.world.IActionRectangle;
import cooble.ch.world.MouseFocusGainIActionListenerWrapper;

import java.awt.*;

/**
 * Class of arrow
 * <p>
 * used to render the arrow in Location
 * needs its ActionListener to be registered
 * needs to be registered to Renderer
 * <p>
 * automatic showing of arrow whenever mouse is located in given rectangle
 * <p>
 * when clicked the specified runnable object is called
 */
public class Arrow implements Tickable {

    private static BitmapStack bitmapStack0 = BitmapStack.getBitmapStack(ResourceStackBuilder.buildResourcesStack("gui/arrow/arrow", "", "0", "1"));
    private static BitmapStack bitmapStack1 = bitmapStack0.rotateBitmaps(90);
    private static BitmapStack bitmapStack2 = bitmapStack0.rotateBitmaps(180);
    private static BitmapStack bitmapStack3 = bitmapStack0.rotateBitmaps(270);

    private static BitmapStack sbitmapStack0 = BitmapStack.getBitmapStack(ResourceStackBuilder.buildResourcesStack("gui/arrow/small_arrow", "", "0", "1"));
    private static BitmapStack sbitmapStack1 = sbitmapStack0.rotateBitmaps(90);
    private static BitmapStack sbitmapStack2 = sbitmapStack0.rotateBitmaps(180);
    private static BitmapStack sbitmapStack3 = sbitmapStack0.rotateBitmaps(270);

    public static final byte LEFT = 0;
    public static final byte UP = 1;
    public static final byte RIGHT = 2;
    public static final byte DOWN = 3;

    private BitmapStack bitmapStack;
    private Rectangle rectangle;
    private MouseFocusGainIActionListenerWrapper actionListenerWrapper;
    private boolean enabled;
    private Tickable mover;
    private String name;
    private Runnable onClicked;
    private Runnable onExit;
    private int joeX, joeY;
    private int joeFinalX, joeFinalY;
    private boolean joeFinalRight;
    private String location;
    private byte pos;
    private boolean big;
    private Event onExitEvent;
    private Event onClickedEvent;

    private boolean noBitmap;

    public Arrow(String id) {
        this.name = id;
        bitmapStack = createBitmapStack(true, Arrow.UP);//default arrow bitmapStack
        bitmapStack.setCurrentIndex(-1);
        enabled = true;
        actionListenerWrapper = new MouseFocusGainIActionListenerWrapper(new IActionRectangle() {
            @Override
            public Rectangle getRectangle() {
                return rectangle;
            }

            @Override
            public void onFocusGiven() {
                if (enabled && !noBitmap) {
                    bitmapStack.setCurrentIndex(0);
                }
            }

            @Override
            public void onFocusLost() {
                if (!noBitmap)
                    bitmapStack.setCurrentIndex(-1);
            }

            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!right_button && item == null && enabled && !noBitmap) {//show second bitmapStack the green one selection color you know
                    bitmapStack.setCurrentIndex(released ? 1 : 0);
                }
                if (!released || right_button || item != null || !enabled)
                    return true;
                if (onClicked != null)
                    onClicked.run();
                if (onClickedEvent != null) {
                    Game.core.EVENT_BUS.addEvent(onClickedEvent);
                } else {//havent specified special onclicked event -> normal mover stuff

                    Runnable runnable = () -> {
                        if (onExit != null)
                            onExit.run();
                        if (onExitEvent != null)
                            Game.core.EVENT_BUS.addEvent(onExitEvent);
                        else if (location != null) {//has not specified special case on locationLoading
                            LocationLoadEvent event = new LocationLoadEvent(location);
                            event.setJoeRightFacing(joeFinalRight);
                            event.setJoesLocation(joeFinalX, joeFinalY);
                            Game.core.EVENT_BUS.addEvent(event);
                        }
                        mover = null;
                    };
                    BoolMap boolMap = Game.getWorld().getLocationManager().getCurrentLocation().getBoolMap();
                    if (boolMap != null)
                        mover = new InteligentMover(new Position(joeX, joeY), boolMap, runnable);
                }
                return true;
            }
        });
    }

    public static byte toByte(String string) {
        string = string.toUpperCase();
        switch (string) {
            case "UP":
                return Arrow.UP;
            case "DOWN":
                return Arrow.DOWN;
            case "LEFT":
                return Arrow.LEFT;
            case "RIGHT":
                return Arrow.RIGHT;
        }
        return -1;
    }

    public static Arrow createArrowWithMover(String name, byte pos, boolean big) {
        switch (pos) {
            case Arrow.LEFT:
                return new ArrowLeft(name, big);
            case Arrow.RIGHT:
                return new ArrowRight(name, big);
            case Arrow.UP:
                return new ArrowUp(name, big);
            case Arrow.DOWN:
                return new ArrowDown(name, big);
        }
        return null;
    }


    //INSTANCE METHODS
    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActionRectangle(int x, int y, int width, int height) {
        rectangle = new Rectangle(x, y, width, height);
    }

    public void setBitmapLocation(int x, int y) {
        bitmapStack.setOffset(x, y);
    }

    public void setJoeLocationToCome(int x, int y) {
        this.joeX = x;
        this.joeY = y;
    }

    public void setJoeFinalLocation(int x, int y) {
        this.joeFinalX = x;
        this.joeFinalY = y;
    }

    public void setJoeFinalRight(boolean joeFinalRight) {
        this.joeFinalRight = joeFinalRight;
    }

    public void setBig(boolean big) {
        setBigPos(big, pos);
    }

    public void setPos(byte pos) {
        setBigPos(big, pos);
    }

    public void setBigPos(boolean big, byte pos) {
        this.big = big;
        this.pos = pos;
        int currentIndex = bitmapStack.getCurrentIndex();
        bitmapStack = createBitmapStack(big, pos);
        bitmapStack.setCurrentIndex(currentIndex);
    }

    public IActionRectangle getActionListener() {
        return actionListenerWrapper;
    }

    public BitmapProvider getBitmapProvider() {
        return bitmapStack;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled && !noBitmap)
            bitmapStack.setCurrentIndex(-1);

    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    /**
     * @param event to be scheduled to event_bus when joe comes on of location
     *              if event is null -> default location loading will be scheduled
     */
    public void setOnExitEvent(@Nullable Event event) {
        this.onExitEvent = event;
    }

    /**
     * @param event to be scheduled to event_bus when player has clicked on arrow
     *              if event is null -> normal mover stuff
     */
    public void setOnClickedEvent(@Nullable Event event) {
        this.onClickedEvent = event;
    }

    /**
     * called when joe comes out of location
     *
     * @param onExit
     */
    public void setOnExit(@Nullable Runnable onExit) {
        this.onExit = onExit;
    }

    /**
     * @param onClicked called when player has clicked on arrow
     */
    public void setOnClicked(@Nullable Runnable onClicked) {
        this.onClicked = onClicked;
    }

    @Override
    public void tick() {
        if (mover != null)
            mover.tick();
    }

    public void setNoBitmap(boolean noBitmap) {
        this.noBitmap = noBitmap;
        bitmapStack.setCurrentIndex(-1);
    }

    public boolean hasNoBitmap() {
        return noBitmap;
    }

    //HELP METHODS

    private static BitmapStack createBitmapStack(boolean big, byte pos) {
        BitmapStack out = null;
        if (big) {
            switch (pos) {
                case LEFT:
                    out = new BitmapStack(bitmapStack0);
                    break;
                case UP:
                    out = new BitmapStack(bitmapStack1);
                    break;
                case RIGHT:
                    out = new BitmapStack(bitmapStack2);
                    break;
                case DOWN:
                    out = new BitmapStack(bitmapStack3);
                    break;
            }
        } else {
            switch (pos) {
                case LEFT:
                    out = new BitmapStack(sbitmapStack0);
                    break;
                case UP:
                    out = new BitmapStack(sbitmapStack1);
                    break;
                case RIGHT:
                    out = new BitmapStack(sbitmapStack2);
                    break;
                case DOWN:
                    out = new BitmapStack(sbitmapStack3);
                    break;
            }
        }
        return out;

    }

}
