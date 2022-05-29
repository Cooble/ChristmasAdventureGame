package mc.cooble.world;


/**
 * doo
 */
public abstract class LocModule {
    private NBT nbt;
    public String MID;
    protected Location[] locations;

    public LocModule(String MID) {
        this.MID = MID;
    }

    public abstract Location[] load();

    public final NBT getNBT() {
        if(nbt==null)
            return nbt=new NBT();
        return nbt;
    }

    public void setNBT(NBT NBT) {
        this.nbt = NBT;
    }

    /**
     * totally clears state of loc module (called after everything successfully saved)
     */
    public void clear(){
        locations=null;
        nbt=null;
    }
}
