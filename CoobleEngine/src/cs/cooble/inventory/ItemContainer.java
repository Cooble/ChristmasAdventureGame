package cs.cooble.inventory;

import com.sun.istack.internal.Nullable;
import cs.cooble.graphics.Bitmap;
import cs.cooble.graphics.BitmapProvider;
import cs.cooble.inventory.item.ItemStack;


/**
 * Created by Matej on 9.8.2016.
 */
class ItemContainer implements BitmapProvider {
    private ItemStack itemStack;
    private Bitmap bitmap;
    private int[] offset;
    private int ID;

    public ItemContainer(int ID, int posX, int posY) {
        this(ID, posX, posY, null);
    }

    public ItemContainer(int ID, int posX, int posY, ItemStack itemStack) {
        this.ID = ID;
        this.itemStack = itemStack;
        offset = new int[]{posX, posY};
    }

    public int getID() {
        return ID;
    }

    public void setItemStack(@Nullable ItemStack itemStack) {
        this.itemStack = itemStack;
        refreshBitmap();
    }

    public void refreshBitmap() {
        if (itemStack != null) {
            bitmap = Bitmap.get(itemStack.ITEM.getTextureName());
            bitmap.setOffset(offset[0], offset[1]);
        } else bitmap = null;
    }

    @Nullable
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public Bitmap getCurrentBitmap() {
        return bitmap;
    }

    public void setOffset(int[] offset) {
        this.offset = offset;
        if(bitmap!=null)
            bitmap.setOffset(offset[0],offset[1]);
    }

    public void setOffset(Integer posX, Integer posY) {
        if (posX != null)
            offset[0] = posX;
        if (posY != null)
            offset[1] = posY;
        if(bitmap!=null)
            bitmap.setOffset(offset[0],offset[1]);

    }

    @Override
    public int[] getOffset() {
        return offset;
    }

    @Override
    public boolean shouldRender() {
        return itemStack != null && itemStack.getPocet() != 0;
    }
}
