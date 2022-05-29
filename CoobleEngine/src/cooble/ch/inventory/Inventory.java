package cooble.ch.inventory;

import com.sun.istack.internal.Nullable;
import cooble.ch.core.Game;
import cooble.ch.event.MouseEventConsumer;
import cooble.ch.graphics.Bitmap;
import cooble.ch.graphics.BitmapProvider;
import cooble.ch.graphics.MultiBitmapProvider;
import cooble.ch.inventory.item.Item;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.music.MPlayer2;
import cooble.ch.window.Tickable;
import cooble.ch.world.NBT;
import cooble.ch.world.NBTSaveable;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Matej on 10.8.2016.
 */
public final class Inventory implements MultiBitmapProvider, Tickable, NBTSaveable, MouseEventConsumer {

    private final ItemContainer[] itemContainers;
    private final Rectangle[] rectangles;
    private Bitmap[] bitmaps;
    private int posX, posY;
    private final int CONTAINER_HAND = 3;
    private final int RECTANGLE_UP = 4;
    private final int RECTANGLE_DOWN = 5;
    private final int RECTANGLE_BIG = 0;
    private final ItemStack[] itemStacks;
    private int itemStacksNextFreeIndex;

    private boolean shouldBeRendered;
    private boolean isOpening, isClosing, hideSlots;

    private int scrollIndex;
    private boolean locked;
    private boolean enabled;

    public Inventory() {
        itemStacks = new ItemStack[20];
        itemContainers = new ItemContainer[4];
        posY += 4;
        for (int i = 0; i < 4; i++) {
            itemContainers[i] = new ItemContainer(i, posX + 10, posY + 22 + 24 * 2 * i);
        }
        bitmaps = new Bitmap[5];
        bitmaps[0] = Bitmap.get("gui/slots");
        loadSlotBitmaps();
        rectangles = new Rectangle[6];
        rectangles[0] = new Rectangle(0, 0, 21 * 2, 90 * 2);//default big actionRectangle
        for (int i = 1; i < 4; i++) {
            rectangles[i] = new Rectangle(2 * 2, 12 * 2 + (i - 1) * 2 * 24, 17 * 2, 17 * 2);//three itemSlots
        }
        rectangles[RECTANGLE_UP] = new Rectangle(1 * 2, 1 * 2, 19 * 2, 10 * 2);//up arrow
        rectangles[RECTANGLE_DOWN] = new Rectangle(1 * 2, 79 * 2, 19 * 2, 10 * 2);//down arrow

    }

    private void setPosition(int x) {
        for (int i = 0; i < 3; i++) {
            getItemSlotContainer(i).setOffset(x + 6, null);
        }
        bitmaps[0].setOffset(x, bitmaps[0].getOffset()[1]);
        loadSlotBitmaps();
    }

    private int currentItemSlotIndexSelected;

    private void onItemSlotClick(int itemSlotIndex, boolean rightButton) {
        ItemStack itemInHand = getItemInHandContainer().getItemStack();
        ItemContainer slot = getItemSlotContainer(itemSlotIndex);
        ItemStack itemInSlot = slot.getItemStack();

        if (itemInHand != null) {// i have item in hand
            if (!rightButton) {//left button
                if (itemInSlot != null) {//click with item on item
                    ItemStack output = itemInHand.onRightClickOnItem(itemInSlot);
                    if (output != null) {
                        slot.setItemStack(output);
                        getItemInHandContainer().setItemStack(null);
                        saveFromContainers();
                    }
                } else {//click with item to empty slot
                    slot.setItemStack(itemInHand);//add itemInHand to container
                    getItemInHandContainer().setItemStack(null);
                    saveFromContainers();
                }

            } else {//right button
                if (itemInSlot != null) {//click with item on item
                    slot.setItemStack(getItemInHandContainer().getItemStack());//exchange
                    getItemInHandContainer().setItemStack(itemInSlot);
                    saveFromContainers();

                }
            }
        } else {//i do not have item in hand
            if (itemInSlot != null) {//click with no item on item
                if (!rightButton) {
                    getItemInHandContainer().setItemStack(itemInSlot);//get item to hand
                    slot.setItemStack(null);
                    saveFromContainers();
                    removeNullsFromItems();
                    loadToContainers();

                } else {//right buttton
                    currentItemSlotIndexSelected = itemSlotIndex;
                    itemInSlot.onClickedRightOnThis();
                }
            }
        }
        loadSlotBitmaps();
    }

