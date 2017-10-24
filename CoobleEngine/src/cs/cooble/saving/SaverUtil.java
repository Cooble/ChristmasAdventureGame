package cs.cooble.saving;

/**
 * Created by Matej on 2.8.2017.
 */
public class SaverUtil {

    public static String getParent(String  path){
        if(path.endsWith("/")){
            path=path.substring(0,path.length()-1);
        }
        return path.substring(0,path.lastIndexOf('/')+1);
    }
    public static String getFileName(String  path){
        if(path.endsWith("/")){
            path=path.substring(0,path.length()-1);
        }
        return path.substring(path.lastIndexOf('/')+1);
    }

    /**
     *
     * @param name
     * @return suffix without "."
     */
    public static String getSuffix(String name){
        int index = name.lastIndexOf('.');
        int slashindex = name.lastIndexOf('/');
        if(index==-1||slashindex>index)
            return null;
        return removeSuffixDot(name.substring(index + 1));
    }

    /**
     * sets suffix of name (removes current one if necessary)
     * @param name
     * @param suffix
     * @return name+suffix
     */
    public static String setSuffix(String name,String suffix){
        suffix=removeSuffixDot(suffix);
        name=cleanPath(name);
        int index = name.lastIndexOf('.');
        int slashindex = name.lastIndexOf('/');
        if(index==-1||slashindex>index)
            return name+"."+suffix;//no suffix yet
        name=name.substring(0,index);
        return name+"."+suffix;
    }

    public static String cleanPath(String src){
        src=src.replace('\\','/');
        src=src.replace("//","/");
        src=src.replace("//","/");
        src=src.replace("..",".");
        return src;
    }
    public static String removeSuffixDot(String suffix){
        if(suffix.startsWith("."))
            suffix=suffix.substring(1);
        return suffix;
    }
    public static String removeSuffix(String name){
        int lastindex = name.lastIndexOf('.');
        if(lastindex==-1)
            return name;
        return name.substring(0,lastindex);
    }
}
