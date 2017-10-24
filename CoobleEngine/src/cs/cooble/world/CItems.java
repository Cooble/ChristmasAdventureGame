package cs.cooble.world;

import cs.cooble.inventory.item.Item;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Matej on 22.7.2016.
 */
public final class CItems {
    protected CItems(){
        items=new ArrayList<>();
    }

    private ArrayList<Item> items;

    /**
     * @param item
     * @return id which was used to save item
     */
    public void saveItem(Item... item) {
        Collections.addAll(items, item);
    }
    public void refreshLanguage(){
        for(Item item:items){
            item.setNameAndText(item.getOnlyName());
        }
    }

    public Item getItem(int ID) {
        return items.get(ID);
    }
}
