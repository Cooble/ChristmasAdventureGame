package mc.cooble.world;

/**
 * foo
 */
public interface NBTSaveable {

    void readFromNBT(NBT nbt);

    void writeToNBT(NBT nbt);

    @Deprecated
    boolean isDirty();//todo remove isDirtyMethod

}
