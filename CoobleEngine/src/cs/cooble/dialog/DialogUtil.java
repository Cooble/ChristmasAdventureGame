package cs.cooble.dialog;

import cs.cooble.saving.SaverUtil;
import cs.cooble.xml.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matej on 21.7.2017.
 */
public class DialogUtil {
    public static final String END = "endDialog";
    private static String dialogName;

    public static Dialog loadDialog(String path) {
        dialogName= SaverUtil.getFileName(SaverUtil.getParent(path));
        XML.loadDocument(path);
        Document document = XML.doc;
        Element root = document.getDocumentElement();

        Dialog.Comment rootComment = new Dialog.Comment(null);

        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            Node node = root.getChildNodes().item(i);
            if (!(node instanceof Element))
                continue;
            Element e = (Element) node;
            Dialog.Comment child = loadComment(e, rootComment);
            rootComment.getChildren().add(child);
        }
        return new Dialog(rootComment);
    }

    private static Dialog.Comment loadComment(Element e, Dialog.Comment parent) {
        Dialog.Comment comment = new Dialog.Comment(parent);
        comment.setSrc(e.getAttribute("id"));
        comment.setSpeaker(e.getAttribute("speaker"));
        comment.setNbtEnable(e.getAttribute("enable"));
        String newDialog = e.getAttribute("dialog");
        if(END.equals(newDialog)){
            newDialog=null;
        }
        else if (newDialog == null) {
            newDialog = dialogName;
        }
        comment.setNewDialogName(newDialog);

        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            Node node = e.getChildNodes().item(i);
            if (!(node instanceof Element))
                continue;
            Element childElement = (Element) node;
            comment.addChild(loadComment(childElement, comment));
        }
        return comment;
    }

    public static List<String> getCurrentStrings(Dialog dialog) {
        ArrayList<String> out = new ArrayList<>(dialog.getCurrentComments().size());
        for (Dialog.Comment comment : dialog.getCurrentComments()) {
            out.add(comment.getSrc());
        }
        return out;
    }

    public static String[] toArray(List<String> strings) {
        String[] out = new String[strings.size()];
        for (int i = 0; i < strings.size(); i++) {
            String s = strings.get(i);
            out[i] = s;
        }
        return out;
    }
}
