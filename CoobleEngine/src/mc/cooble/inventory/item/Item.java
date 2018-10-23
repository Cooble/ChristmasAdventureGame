package mc.cooble.inventory.item;


import mc.cooble.entity.Creature;
import mc.cooble.inventory.stuff.Stuff;
import mc.cooble.translate.Translator;
import mc.cooble.world.NBT;
import mc.cooble.world.NBTSaveable;
import mc.cooble.world.World;

/**
 * Created by Matej on 14.2.2015.
 */
public class Item implements NBTSaveable {
    protected String name;
    protected String textName;
    protected String textureName;
    protected int maxStackSize;
    public final int ID;

    public Item(int ID, String name, String textureName, String textName) {
        this.ID = ID;
        this.name = name;
        this.textureName = textureName;
        maxStackSize = 1;
        this.textName = textName;
    }
    public Item(int ID, String name, String textureName) {
        this.ID = ID;
        setNameAndText(name);
        this.textureName = textureName;
        maxStackSize = 1;
    }
    public Item(int ID) {
        this.ID = ID;
        maxStackSize = 1;
    }

    /**
     * sets name like itemPot
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
        this.textName = Translator.translate("item."+name+".name");
    }
    public String getOnlyName(){
        return name;
    }

    /**
     * sets name and textName with Translate name into lang which will be displayed when focused with mouse on this item in inventory
     *
     * @param name
     */
    public void setNameAndText(String name) {
        this.name = name;
        this.textName = Translator.translate("item."+name+".name");
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }
    public String getTextName() {
        return textName;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    /**
     * Called when was right clicked on the item in the inventory with no itemInHand
     * Used for doing some action for example: opening book, reading letter.
     * @param someItem
     * @param thisItem
     */
    public ItemStack onRightClickOnItem(ItemStack someItem, ItemStack thisItem) {
        return null;
    }

    public String getFullName() {
        return "item."+name;
    }

    public int getMaxPocet() {
        return maxStackSize;
    }

    @Override
    public String toString() {
        return name;
    }

    public void onItemUse(World world, ItemStack itemStack, Stuff stuff) {
    }

    public void onItemUse(World world, ItemStack itemStack, Creature creature) {
    }

    /**
     * called whenever is item held in inventory (player hss clicked on it and has it in hand or this item is located on action tool slot)
     */
    public void onItemHeldInHand(World world, ItemStack itemStack) {
    }

    @Override
    public boolean equals(Object obj) {
        return ((obj instanceof Item) && ((Item) obj).getFullName().equals(getFullName()));
    }

    public String getTextureName() {
        return textureName;
    }

    @Override
    public void readFromNBT(NBT nbt) {}

    @Override
    public void writeToNBT(NBT nbt) {}

    @Override
    public boolean isDirty() {
        return false;
    }

    /**
     * Called when was rightclicked on this item
     */
    public void onClickedRightOnThis() {}
}
