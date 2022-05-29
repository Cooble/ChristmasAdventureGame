package mc.cooble.module;

import mc.cooble.logger.Log;
import mc.cooble.world.CModules;
import mc.cooble.world.LocModule;

/**
 * Created by Matej on 1.10.2016.
 */
public class Modules {
    public static final LocModule moduleIntro = new ModuleIntro();
    public static final LocModule moduleParty = new ModuleParty();

    public static void load(CModules modules){
        Log.println("MODULES loaded");

        modules.setModulesSize(2);
        modules.register(moduleIntro);
        modules.register(moduleParty);
    }
}
