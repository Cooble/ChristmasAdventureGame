package cs.cooble.saving;

import com.sun.istack.internal.Nullable;
import cs.cooble.logger.Log;
import cs.cooble.world.NBT;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;


/**
 * Class providing all contact with saving and loading
 */
public final class SaverIO {
    private Class injectedResClass;
    private final String mainGameRes;

    public SaverIO(String mainGameRes, Class injectedResClass) {
        this.mainGameRes = mainGameRes;
        this.injectedResClass = injectedResClass;
    }

    NBT readNBT(String file) {
        try {
            return (NBT) readObject(new File(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void writeNBT(String file, NBT nbt) {
        try {
            writeObject(new File(file), nbt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //=PSANÍ A CTENÍ SOUBORÙ============================================================================================
    Object readObject(File file) throws Exception {
        FileInputStream fileStream = new FileInputStream(file);
        ObjectInputStream objectStream = new ObjectInputStream(fileStream);
        return objectStream.readObject();
    }

    void writeObject(File file, Object object) {
        if(!file.exists())
            createFile(file);
        try {
            FileOutputStream fileStream = new FileOutputStream(file);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

            objectStream.writeObject(object);

            objectStream.close();
            fileStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //=VYTVÁØENÍ SLOŽEK A SOUBORU=======================================================================================
    void createFile(File file) {
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
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
    boolean createFolder(File file) {
        return !file.exists() && file.mkdirs();
    }

    void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        String src = sourceLocation.getAbsolutePath();
        if (src.endsWith("/")) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            List<String> children = findResource(src, null);
            for (String child : children) {
                String fullChild = child;
                if (child.endsWith("/"))//is folder?
                    child = child.substring(0, child.length() - 1);
                if (child.contains("/")) {
                    child = child.substring(child.lastIndexOf("/"));
                    copyDirectory(new File(fullChild), new File(targetLocation, child));
                }
            }
        } else {
            //  System.out.println("Src loc: " + sourceLocation.getCanonicalPath());
            InputStream in = getResourceAsStream(src);
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

    boolean deleteDirectory(File dir) {
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
                    try {
                        String out = f.toString();
                        out = out.replace('\\', '/');
                        Log.println("dir.to.path " + Paths.get(new URI("file:/" + out)));


                        Files.delete(Paths.get(new URI("file:/" + out)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return dir.delete();
    }

    boolean deleteFile(File file) {
        return file.delete();
    }

    //=EVENTY===========================================================================================================

    /**
     * search in resources
     *
     * @param folde
     * @param name  if null everything will return
     * @return all files in subdirectoies
     */
    ArrayList<File> findFiles(String folde, @Nullable String name) {
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

    String makeResString(String path) {
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

    File getResource(String path) {
        return getResource(path, true);
    }

    File getResource(String path, boolean showException) {
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

    InputStream getResourceAsStream(String path, boolean showException) {
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

    InputStream getResourceAsStream(String path) {
        return getResourceAsStream(path, true);
    }

    ClassLoader getContextClassLoader() {
        if (injectedResClass != null) {
            return injectedResClass.getClassLoader();
        }
        return SaverIO.class.getClassLoader();
    }

    FileSystem fileSystem;

    /**
     * @param path
     * @param name
     * @return absolute path relative to jar
     */
    List<String> findResource(String path, @Nullable String name) {
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
                    out.addAll(findResource(pathik, name));

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
                if (!anOut.replace("\\", "/").endsWith(path) && !anOut.replace("\\", "/").concat("/").endsWith(path))
                    if (!anOut.replace("\\", "/").concat("/").endsWith(path))//or use uri as reference to prevent same folder in which is searched for subfolders listed as a subfolder
                        real.add(anOut.replace("\\", "/"));


            return real;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    boolean existPath(String path) {
        InputStream i = getResourceAsStream(path, false);
        return i != null;

    }
}