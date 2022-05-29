package mc.cooble.event;

/**
 * Created by Matej on 2.10.2016.
 *
 * PreInitEvent set Saver,
 *                  Translator,
 *                  Renderer
 *                  load Settings (if exists)
 */
public interface PreInitEvent {
    void preInit();
}
