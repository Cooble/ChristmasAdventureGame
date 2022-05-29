package cooble.ch.entity;


import cooble.ch.window.Tickable;
import cooble.ch.world.NBT;
import cooble.ch.world.NBTSaveable;

/**
 * Simple creature which has some position, is tickable, and has possibility to load textures
 */
public class Creature implements Tickable, NBTSaveable, TextureLoadable {

    protected double posX, posY;
    protected double speed;

    public void setPos(int x, int y) {
        posX = x;
        posY = y;
    }

    @Override
    public void tick() {
    }

    public double getSpeed() {
        return speed;
    }

    @Override
    public void readFromNBT(NBT nbt) {
        posX = nbt.getDouble("posX");
        posY = nbt.getDouble("posY");
        speed = nbt.getDouble("speed");
    }

    @Override
    public void writeToNBT(NBT nbt) {
        nbt.putDouble("posX", posX);
        nbt.putDouble("posY", posY);
        nbt.putDouble("speed", speed);
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void loadTextures() {

    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
