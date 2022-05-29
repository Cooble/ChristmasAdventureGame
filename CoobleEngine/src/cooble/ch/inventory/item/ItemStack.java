package cooble.ch.inventory.item;


import cooble.ch.core.Game;
import cooble.ch.entity.Creature;
import cooble.ch.inventory.stuff.Stuff;
import cooble.ch.world.NBT;
import cooble.ch.world.NBTSaveable;
import cooble.ch.world.World;

/**
 * Created by Matej on 7.3.2015.
 */
public class ItemStack implements NBTSaveable {
    public Item ITEM;
    private int pocet;
    private NBT nbt;

    public ItemStack(Item item) {
        this(item, 1);
    }

    public ItemStack(Item item, int pocet) {
        ITEM = item;
        this.pocet = pocet;
        nbt=new NBT();
    }

    public int getPocet() {
        return pocet;
    }

    public ItemStack addPocet(int added) {
        pocet += added;
        return this;
    }

    public ItemStack setPocet(int pocet) {
        this.pocet = pocet;
        return this;
    }

    public int getMaxPocet() {
        return ITEM.getMaxPocet();
    }

    public boolean isMax() {
        return ITEM.maxStackSize <= pocet;
    }

    @Override
    public String toString() {
        return ITEM.toString();
    }

    public ItemStack getCopy() {
        return new ItemStack(ITEM, pocet);
    }

    public NBT getNBT() {
        return nbt;
    }

    public void setNBT(NBT nbt) {
        this.nbt = nbt;
    }

    public void loadNBT(NBT nbt){
        setNBT(nbt);
        pocet = nbt.getInteger("pocet");
        Integer id = nbt.getInteger("itemID");
        if(id!=null)
            ITEM = Game.getWorld().items().getItem(id);
    }

    public void onItemUse(World world, Stuff stuff) {
        ITEM.readFromNBT(nbt.getNBT("ITEM"));
        ITEM.onItemUse(world, this, stuff);
        ITEM.writeToNBT(nbt.getNBT("ITEM"));
    }

    public void onItemUse(World world, Creature creature) {
        ITEM.readFromNBT(nbt.getNBT("ITEM"));
        ITEM.onItemUse(world, this, creature);
        ITEM.writeToNBT(nbt.getNBT("ITEM"));
    }

    /**
     * called whenever is item held in inventory (player hss clicked on it and has it in hand or this item is located on action tool slot)
     */
    public void onItemHeldInHand(World world) {
        ITEM.readFromNBT(nbt.getNBT("ITEM"));
        ITEM.onItemHeldInHand(world, this);
        ITEM.writeToNBT(nbt.getNBT("ITEM"));
    }

    /**
     * Called when was right clicked on some item in the inventory with this item.
     *
     * @return item which was created by assembling two items (items will be deleted)
     * null when nothing should happen
     */
    public ItemStack onRightClickOnItem(ItemStack someItem) {
        return this.ITEM.onRightClickOnItem(someItem, this);
    }

    /**
     * Called when was rightclicked on this item
     */
    public void onClickedRightOnThis(){
        this.ITEM.onClickedRightOnThis();
    }


    @Override
    public void readFromNBT(NBT nbt) {
        this.nbt=nbt;
        Integer id = nbt.getInteger("itemID");
        if(id!=null){
            ITEM= Game.getWorld().items().getItem(id);
            ITEM.readFromNBT(nbt.getNBT("ITEM"));
        }
    }

    @Override
    public void writeToNBT(NBT nbt) {
        nbt.putInteger("itemID",ITEM.ID);
        ITEM.writeToNBT(nbt.getNBT("ITEM"));


    }

    @Override
    public boolean isDirty() {
        return false;
    }
}
