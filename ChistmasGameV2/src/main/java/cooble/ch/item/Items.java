package cooble.ch.item;

import cooble.ch.inventory.item.Item;
import cooble.ch.logger.Log;
import cooble.ch.world.CItems;

/**
 * Created by Matej on 1.10.2016.
 */
public class Items {

    public static Item itemBattery = new ItemBattery(0);
    public static Item itemMail = new ItemMail(1);
    public static Item itemPot = new ItemPot(2);
    public static Item itemScrewdriver = new ItemScrewDriver(3);
    public static Item itemBook = new ItemBook(4);
    public static Item itemBluePrint = new ItemBlueprint(5);
    public static Item itemLux = new ItemLux(6);
    public static Item itemSoldier = new ItemSoldier(7);
    public static Item itemToothbrush = new ItemToothBrush(8);
    public static Item itemSoldierBrush = new ItemSoldierToothbrush(9);
    public static Item itemKey = new ItemKey(10);
    public static Item itemCap = new ItemCap(11);
    public static Item itemFan = new ItemFan(12);
    public static Item itemBigBattery = new ItemBigBattery(13);
    public static Item itemElectronics = new ItemElectronics(14);
    public static Item itemQuadracopter = new Item(15, "quadracopter", "item/quadracopter_item");

    public static void load(CItems items) {
        Log.println("ITEMS loaded");


        items.saveItem(itemBattery, itemMail, itemPot, itemScrewdriver, itemBook, itemBluePrint, itemLux, itemSoldier, itemToothbrush, itemSoldierBrush, itemKey, itemCap, itemFan, itemBigBattery, itemElectronics, itemQuadracopter);
    }
}
