package cs.cooble.world;

/**
 * Created by Matej on 22.7.2016.
 */
public class Settings implements NBTSaveable {
    public double songVolume=-1;
    public double soundVolume=-1;
    public double voiceVolume=-1;
    public String lang="nothing";
    public Boolean fullScreen=null;

    @Override
    public void readFromNBT(NBT nbt) {
        if (songVolume == -1)
            songVolume = nbt.getDouble("songVolume", 0.4);
        if (soundVolume == -1)
            soundVolume = nbt.getDouble("soundVolume", 1);
        if (voiceVolume == -1)
            voiceVolume = nbt.getDouble("voiceVolume", 1);
        if (lang.equals("nothing"))
            lang = nbt.getString("lang","en");
        if (fullScreen==null)
            fullScreen = nbt.getBoolean("fullScreen",false);
    }

    @Override
    public void writeToNBT(NBT nbt) {
        nbt.putDouble("songVolume", songVolume);
        nbt.putDouble("soundVolume", soundVolume);
        nbt.putDouble("voiceVolume", voiceVolume);
        nbt.putBoolean("fullScreen", fullScreen);
        nbt.putString("lang", lang);
    }

    @Override
    public boolean isDirty() {
        return false;
    }
}
