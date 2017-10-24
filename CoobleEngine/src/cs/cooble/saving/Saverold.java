package cs.cooble.saving;

import com.sun.istack.internal.Nullable;
import cs.cooble.core.Game;
import cs.cooble.logger.Log;
import cs.cooble.world.NBT;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


/**
 * Trida, zajistujici vsechen kontakt s ukládáním a nacitanim cehokoliv.
 */
public class Saverold {

    private String world_name = "/kokoWorld/";

    private final String APPDATA = System.getenv("APPDATA");
    public final String GAME_PATH;
    private final String WORLDS_PATH;
    private final String SETTINGS;
    public final String XML_PATH;
    private String res;
    public String DIALOG_PATH;


    public Saverold(String gameName) {
        GAME_PATH = APPDATA + "/" + gameName+ "/";
        WORLDS_PATH = GAME_PATH + "worlds/";
        XML_PATH = GAME_PATH + "xml/";
        DIALOG_PATH  =GAME_PATH+"dialog/";
        SETTINGS = GAME_PATH + "SETTINGS.txt";
        res = GAME_PATH + "res/";
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

    //=PSANÍ A CTENÍ SOUBORÙ============================================================================================
    public Object readObject(String file) throws IOException, ClassNotFoundException {
        File file1 = new File(file);
        Object object = null;

        FileInputStream fileStream = new FileInputStream(file1);
        ObjectInputStream objectStream = new ObjectInputStream(fileStream);

        object = objectStream.readObject();


        return object;
    }

    private void writeObject(String file, Object object) {
        File file1 = new File(file);
        try {
            FileOutputStream fileStream = new FileOutputStream(file1);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

            objectStream.writeObject(object);

            objectStream.close();
            fileStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=VYTVÁØENÍ SLOŽEK A SOUBORU=======================================================================================
    private void createFile(String cesta) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(cesta, "UTF-8");
            writer.close();

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates folder if doesn't exist.
     *
     * @param file
     * @return true if file was created
     */
    private boolean createFolder(File file) {
        if (!file.exists()) {
            file.mkdirs();
            return true;
        } else return false;
    }

    public void copyDirectory(File sourceLocation, File targetLocation) throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public boolean deleteDirectory(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return false;
        }
        //t       odo dodelat mazani directories nefunguje pouziva to jiny proces ale jaky nevim

        String[] files = dir.list();
        for (int i = files.length - 1; i >= 0; i--) {
            File f = new File(dir, files[i]);
            if (f.isDirectory()) {
                Log.println(deleteDirectory(f) ? "smazano dir " + f : "nelze smazat dir " + f);
            } else {
                if (!f.delete()) {
                    //System.out.println("*Nelze smazat soubor " + f + " ** canread " + f.canRead() + " canwirte " + f.canWrite() + " canexecute " + f.canExecute() + f.isAbsolute()+"isdir "+f.isDirectory());
                    try {
                        String out = f.toString();
                        out = out.replace('\\', '/');
                        Log.println("dir.to.path " + Paths.get(new URI("file:/" + out)));


                        Files.delete(Paths.get(new URI("file:/" + out)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else { /*System.out.println("Smazan soubor " + f);*/}
            }
        }
        return dir.delete();
    }

    public boolean deleteDirectory(String dir) {
        return deleteDirectory(new File(dir));
    }

    public boolean deleteFile(File file) {
        return file.delete();
    }


    //=EVENTY===========================================================================================================

    public void saveWorld(NBT world) {
        writeObject(getWORLD_NBT(), world);
    }

    public void makeDefaultFoldersFiles() {
        try {
            if (!new File(getWORLD_NBT()).exists()) {//all has to created
                createFolder(new File(GAME_PATH));
                createFolder(new File(WORLDS_PATH));
                createFolder(new File(getWORLD_PATH()));
                createFolder(new File(getWorldModules()));
                createFile(SETTINGS);
                NBT settingsNbt = new NBT();
                Game.readSettingsNBT(settingsNbt);
                writeObject(SETTINGS, settingsNbt);
            } else {
                Game.writeSettingsNBT((NBT) readObject(SETTINGS));
            }
        } catch (Exception ignored) {
        }
    }

    public void saveSettingsNBT(NBT settings) {
        writeObject(SETTINGS, settings);
    }

    public NBT loadSettingsNBT() {
        //System.err.println("settings fucked "+SETTINGS);
        try {
            return (NBT) readObject(SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public NBT loadWorld() {
        try {
            return (NBT) readObject(getWORLD_NBT());
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

    //=PRÁCE S INFEM====================================================================================================


    /*public void deleteSvet(String name) {
        try {
            deleteDirectory(worldsPath+name);
        }
        catch (Exception ignored){}

    }*/
    public void deleteSvet() {
        try {
            deleteDirectory(getWORLD_NBT());
        } catch (Exception ignored) {
        }

    }

    public void removeEverything() {
        deleteDirectory(GAME_PATH);
    }

    public NBT loadModuleNBT(String name) {
        name = getWorldModules() + "mod_" + name + ".dat";
        try {
            return (NBT) readObject(name);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveModuleNBT(NBT nbt, String moduleName) {
        moduleName = getWorldModules() + "mod_" + moduleName + ".dat";
        if (!new File(moduleName).exists())
            createFile(moduleName);
        writeObject(moduleName, nbt);
    }

    //SETTINGS==========================================================================================================

    public void clearGameFolder() {
        deleteDirectory(WORLDS_PATH);
    }


    public void makeTempFolder(int W, int H) {
        temporaries = new ArrayList<>();
        resizeImagesToTemp(new File(GAME_PATH + "/res/textures/shadow_src"), new File(GAME_PATH + "/res/textures/shadow"), W, H, true);
        resizeImagesToTemp(new File(GAME_PATH + "/res/textures/location_src"), new File(GAME_PATH + "/res/textures/location"), W, H, false);
        resizeImagesToTemp(new File(GAME_PATH + "/res/textures/bool_src"), new File(GAME_PATH + "/res/textures/bool"), W, H, false);
    }

    //Texture management
    private ArrayList<File> temporaries;

    private void resizeImagesToTemp(File from, File temp, int w, int h, boolean invert) {
        File locationTextures = from;
        temporaries.add(temp);
        if (!locationTextures.exists())
            return;
        try {
            createFolder(temp);
            copyDirectory(locationTextures, temp);
            resizeAllLocationTextures(temp, w, h, invert);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void resizeAllLocationTextures(File fileWithLocs, int w, int h, boolean invert) {
        File[] images = fileWithLocs.listFiles();
        for (File image : images) {
            if (!image.isFile())
                continue;
            try {
                BufferedImage img = ImageIO.read(image);
                deleteFile(image);
                img = resizeImage(img, w, h);
                if (invert)
                    img = invertImage(img);
                ImageIO.write(img, "png", image);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void removeTemp() {
        for (File temporary : temporaries) {
            deleteDirectory(temporary);
        }
    }

    private BufferedImage resizeImage(BufferedImage src, int W, int H) {
        BufferedImage out = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = out.createGraphics();
        graphics.drawImage(src, 0, 0, W, H, null);
        graphics.dispose();
        return out;
    }

    private BufferedImage invertImage(BufferedImage v) {
        BufferedImage b = new BufferedImage(v.getWidth(), v.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = b.getGraphics();
        g.drawImage(v, 0, 0, null);
        for (int i = 0; i < b.getWidth(); i++) {
            for (int j = 0; j < b.getHeight(); j++) {
                Color c = new Color(b.getRGB(i, j), true);
                c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255 - c.getAlpha());
                b.setRGB(i, j, c.getRGB());
            }
        }
        return b;
    }

    public void loadFont(File file) {
        try {
            GraphicsEnvironment ge =
                    GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, file));
        } catch (IOException | FontFormatException e) {
            //Handle exception
        }
    }

    public String getRes() {
        return res;
    }

    /**
     *
     * @param folder
     * @param name if null everything will return
     * @return all files in subdirectoies
     */
    public ArrayList<File> findFiles(File folder,@Nullable String name) {
        ArrayList<File> out  =new ArrayList<>();
        File[] files = folder.listFiles();
        if(files==null)
            return out;
        for(File file:files){
            if(file.isFile()){
                if(name!=null&&file.getName().equals(name)){
                    out.add(file);
                }
                else if(name==null)
                    out.add(file);
            }else{
                out.addAll(findFiles(file,name));
            }
        }
        return out;
    }
    /**
     *
     * @param folder
     * @param name if null everything will return
     * @return all files in subdirectoies
     */
    public ArrayList<File> findFiles(String folde,@Nullable String name) {
        File folder = new File(folde);
        ArrayList<File> out  =new ArrayList<>();
        File[] files = folder.listFiles();
        if(files==null)
            return out;
        for(File file:files){
            if(file.isFile()){
                if(name!=null&&file.getName().equals(name)){
                    out.add(file);
                }
                else if(name==null)
                    out.add(file);
            }else{
                out.addAll(findFiles(file.getAbsolutePath(),name));
            }
        }
        return out;
    }
}