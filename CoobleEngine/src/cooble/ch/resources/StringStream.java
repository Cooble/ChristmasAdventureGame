package cooble.ch.resources;


import cooble.ch.translate.Translator;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * Created by Matej on 1.1.2016.
 */
public final class StringStream implements Supplier<String> {
    String[] array;
    int curIndex = -1;

    public StringStream(String... strings) {
        array = strings;
    }

    public String getNextString() {
        if(array==null)
            return null;
        curIndex++;
        if (curIndex == array.length) {
            curIndex = 0;
        }
        if (array.length == 0)
            return null;
        return array[curIndex];
    }

    public static String[] getTranslated(String string, int size) {
        String[] out = new String[size];
        for (int i = 0; i < out.length; i++) {
            out[i] = Translator.translate(string + i);
        }
        return out;
    }

    public static String[] getArray(String string, int size) {
        String[] out = new String[size];
        for (int i = 0; i < out.length; i++) {
            out[i] = string + i;
        }
        return out;
    }

    /**
     * It uses Translator to check, how many versions there are
     *
     * @param string
     * @return
     */
    public static String[] getArray(String string) {
        int size = 0;
        String[] smth = getTranslated(string);
        if (smth == null)
            return null;
        size = smth.length;
        String[] out = new String[size];
        for (int i = 0; i < out.length; i++) {
            out[i] = string + i;
        }
        return out;
    }

    public static String[] getTranslated(String string) {
        return getTranslatedSrc(string, false);
    }

    public static String[] getTranslatedTry(String string) {
        return getTranslatedSrc(string, true);
    }


    private static String[] getTranslatedSrc(String string, boolean tryMode) {
        ArrayList<String> strings = new ArrayList<>();
        int i = 0;
        while (true) {
            String s = tryMode ? Translator.translateTry(string + i) : Translator.translate(string + i);
            if (s == null)
                break;
            strings.add(s);
            i++;
        }
        if (strings.size() == 0)
            return null;
        String[] stockArr = new String[strings.size()];
        stockArr = strings.toArray(stockArr);
        return stockArr;
    }

    public static StringStream getTranslatedStream(String string) {
        String[] strings = getTranslated(string);
        if (strings == null)
            return null;
        return new StringStream(strings);
    }
    public static StringStream getStream(String string) {
        ArrayList<String> out = new ArrayList<>();
        String current=string+"0";
        int index=0;
        while (Translator.translate(current)!=null){
            out.add(current);
            index++;
            current=string+index;
        }
        String[] a = new String[out.size()];
        for (int i = 0; i < out.size(); i++) {
            a[i]=out.get(i);
        }
        return new StringStream(a);
    }

    public static StringStream getTranslatedStreamTry(String string) {
        String[] strings = getTranslatedTry(string);
        if (strings == null)
            return null;
        return new StringStream(strings);
    }

    @Override
    public String get() {
        return getNextString();
    }

    public int size() {
        return array.length;
    }
}
