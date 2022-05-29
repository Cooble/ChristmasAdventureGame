package cooble.ch.location;

import cooble.ch.core.Game;
import cooble.ch.event.SpeakEvent;
import cooble.ch.inventory.item.ItemStack;
import cooble.ch.inventory.stuff.Stuff;
import cooble.ch.inventory.stuff.StuffToCome;
import cooble.ch.item.Items;
import cooble.ch.world.IActionRectNoRect;
import cooble.ch.world.Location;
import cooble.ch.world.NBT;

/**
 * Created by Matej on 5.2.2017.
 */
public class LocationKumbal extends Location {
    private StuffToCome lux;
    private int batteries;

    public LocationKumbal() {
        super("kumbal");
    }

    @Override
    public void loadTextures() {

        Stuff robot = getStuffByID("robot");
        robot.setAction(new IActionRectNoRect() {
            @Override
            public boolean onClicked(int x, int y, boolean right_button, boolean released, ItemStack item) {
                if (!released)
                    return true;
                if (batteries != 0) {
                    Game.getWorld().inventory().addItem(new ItemStack(Items.itemBattery));
                    batteries--;
                }else Game.core.EVENT_BUS.addEvent(new SpeakEvent("entity.joe.nomoreBat"));
                return true;
            }
        });
        StuffToCome soldier = (StuffToCome) getStuffByID("soldier");
        soldier.setItem(new ItemStack(Items.itemSoldier));

        lux = (StuffToCome) getStuffByID("lux");
        lux.setOnPickedUp(() -> {
            Game.getWorld().inventory().addItem(new ItemStack(Items.itemLux));
            lux.setVisible(false);
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (Game.getWorld().getModule().getNBT().getBoolean("putLuxBack")) {
            lux.setVisible(true);
            Game.getWorld().getModule().getNBT().putBoolean("putLuxBack", false);
        }
    }

    @Override
    public void readFromNBT(NBT nbt) {
        super.readFromNBT(nbt);
        batteries = nbt.getInteger("batteries", 4);
    }

    @Override
    public void writeToNBT(NBT nbt) {
        super.writeToNBT(nbt);
        nbt.putInteger("batteries", batteries);
    }
}