    private void removeNullsFromItems() {
        ArrayList<ItemStack> out = new ArrayList<>(itemStacks.length);
        for (ItemStack itemStack : itemStacks) {
            if (itemStack != null)
                out.add(itemStack);
        }
        for (int i = 0; i < itemStacks.length; i++) {
            if (i < out.size()) {
                itemStacks[i] = out.get(i);
            } else
                itemStacks[i] = null;
        }
        itemStacksNextFreeIndex = out.size();
    }

    private void reloadItemSlots(int newScrollIndex) {
        saveFromContainers();
        scrollIndex = newScrollIndex;
        loadToContainers();
        loadSlotBitmaps();

    }

    private void saveFromContainers() {
        for (int i = 0; i < 3; i++) {
            ItemContainer container = getItemSlotContainer(i);
            int index = scrollIndex + i;
            if (index < itemStacks.length)
                itemStacks[i + scrollIndex] = container.getItemStack();

        }
    }

    private void loadToContainers() {
        for (int i = 0; i < 3; i++) {
            ItemContainer container = getItemSlotContainer(i);
            int index = scrollIndex + i;
            if (index < itemStacks.length)
                container.setItemStack(itemStacks[index]);
        }
    }

    private void scrollUp() {
        if (scrollIndex != 0) {
            MPlayer2.playSound("cvak_0");
            reloadItemSlots(scrollIndex - 1);
        }
    }

    private void scrollDown() {
        if (scrollIndex != itemStacks.length - 1&&shouldScrollDown()) {
            MPlayer2.playSound("cvak_0");
            reloadItemSlots(scrollIndex + 1);
        }
    }

