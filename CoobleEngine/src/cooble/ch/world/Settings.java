package cooble.ch.world;

import com.sun.istack.internal.Nullable;

/**
 * Created by Matej on 22.7.2016.
 */
public abstract class Settings implements NBTSaveable {

    protected NBT nbt = new NBT();

    public void setAttribute(String name, @Nullable Object val) {
        if (val != null)
            nbt.putString(name, val.toString());
        else nbt.putString(name, "");
    }

    public Integer getInt(String name) {
        return Integer.parseInt(nbt.getString(name));
    }

    public Double getDouble(String name) {
        try {
            return Double.parseDouble(nbt.getString(name));
        }catch (Exception e){
          e.printStackTrace();
        }
        return null;
    }

    public String getString(String name) {
        return nbt.getString(name);
    }

    public Boolean getBoolean(String name) {
        return Boolean.parseBoolean(nbt.getString(name));
    }

    @Override
    public void readFromNBT(NBT nbt) {
        this.nbt = nbt.getNBT("settings");
    }

    @Override
    public void writeToNBT(NBT nbt) {
        nbt.putNBT("settings", this.nbt);
    }

    @Override
    public boolean isDirty() {
        return false;
    }
}
