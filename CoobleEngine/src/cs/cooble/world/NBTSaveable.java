package cs.cooble.world;

/**
 * foo
 */
public interface NBTSaveable {

    void readFromNBT(NBT nbt);

    void writeToNBT(NBT nbt);

    boolean isDirty();

}