    private boolean shouldScrollDown() {
        int lastItemstackIndex = 0;
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack itemStack = itemStacks[i];
            if (itemStack != null)
                lastItemstackIndex = i;
        }
        return scrollIndex<=lastItemstackIndex-1;

    }

    private ItemContainer getItemInHandContainer() {
        return itemContainers[CONTAINER_HAND];
    }

    private ItemContainer getItemSlotContainer(int index) {
        return itemContainers[index];
    }

    private void loadSlotBitmaps() {
        for (int i = 0; i < 4; i++) {
            bitmaps[i + 1] = itemContainers[i].getCurrentBitmap();
        }
    }

    @Override
    public BitmapProvider[] getBitmaps() {
        return bitmaps;
    }

    @Override
    public boolean shouldRender() {
        return shouldBeRendered;
    }

    private int openSpeed = 2;

    @Override
    public void tick() {
        if (isOpening) {
            shouldBeRendered = true;
            posX += openSpeed;
            setPosition(posX);
            if (posX >= 0) {
                posX = 0;
                setPosition(posX);
                isOpening = false;
            }
        } else if (isClosing) {
            posX -= openSpeed;
            setPosition(posX);
            if (posX <= -21 * 2) {
                putItemInHandBack();
                posX = -21 * 2;
                setPosition(posX);
                isClosing = false;
                shouldBeRendered = false;
                // putItemInHandBack();
            }
        }

    }

    public void setOn() {
        if (!locked) {
            enabled = true;
            isOpening = true;
            isClosing = false;
        }
    }

    public void setOff() {
        if (!locked) {
            enabled = false;
            isClosing = true;
            isOpening = false;
        }
    }

    public void setOnImmediately() {
        shouldBeRendered = true;
        posX = 0;
        setPosition(posX);
        enabled = true;
    }

    public void setOffImmediately() {
        if (enabled) {
            putItemInHandBack();
            posX = -21 * 2;
            setPosition(posX);
            shouldBeRendered = false;
            enabled = false;
        }
    }

    public void setOnOrOff() {
        if (locked)
            return;
        enabled = !enabled;
        if (enabled) {
            setOn();
        } else setOff();
    }

    private void hideSlots(boolean hide) {
        hideSlots = hide;
        if (hideSlots) {
            posX = -21 * 2;
            setPosition(posX);
        } else {
            posX = 0;
            setPosition(posX);
        }
    }

    @Override
    public void readFromNBT(NBT nbt) {
        int size = nbt.getInteger("inventorySize");
        for (int i = 0; i < size; i++) {
            ItemStack itemStack = new ItemStack(null);
            itemStack.readFromNBT(nbt.getNBT("item_" + i));
            itemStacks[i] = itemStack;
        }
        loadToContainers();
        loadSlotBitmaps();
    }

    @Override
    public void writeToNBT(NBT nbt) {
        if (getItemInHand() != null) {
            putItemInHandBack();
        }
        removeNullsFromItems();
        int i;
        for (i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] != null)
                itemStacks[i].writeToNBT(nbt.getNBT("item_" + i));
            else {
                break;
            }
        }
        nbt.putInteger("inventorySize", i);


    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean consume(int x, int y, int state, boolean released) {
        if (!shouldBeRendered)
            return false;

        int wheelScroll = x;

        x /= Game.renderer.PIXEL_SIZE;
        y /= Game.renderer.PIXEL_SIZE;


        if (state == MOUSE_MOVED || state == MOUSE_DRAGGED) {
            return move(x, y);
        }
        if (hideSlots)
            return false;
        if (state == WHEEL_SCROLL) {
            if (wheelScroll > 0)
                scrollUp();
            else scrollDown();
            return true;
        }
        if (!released)
            return rectangles[0].contains(x, y);

        if (rectangles[0].contains(x, y)) {
            if (rectangles[RECTANGLE_UP].contains(x, y) && state == MouseEventConsumer.CLICKED_LEFT) {
                scrollUp();

            } else if (rectangles[RECTANGLE_DOWN].contains(x, y) && state == MouseEventConsumer.CLICKED_LEFT) {
                scrollDown();
            } else {
                for (int i = 0; i < 3; i++) {
                    if (rectangles[i + 1].contains(x, y)) {
                        onItemSlotClick(i, state == MouseEventConsumer.CLICKED_RIGHT);
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean[] textts = new boolean[3];

    public boolean move(int x, int y) {
        if (getItemInHand() != null) {
            if (!hideSlots) {
                if (x > 50) {
                    hideSlots(true);
                }
            } else {
                if (x < 1) {
                    hideSlots(false);
                }
            }
        } else {
            if (hideSlots)
                hideSlots(false);
        }
        //  hideSlots(false);

        getItemInHandContainer().setOffset(x - 17 * 2 / 2, y - 17 * 2 / 2);

        for (int i = 0; i < 3; i++) {
            boolean contain = rectangles[i + 1].contains(x, y);
            boolean lastContain = textts[i];
            textts[i] = contain;
            if (contain && !lastContain) {
                ItemStack itemStack = getItemSlotContainer(i).getItemStack();
                if (itemStack != null)
                    Game.dialog.setText(itemStack.ITEM.getTextName());
            } else if (!contain && lastContain) {
                Game.dialog.setText(null);

            }
        }
        return rectangles[0].contains(x, y);
    }

    public ItemStack getItemInHand() {
        return getItemInHandContainer().getItemStack();
    }

    public void setItemInHand(@Nullable ItemStack itemStack) {
        getItemInHandContainer().setItemStack(itemStack);
        loadSlotBitmaps();
        hideSlots(false);
    }

    public void addItem(ItemStack itemStack) {
        addItem(itemStack, true);
    }

    public void addItem(ItemStack itemStack, boolean popSound) {
        if (itemStack == null)
            return;
        if (popSound)
            MPlayer2.playSound("pop");
        saveFromContainers();
        removeNullsFromItems();
        itemStacks[itemStacksNextFreeIndex] = itemStack;
        itemStacksNextFreeIndex++;
        loadToContainers();
        loadSlotBitmaps();
    }

    public boolean isOpen() {
        return shouldBeRendered;
    }

    public void lock(boolean b) {
        this.locked = b;
    }

    public void putItemInHandBack() {
        if (getItemInHand() == null)
            return;
        addItem(getItemInHand(), false);
        getItemInHandContainer().setItemStack(null);
        loadSlotBitmaps();
        hideSlots(false);
    }

    /**
     * removes item on which was right clicked
     */
    public void removeSelected() {
        if (currentItemSlotIndexSelected < 3) {
            getItemSlotContainer(currentItemSlotIndexSelected).setItemStack(null);
        }

    }

    public boolean hasItem(Item item) {
        for (ItemStack stack : itemStacks) {
            if (stack != null) {
                if (stack.ITEM.ID == item.ID)
                    return true;
            }
        }
        return false;
    }

    public void stealItem(Item item) {
        for (int i = 0; i < itemStacks.length; i++) {
            ItemStack stack = itemStacks[i];
            if (stack != null) {
                if (stack.ITEM.ID == item.ID) {
                    saveFromContainers();
                    itemStacks[i] = null;
                    loadToContainers();
                    return;
                }
            }
        }
    }


}
