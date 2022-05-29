package cooble.ch.translate;

/**
 * Created by Matej on 23.4.2016.
 */
public class SlovoManager {

    public static boolean containsWord(String sentence, String word) {
        return getPoradiSlovaVeVete(word, sentence) != -1;
    }

    public static String[] getWordsFromSentence(String sentence) {
        String[] out = sentence.split(" ");
        return out;
    }

    public static int getPoradiSlovaVeVete(String slovo, String veta) {
        String[] slova = getWordsFromSentence(veta);
        for (int i = 0; i < slova.length; i++) {
            String s = slova[i];
            if (s.equals(slovo))
                return i;
        }
        return -1;
    }

    public static String getSlovoFromVeta(String veta, int indexSlova) {
        return getWordsFromSentence(veta)[indexSlova];
    }

    public static int pocetWords(String veta) {
        if (veta == null)
            return 0;
        return getWordsFromSentence(veta).length;
    }

    public static String createVetaFromArray(String[] arrayVeta, int fromIndex, int toIndex) {
        String out = "";
        for (int i = fromIndex; i < toIndex + 1; i++) {
            out += arrayVeta[i] + " ";
        }
        if (out.endsWith(" "))
            out = out.substring(0, out.length() - 1);

        return out;
    }

    public static String createVetaFromArray(String[] arrayVeta, int fromIndex) {
        String out = "";
        for (int i = fromIndex; i < arrayVeta.length; i++) {
            out += arrayVeta[i] + " ";
        }
        if (out.endsWith(" "))
            out = out.substring(0, out.length() - 1);

        return out;
    }
}

