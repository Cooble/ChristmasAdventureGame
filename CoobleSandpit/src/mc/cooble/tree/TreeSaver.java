package mc.cooble.tree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import mc.cooble.xml.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Matej on 21.7.2017.
 */
public class TreeSaver {

    static Document document;

    public static void saveTree(TreeView<Tree.Comment> tree, String path, String name) {
        XML.setupForSave();
        document = XML.doc;
        Element root = document.createElement(name);
        document.appendChild(root);

        TreeItem<Tree.Comment> rootItem = tree.getRoot();
        TreeItem<Tree.Comment> itemfirst = rootItem.getChildren().get(0);

        for (TreeItem<Tree.Comment> tee : itemfirst.getChildren()) {
            root.appendChild(createComment(tee));
        }

        XML.save(path);
    }

    private static Element createComment(TreeItem<Tree.Comment> comment) {
        Element e = document.createElement("com");
        e.setAttribute("speaker", comment.getValue().getSpeaker());
        e.setAttribute("id", comment.getValue().getValue());
        if (comment.getValue().getNbtEnable() != null)
            e.setAttribute("enable", comment.getValue().getNbtEnable());
        if(comment.getValue().getNewDialogName()!=null)
            e.setAttribute("dialog",comment.getValue().getNewDialogName());
        for (TreeItem<Tree.Comment> item : comment.getChildren()) {
            e.appendChild(createComment(item));
        }
        return e;
    }

    public static void loadTree(String path) {
        XML.loadDocument(path);
        document = XML.doc;
        Element root = document.getDocumentElement();
        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            Node node = root.getChildNodes().item(i);
            if (node instanceof Element) {
                Element e = (Element) node;
                loadComments(e, Tree.getParent());
            }
        }

    }

    private static void loadComments(Element e, TreeItem<Tree.Comment> parent) {
        String speaker = e.getAttribute("speaker");
        String id = e.getAttribute("id");
        String enable = e.getAttribute("enable");
        String dialogName = e.getAttribute("dialog");


        TreeItem<Tree.Comment> child = Tree.createBranch(parent, id);
        child.getValue().setSpeaker(speaker);
        child.getValue().setNbtEnable(enable);
        child.getValue().setNewDialogName(dialogName);

        for (int i = 0; i < e.getChildNodes().getLength(); i++) {
            Node node = e.getChildNodes().item(i);
            if (node instanceof Element) {
                Element ee = (Element) node;
                loadComments(ee, child);
            }
        }
    }

    public static void saveTranslation(TreeView<Tree.Comment> tree, String path) {
        HashMap<String, String> map = new HashMap<>();
        TreeItem<Tree.Comment> rootItem = tree.getRoot();
        TreeItem<Tree.Comment> itemfirst = rootItem.getChildren().get(0);
        for (int i = 0; i < itemfirst.getChildren().size(); i++) {
            writeTranslation(map, itemfirst.getChildren().get(i));
        }
        BufferedWriter writer = null;
        try {
            //create a temporary file
            File logFile = new File(path);

            // This will output the full path where the file will be written to...
            System.out.println(logFile.getCanonicalPath());
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8"));
            final BufferedWriter finalWriter = writer;
            finalWriter.write("->dialog."+Tree.getName());
            finalWriter.newLine();
            map.forEach(new BiConsumer<String, String>() {
                @Override
                public void accept(String s, String s2) {
                    try {
                        finalWriter.write(s + "=" + s2);
                        finalWriter.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }

    }

    public static void loadTranslation(TreeView<Tree.Comment> tree, String path) {
        TreeItem<Tree.Comment> rootItem = tree.getRoot();
        TreeItem<Tree.Comment> itemfirst = rootItem.getChildren().get(0);
        try {
            Stream<String> stringStream = Files.lines(new File(path).toPath(), Charset.defaultCharset());
            stringStream.forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    if(s.startsWith("->")){
                        Tree.setName(s.substring(9));
                    }
                    String[] array = s.split("=");
                    if (array.length != 2)
                        return;
                    Tree.Comment comment = findComment(itemfirst, array[0]);
                    if (comment != null) {
                        comment.getCommentTranslated().setTranslate(array[1]);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Tree.Comment findComment(TreeItem<Tree.Comment> commentTreeItem, String srcName) {
        if (commentTreeItem.getValue().getValue().equals(srcName))
            return commentTreeItem.getValue();
        for (int i = 0; i < commentTreeItem.getChildren().size(); i++) {
            Tree.Comment comment = findComment(commentTreeItem.getChildren().get(i), srcName);
            if (comment != null)
                return comment;
        }
        return null;
    }

    private static void writeTranslation(Map<String, String> map, TreeItem<Tree.Comment> item) {
        map.put(item.getValue().getValue(), item.getValue().getCommentTranslated().getTranslated());
        for (int i = 0; i < item.getChildren().size(); i++) {
            writeTranslation(map, item.getChildren().get(i));
        }
    }
}
