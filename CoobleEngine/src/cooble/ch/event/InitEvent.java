package cooble.ch.event;

/**
 * Created by Matej on 2.10.2016.
 *
 * InitEvent    gives the World NBT to process later in postInit
 *              gives Renderer main Paintable objects such as:  Inventory
 *                                                              TextManager/TextPainter
 *
 *
 */
public interface InitEvent {
    void init();
}
