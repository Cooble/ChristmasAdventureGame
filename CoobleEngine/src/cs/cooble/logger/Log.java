package cs.cooble.logger;

/**
 * Created by Matej on 28.12.2016.
 */
public class Log {
    private static boolean onlyClass = true;
    private static boolean enabled = false;
    private static boolean isFromInnerMethod;

    public static void setWholePath(boolean isDebugging) {
        onlyClass = !isDebugging;
    }

    public static void setPathPrintEnabled(boolean enabled) {
        Log.enabled = enabled;
    }

    public static void println(String o, LogType logType) {
        if (!enabled) {
            return;
           // println(o);
        }
        String location="";
        int index = isFromInnerMethod ? 2 : 1;
        isFromInnerMethod = false;
        try {
            if (onlyClass) {
                location = new Exception().getStackTrace()[index].getFileName();
                location = location.substring(0, location.length() - 5);
            } else location = new Exception().getStackTrace()[index].toString();

        } catch (Exception e) {
            e.getStackTrace();
        }
        if(logType==LogType.PATH){
            new Exception("PATH: "+o).printStackTrace();
            return;
        }

        System.out.println(color(logType.COLOR) + (logType.equals(LogType.DEFAULT) ? "" : logType.NAME + "->") + "[" + location + "]: " + o + color(ChatColor.CLEAR));
    }

    public static void println(String o) {
        if(!enabled)
            return;
        isFromInnerMethod = true;
        println(o, LogType.DEFAULT);
    }

    public static void println(Object o) {
        println(o.toString());
    }

    private static String color(ChatColor chatColor) {
        if (chatColor.equals(ChatColor.WHITE))
            return "";
        return (char) 27 + "[" + chatColor + "m";
    }



    public enum LogType {
        DEFAULT("DEFAULT", ChatColor.WHITE),
        WARN("WARN", ChatColor.YELLOW),
        ERROR("ERROR", ChatColor.RED),
        PATH("PATH", ChatColor.WHITE);

        private final String NAME;
        private final ChatColor COLOR;

        LogType(String name, ChatColor color) {
            NAME = name;
            COLOR = color;
        }

        @Override
        public String toString() {
            return NAME;
        }
    }

    private enum ChatColor {
        BLACK(30),
        RED(31),
        GREEN(32),
        YELLOW(33),
        BLUE(34),
        WHITE(37),
        BOLD(1),
        STOP_BOLD(21),
        UNDERLINE(4),
        STOP_UNDERLINE(24),
        CLEAR(0);

        private int ID;

        ChatColor(int i) {
            ID = i;

        }

        @Override
        public String toString() {
            return ID + "";
        }
    }
}
