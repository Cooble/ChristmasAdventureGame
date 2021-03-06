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
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;


/**
 * Trida, zajistujici vsechen kontakt s ukl�d�n�m a nacitanim cehokoliv.
 */
public class Saver {

    private String world_name = "/world/";
    private final String mainGameRes = "mainGameResource/";

    private final String APPDATA = System.getenv("APPDATA");
    public final String GAME_PATH;
    private final String WORLDS_PATH;
    private final String SETTINGS;
    public final String XML_PATH;
    public final String FONT_PATH;
    public final String TEXTURE_PATH;
    private String res;
    public String DIALOG_PATH;
    public final String DICTIONARY_PATH;

    private Class injectedResClass;

    public void setInjectedResClass(Class injectedResClass) {
        this.injectedResClass = injectedResClass;
    }

    public Saver(String gameName) {
        GAME_PATH = APPDATA + "/" + gameName + "/";
        WORLDS_PATH = GAME_PATH + "worlds/";
        XML_PATH = mainGameRes + "/xml/";
        FONT_PATH = mainGameRes + "/font/";
        DIALOG_PATH = mainGameRes + "/dialog/";
        DICTIONARY_PATH = mainGameRes + "/dictionary/";
        SETTINGS = GAME_PATH + "SETTINGS.txt";
        res = mainGameRes + "/res/";
        TEXTURE_PATH = res + "/textures/";

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

    //=PSAN� A CTEN� SOUBOR�============================================================================================
    public Object readObject(String file) throws IOException, ClassNotFoundException {
        Object object = null;

        // if (file.contains(":")) {//check if resources or external
        File file1 = new File(file);

        FileInputStream fileStream = new FileInputStream(file1);
        ObjectInputStream objectStream = new ObjectInputStream(fileStream);

        object = objectStream.readObject();
        //  } else {
        //    object = new ObjectInputStream(getResourceAsStream(file));
        // }

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

    //=VYTV��EN� SLO�EK A SOUBORU=======================================================================================
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

    public void copyDirectory(String sourceLocation, File targetLocation) throws IOException {

        if (sourceLocation.endsWith("/")) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            List<String> children = findResourceC(sourceLocation, null);
            for (String child : children) {
                String fullChild = child;
                if (child.endsWith("/"))//is folder?
                    child = child.substring(0, child.length() - 1);
                if (child.contains("/")) {
                    child = child.substring(child.lastIndexOf("/"));
                    copyDirectory(fullChild, new File(targetLocation, child));
                }
            }
        } else {
            //  System.out.println("Src loc: " + sourceLocation.getCanonicalPath());
            InputStream in = getResourceAsStream(sourceLocation);
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
                deleteDirectory(f);
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
            ignored.printStackTrace();
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

    //=PR�CE S INFEM====================================================================================================


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
        deleteDirectory(GAME_PATH);
    }


    public void makeTempFolder(int W, int H) {
        temporaries = new ArrayList<>();
        if (!new File(GAME_PATH + "/res/textures/").exists()) {
            //resizeImagesToTemp(getRes() + "/textures/shadow_src/", new File(GAME_PATH + "/res/textures/shadow"), W, H, true);
            //  resizeImagesToTemp(getRes() + "/textures/location_src/", new File(GAME_PATH + "/res/textures/location"), W, H, false);
            //  resizeImagesToTemp(getRes() + "/textures/bool_src/", new File(GAME_PATH + "/res/textures/bool"), W, H, false);
        }
    }

    //Texture management
    private ArrayList<File> temporaries;

    private void resizeImagesToTemp(String from, File temp, int w, int h, boolean invert) {
        temporaries.add(temp);
        // if (!from.exists())
        //     return;
        try {
            createFolder(temp);
            copyDirectory(from, temp);
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

    public String getRes() {
        return res;
    }

    /**
     * search in resources
     *
     * @param folde
     * @param name  if null everything will return
     * @return all files in subdirectoies
     */
    public ArrayList<File> findFiles(String folde, @Nullable String name) {
        File folder = getResource(folde);
        ArrayList<File> out = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null)
            return out;
        for (File file : files) {
            if (file.isFile()) {
                if (name != null && file.getName().equals(name)) {
                    out.add(file);
                } else if (name == null)
                    out.add(file);
            } else {
                out.addAll(findFiles(file.getAbsolutePath(), name));
            }
        }
        return out;
    }

    public String makeResString(String path) {
        if (path.contains(mainGameRes)) {
            while (path.contains(mainGameRes)) {
                path = path.substring(1);
            }
            path = mainGameRes.substring(0, 1) + path;
        }
        path = path.replace("\\", "/");
        path = path.replace("//", "/");
        path = path.replace("//", "/");
        path = path.replace("//", "/");
        if (path.startsWith("/"))
            path = path.substring(1);
        return path;
    }

    public File getResource(String path) {
        return getResource(path, true);
    }

    public File getResource(String path, boolean showException) {
        path = makeResString(path);
        URL url = getContextClassLoader().getResource(path);
        File file = null;
        try {
            file = new File(url.getFile());
            if (!file.exists())
                throw new Exception();
        } catch (Exception e) {
            if (showException) {
                new Exception("Cannot load: " + path).printStackTrace();
                Log.println("Cannot load resource: " + path, Log.LogType.ERROR);
            }
        }
        return file;
    }

    public InputStream getResourceAsStream(String path, boolean showException) {
        path = makeResString(path);
        final String oldPath = path;
        InputStream out = null;

        final InputStream in = getContextClassLoader().getResourceAsStream(path);
        if (in != null) {
            return in;
        } else {
            out = getClass().getResourceAsStream(path);
            if (out == null) {
                if (path.startsWith("/"))
                    path = path.substring(1);
                else path = "/" + path;
                out = getClass().getResourceAsStream(path);
                if (out == null)
                    out = getContextClassLoader().getResourceAsStream(path);

                if (showException && out == null)
                    new Exception("Cannot load resource as stream: " + oldPath).printStackTrace();
            }
        }
        return out;

    }

    public InputStream getResourceAsStream(String path) {
        return getResourceAsStream(path, true);
    }

    public ClassLoader getContextClassLoader() {
        // return Thread.currentThread().getContextClassLoader();
        if (injectedResClass != null) {
            return injectedResClass.getClassLoader();
        }
        return Saver.class.getClassLoader();
    }

    private FileSystem fileSystem;

    /**
     * @param path
     * @param name
     * @return absolute path relative to jar
     */
    public List<String> findResourceC(String path, @Nullable String name) {
        path = makeResString(path);

        try {
            URI uri = getContextClassLoader().getResource(path).toURI();
            ArrayList<String> out = new ArrayList<>();
            Path myPath;
            if (uri.getScheme().equals("jar")) {
                try {
                    fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                } catch (Exception ingored) {
                }
                myPath = fileSystem.getPath(path);
            } else {
                myPath = Paths.get(uri);
            }
            Stream<Path> walk = Files.walk(myPath, 50);
            for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                String pathik = it.next().toString();
                if (pathik.equals(path))
                    continue;
                if (pathik.endsWith("/")) {
                    if (pathik.startsWith("/"))
                        pathik = pathik.substring(1);
                    out.addAll(findResourceC(pathik, name));

                } else {
                    if (name != null) {
                        if (pathik.endsWith(name))
                            out.add(pathik);
                    } else
                        out.add(pathik);
                }
            }
            ArrayList<String> real = new ArrayList<>();
            for (String anOut : out)
                if (!anOut.replace("\\", "/").endsWith(path)&&!anOut.replace("\\","/").concat("/").endsWith(path))
                    if (!anOut.replace("\\", "/").concat("/").endsWith(path))//or use uri as reference to prevent same folder in which is searched for subfolders listed as a subfolder
                        real.add(anOut.replace("\\", "/"));


            return real;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean existPath(String path) {
        InputStream i = getResourceAsStream(path, false);
        return i != null;

    }
}