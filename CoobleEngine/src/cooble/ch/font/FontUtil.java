package cooble.ch.font;

/**
 * Created by Matej on 30.7.2017.
 */
public class FontUtil {

    /**
     * Used only with render method
     * -> changes some characters to be possible to have them in 256 char set
     * @param s
     * @return string for rendering method
     */
    public static String translate(String s) {
        if(s==null)
            return null;
        char[] out = new char[s.length()];
        for (int i = 0; i < out.length; i++) {
            char c = s.charAt(i);
            switch (c) {
                case 283:
                    c = 164;
                    break;
                case 353:
                    c = 166;
                    break;
                case 269:
                    c = 167;
                    break;
                case 345:
                    c = 168;
                    break;
                case 382:
                    c = 169;
                    break;
                case 367:
                    c = 172;
                    break;
                case 328:
                    c = 173;
                    break;
                case 271:
                    c = 174;
                    break;
                case 357:
                    c = 177;
                    break;
                case 352:
                    c = 181;
                    break;
                case 344:
                    c = 183;
                    break;
                case 381:
                    c = 184;
                    break;
                case 327:
                    c = 194;
                    break;
                case 270:
                    c = 196;
                    break;
                case 356:
                    c = 199;
                    break;
                case 282:
                    c = 230;
                    break;
                case 268:
                    c = 214;
                    break;
                case 366:
                    c = 238;
                    break;
            }
            out[i]=c;
        }
        return new String(out);
    }
}
