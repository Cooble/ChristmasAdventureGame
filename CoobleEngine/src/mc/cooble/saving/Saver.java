package mc.cooble.saving;

import mc.cooble.world.NBT;

import java.io.File;
import java.io.InputStream;


/**
 * Trida, zajistujici vsechen kontakt s ukládáním a nacitanim cehokoliv.
 */
public final class Saver {

    private String world_name = "/world/";
    private final String mainGameRes = "mainGameResource/";

    private final String APPDATA = System.getenv("APPDATA");
    public final String GAME_PATH;
    private final String WORLDS_PATH;
    private final String SETTINGS;
    public final String XML_PATH;
    public final String FONT_PATH;
    public final String TEXTURE_PATH;
    public final String SOUND_PATH;
    public String DIALOG_PATH;
    public final String DICTIONARY_PATH;
    private final SaverIO saver;

    public Saver(String gameName, Class injectedResClass) {
        GAME_PATH = APPDATA + "/" + gameName + "/";
        WORLDS_PATH = GAME_PATH + "worlds/";
        XML_PATH = mainGameRes + "/xml/";
        FONT_PATH = mainGameRes + "/font/";
        DIALOG_PATH = mainGameRes + "/dialog/";
        DICTIONARY_PATH = mainGameRes + "/dictionary/";
        SETTINGS = GAME_PATH + "SETTINGS.txt";
        TEXTURE_PATH = mainGameRes + "/textures/";
        SOUND_PATH = mainGameRes + "/sounds/";
        saver = new SaverIO(injectedResClass,mainGameRes);
    }

    public String getWORLD_NBT() {
        return getWORLD_PATH() + "WORLD.dat";
    }

    public String getWorldModules() {
        return getWORLD_PATH() + "modules/";
    }

    public String getWORLD_PATH() {
        return WORLDS_PATH + world_name;
    }

    public void saveWorld(NBT world) {
        saver.writeNBT(getWORLD_NBT(), world);
    }

    /**
     * @return true if fresh creation
     */
    public boolean makeDefaultFoldersFiles() {
        try {
            if (!new File(getWORLD_NBT()).exists()) {//all has to created
                saver.createFolder(new File(GAME_PATH));
                saver.createFolder(new File(WORLDS_PATH));
                saver.createFolder(new File(getWORLD_PATH()));
                saver.createFolder(new File(getWorldModules()));
                saver.createFile(new File(SETTINGS));
                return true;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return false;
    }

    public void saveSettingsNBT(NBT settings) {
        saver.writeNBT(SETTINGS, settings);
    }

    public NBT loadSettingsNBT() {
        return saver.readNBT(SETTINGS);
    }

    public NBT loadWorld() {
        return saver.readNBT(getWORLD_NBT());
    }

    public NBT loadModuleNBT(String name) {
        name = getWorldModules() + "mod_" + name + ".dat";
        NBT out = saver.readNBT(name);
        if(out==null)
            return new NBT();
        return out;
    }

    public void saveModuleNBT(NBT nbt, String moduleName) {
        moduleName = getWorldModules() + "mod_" + moduleName + ".dat";
        saver.writeNBT(moduleName, nbt);
    }

    //SETTINGS==========================================================================================================

    public void clearGameFolder() {
        saver.deleteDirectory(new File(GAME_PATH));
    }

    public InputStream loadFont(String fontName) {
        return saver.getResourceAsStream(FONT_PATH + fontName);
    }

    public SaverIO getIO() {
        return saver;
    }
}