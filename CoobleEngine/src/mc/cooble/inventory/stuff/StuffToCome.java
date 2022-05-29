package mc.cooble.inventory.stuff;


import com.sun.istack.internal.Nullable;
import mc.cooble.core.Game;
import mc.cooble.entity.InteligentMover;
import mc.cooble.entity.Mover;
import mc.cooble.entity.Position;
import mc.cooble.event.EventConsumer;
import mc.cooble.event.SpeakEvent;
import mc.cooble.graphics.Bitmap;
import mc.cooble.graphics.BoolMap;
import mc.cooble.inventory.item.ItemStack;
import mc.cooble.logger.Log;
import mc.cooble.translate.Translator;
import mc.cooble.window.Tickable;
import mc.cooble.world.ActionRectangleManager;
import mc.cooble.world.IAction;
import mc.cooble.world.NBT;

/**
 * stuff which you can easily
 * pick up from ground/location to your inventory
 * or if its item is set to null -> then the joe will come to it with mover and comment it with speakEvent
 * <p>
 * Stuff which have to be set:
 * ===========================
 * #actionRectangle
 * #positionJoeToCome
 * #posibble Joe's speed
 * #possible runnable when joe picked up the item
 * #call packIt()
 * ===========================
 */
public class StuffToCome extends Stuff {

    private Position position;
    private Runnable onPickedUp;
    private EventConsumer consumerOnClick;
    private Tickable mover;
    private ItemStack itemik;

    private boolean hasBeenPickedUp;


    public StuffToCome(String name) {
        super(name);
    }

    public StuffToCome(String name, int x, int y, int width, int height, String bitmapPath, int offsetX, int offsetY, ItemStack itemStack, @Nullable EventConsumer onPickUp) {
        this(name, x, y, width, height, Bitmap.get(bitmapPath), offsetX, offsetY, itemStack, onPickUp);
    }

    public StuffToCome(String name, int x, int y, int width, int height, Bitmap bitmap, int offsetX, int offsetY, ItemStack itemStack, @Nullable EventConsumer onPickUp) {
        super(name);
        setRectangle(x, y, width, height);
        bitmap.setOffset(offsetX, offsetY);
        setLocationTexture(bitmap);
        setItem(itemStack);
        setConsumerOnClick(onPickUp);
    }

    public void setOnPickedUp(Runnable onPickedUp) {
        this.onPickedUp = onPickedUp;
    }

    public void setPositionToCome(Position position) {
        this.position = position;
    }

    public Position getPositionToCome() {
        return position;
    }

    public void setItem(@Nullable ItemStack item) {
        this.itemik = item;
    }

    /**
     * if consumption!=successful then runs pickable event(Joe will pick up this item)
     *
     * @param consumerOnClick
     */
    public final void setConsumerOnClick(EventConsumer consumerOnClick) {
        this.consumerOnClick = consumerOnClick;
    }


    /**
     * called after everything is set
     * #actionRectangle
     * #positionToCome
     *
     * @param actionRectangleManager
     */
    public void packIt(ActionRectangleManager actionRectangleManager, BoolMap map) {
        if (rectangle == null)
            Log.println("[DebugMode] rectangle is null", Log.LogType.ERROR);

        IAction actionListener = new IAction() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (consumerOnClick == null || !consumerOnClick.consumeEvent(x, y, right_button, released, item)) {
                    if (!released)
                        return true;
                    if (position != null) {
                        if (map == null)
                            mover = new Mover(position, this::pickUp);
                        else
                            mover = new InteligentMover(position, map, this::pickUp);
                    } else pickUp();
                }
                return true;
            }

            private void pickUp() {
                mover = null;
                if (itemik != null) {
                    markDeath();
                    onFocusLost();
                    Game.getWorld().inventory().addItem(itemik);
                    hasBeenPickedUp = true;
                    String onPickUpString = Translator.translate(getFullName() + ".pickup");
                    if (onPickUpString != null) {
                        SpeakEvent event = new SpeakEvent(getFullName() + ".pickup");
                        Game.core.EVENT_BUS.addEvent(event);
                    }
                } else if (lore != null) {
                    SpeakEvent event = new SpeakEvent(lore.get());
                    Game.core.EVENT_BUS.addEvent(event);
                }
                onPickedUp();
            }
        };
        setAction(actionListener);
        actionRectangleManager.register(this);
    }

    @Override
    public void tick() {
        super.tick();
        if (mover != null)
            mover.tick();
        if (hasBeenPickedUp)
            markDeath();
    }

    /**
     * called when the item was picked up
     */
    private void onPickedUp() {
        if (onPickedUp != null)
            onPickedUp.run();
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putBoolean("isPicked", hasBeenPickedUp);
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        hasBeenPickedUp = nbt.getBoolean("isPicked", false);
        if (hasBeenPickedUp)
            markDeath();
    }
}
