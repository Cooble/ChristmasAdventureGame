package cs.cooble.tree;

import cs.cooble.duck.Loc;
import cs.cooble.fx.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.File;

/**
 * Created by Matej on 20.7.2017.
 */
public class Tree {
    private static TreeView<Comment> tree;
    private static TreeView<CommentTranslated> treeTwo;
    private static TreeItem<Comment> item1;
    private static ComboBox<String> comboBox;

    private static Button setName;

    private static String name;
    public static Node start(){
        BorderPane pane = new BorderPane();
        BorderPane buttonBox = new BorderPane();
        BorderPane centre = new BorderPane();
        pane.setTop(buttonBox);

        // Root Item 1
        TreeItem<Comment> rootItem = new TreeItem<>(new Comment("#root",new CommentTranslated()));
        rootItem.setExpanded(true);
        // Root Item 2
        TreeItem<CommentTranslated> rootItem2 = new TreeItem<>(new CommentTranslated());
        rootItem.setExpanded(true);

        //item 2
        CommentTranslated translated = new CommentTranslated();
        translated.setTranslate("TRANSLATE");
        TreeItem<CommentTranslated> item2 = new TreeItem<>(translated);
        translated.setItem(item2);
        item2.setExpanded(true);
        item2.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue==newValue)
                    return;
                item2.setExpanded(true);
            }
        });


        // item 1
        TreeItem<Comment> item1 = new TreeItem<Comment>(new Comment("SRC",translated));
        item1.getValue().setItem(item1);
        item1.setExpanded(true);
        item1.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue==newValue)
                    return;
                item1.setExpanded(true);
            }
        });
        Tree.item1=item1;


        // Add to Root
        rootItem.getChildren().add(item1);
        rootItem2.getChildren().add(item2);

        tree = new TreeView<>(rootItem);
        tree.setShowRoot(false);
        tree.setEditable(true);

        tree.setCellFactory(new Callback<TreeView<Comment>, TreeCell<Comment>>() {
            @Override
            public TreeCell<Comment> call(TreeView<Comment> param) {
                return new TextFieldTreeCell<Comment>(new StringConverter<Comment>() {
                    @Override
                    public String toString(Comment object) {
                        return object.toString();
                    }

                    @Override
                    public Comment fromString(String string) {
                        Comment c = param.getSelectionModel().getSelectedItem().getValue();
                        c.setSrc(string);
                        return c;
                    }
                });
            }
        });

        treeTwo=new TreeView<>(rootItem2);

        treeTwo.setEditable(true);
        treeTwo.setShowRoot(false);

        treeTwo.setCellFactory(new Callback<TreeView<CommentTranslated>, TreeCell<CommentTranslated>>() {
            @Override
            public TreeCell<CommentTranslated> call(TreeView<CommentTranslated> param) {
                return new TextFieldTreeCell<CommentTranslated>(new StringConverter<CommentTranslated>() {
                    @Override
                    public String toString(CommentTranslated object) {
                        return object.toString();
                    }

                    @Override
                    public CommentTranslated fromString(String string) {
                        CommentTranslated c = param.getSelectionModel().getSelectedItem().getValue();
                        c.setTranslate(string);
                        return c;
                    }
                });
            }
        });

        tree.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(oldValue.equals(newValue))
                    return;
                treeTwo.getSelectionModel().select(newValue.intValue());
            }
        });
        treeTwo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(oldValue.equals(newValue))
                    return;
                tree.getSelectionModel().select(newValue.intValue());
            }
        });


        centre.setLeft(treeTwo);
        centre.setRight(tree);
        treeTwo.setPrefSize(1920/5*4,900);
        tree.setPrefSize(1920/5,900);

        pane.setCenter(centre);

        addButtons(buttonBox);

        return pane;
    }

    private static void registerNewSpeaker(String name){
        if(name==null||name.equals(""))
            return;
        if(!comboBox.getItems().contains(name)){
            comboBox.getItems().add(name);
        }
    }

    public static TreeItem<Comment> getParent() {
        return item1;
    }

    public static TreeItem<Comment> createBranch(TreeItem<Comment> parent,String name){
        CommentTranslated translated = new CommentTranslated();
        TreeItem<CommentTranslated> translatedItem = new TreeItem<>(translated);
        translated.setItem(translatedItem);

        Comment comment = new Comment(name,translated);
        TreeItem<Comment> item = new TreeItem<>(comment);
        comment.setItem(item);

        parent.getChildren().add(item);
        parent.getValue().getCommentTranslated().getItem().getChildren().add(translatedItem);


        item.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue==newValue)
                    return;
                translatedItem.expandedProperty().setValue(newValue);

            }
        });
        translatedItem.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(oldValue==newValue)
                    return;
                item.expandedProperty().setValue(newValue);
            }
        });
        translated.setTranslate("NEW");

        String speaker = comboBox.getSelectionModel().getSelectedItem();
        if(speaker!=null){
            comment.setSpeaker(speaker);
        }

        return item;
    }

    private static void addButtons(BorderPane src){
        HBox leftBox = new HBox();
        HBox rightBox = new HBox();
        src.setLeft(leftBox);
        src.setRight(rightBox);
        Button add = new Button("Add");
        Button remove = new Button("Destroy");
        Button save = new Button("Save");
        Button load = new Button("Load");
        setName = new Button("SetName");
        leftBox.getChildren().addAll(add,remove,save,load);
        rightBox.getChildren().add(setName);
        remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<Comment> item = tree.getSelectionModel().getSelectedItem();
                TreeItem<CommentTranslated> item2 = treeTwo.getSelectionModel().getSelectedItem();
                if(item1.equals(item))
                    return;

                if(item!=null){
                    tree.getSelectionModel().select(item.getParent());
                    killBranch(item);
                   // treeTwo.getSelectionModel().select(item2.getParent().getValue().getItem());
                    killBranch(item2);
                }
            }
        });
        add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TreeItem<Comment> item = tree.getSelectionModel().getSelectedItem();
                if(item!=null){
                    TreeItem item1 = createBranch(item,"NEW");
                    tree.getSelectionModel().select(item1);
                }
            }
        });
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File f = saveFileAs("XML files (*.xml)", "*.xml");
                File ff = saveFileAs("TXT files (*.txt)", "*.txt");
                if(ff==null)
                    return;
                if(f==null)
                    return;
                TreeSaver.saveTree(tree, f.getAbsolutePath(), "dialog");
                TreeSaver.saveTranslation(tree, ff.getAbsolutePath());
            }
        });
        load.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File f = openFile("XML files (*.xml)", "*.xml");
                if(f==null||!f.exists())
                    return;
                TreeSaver.loadTree(f.getAbsolutePath());

                File ff = openFile("TXT files (*.txt)", "*.txt");
                if(ff==null||!ff.exists())
                    return;
                TreeSaver.loadTranslation(tree, ff.getAbsolutePath());
                tree.refresh();
                treeTwo.refresh();
            }
        });
        setName.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String name = Main.askForString("Enter Name of Dialog", false);
                setName.setText("SetName: "+name);
                Tree.name=name;

            }
        });

        ObservableList<String> options =
                FXCollections.observableArrayList();
        comboBox = new ComboBox(options);
        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(comboBox.getSelectionModel().getSelectedItem());
                TreeItem<Comment> item  = tree.getSelectionModel().getSelectedItem();
                if(item==null)
                    return;
                item.getValue().setSpeaker(comboBox.getSelectionModel().getSelectedItem().toString());
                tree.refresh();
            }
        });

        Button addSpeaker = new Button("AddSpeaker");
        addSpeaker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String s = Main.askForString("SpeakerName",true);
                if(s!=null)
                    options.add(s);
            }
        });

        leftBox.getChildren().addAll(comboBox, addSpeaker);
        //ogogo

        MenuItem delete = new MenuItem("delete");
        delete.setOnAction(remove.getOnAction());
        MenuItem tagManager = new MenuItem("tags");
        tagManager.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                openTags(tree.getSelectionModel().getSelectedItem().getValue());
            }
        });
        MenuItem addd= new MenuItem("new");
        addd.setOnAction(add.getOnAction());

        MenuItem deleteOne = new MenuItem("deleteOnlyOne");
        deleteOne.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              /*  TreeItem<Comment> item = tree.getSelectionModel().getSelectedItem();
                TreeItem<Comment> parent = item.getParent();
                ObservableList<TreeItem<Comment>> children = item.getChildren();
                killBranch(item);
                parent.getChildren().addAll(children);

                TreeItem<CommentTranslated> item2 = treeTwo.getSelectionModel().getSelectedItem();
                TreeItem<CommentTranslated> parent2 = item2.getParent();
                ObservableList<TreeItem<CommentTranslated>> children2 = item2.getChildren();
                killBranch(item2);
                parent2.getChildren().addAll(children2);

                tree.refresh();
                treeTwo.refresh();*/
            }
        });

        ContextMenu contextMenu = new ContextMenu(tagManager,addd,delete,deleteOne);
        tree.setContextMenu(contextMenu);
        treeTwo.setContextMenu(contextMenu);

        Button locationSceneBtn = new Button("Location");
        rightBox.getChildren().add(locationSceneBtn);
        locationSceneBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Main.window.setScene(Main.comScene);
            }
        });

    }
    public static void openTags(Comment comment){
        Stage dialog = new Stage();
        dialog.setTitle("Comment: "+comment.getValue()+" = "+comment.getCommentTranslated().getTranslated());
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        VBox vBox = new VBox(20);
        Scene scene = new Scene(vBox);
        dialog.setScene(scene);

        Label labelTxt = new Label("NewDialogName");
        TextField dialogTxt = new TextField();
        dialogTxt.setText(comment.getNewDialogName());
        dialogTxt.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if(dialogTxt.getText().equals(""))
                        dialogTxt.setText(null);
                    comment.setNewDialogName(dialogTxt.getText());
                }
            }
        });
        Button setEnd = new Button("SetEnd");
        setEnd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                dialogTxt.setText("endDialog");
                comment.setNewDialogName(dialogTxt.getText());
            }
        });
        HBox dialogName = new HBox(labelTxt,dialogTxt,setEnd);
        vBox.getChildren().add(dialogName);

        Label nbtEnable = new Label("NBT Enable Name");
        TextField nbtTxt = new TextField();
        nbtTxt.setText(comment.getNbtEnable());

        nbtTxt.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if("".equals(nbtTxt.getText()))
                    nbtTxt.setText(null);
                comment.setNbtEnable(nbtTxt.getText());
            }
        });
        HBox nbt = new HBox(nbtEnable,nbtTxt);
        vBox.getChildren().add(nbt);

        dialog.show();
    }

    private static void killBranch(TreeItem item){
        item.getParent().getChildren().remove(item);
    }

    public static void setName(String name) {
        Tree.name = name;
        setName.setText("Set Name: "+name);
    }

    public static String getName() {
        return name;
    }

    public static class Comment{
        public String src;
        private CommentTranslated commentTranslated;
        private TreeItem<Comment> item;
        private String speakerName;
        private String nbtEnable;
        private String newDialogName;

        public Comment(String src,CommentTranslated translated){
            this.commentTranslated = translated;
            setSrc(src);
        }

        public void setItem(TreeItem<Comment> item) {
            this.item = item;
        }

        public TreeItem<Comment> getItem() {
            return item;
        }

        public void setSpeaker(String speakerName) {
            this.speakerName = speakerName;
            registerNewSpeaker(speakerName);
        }

        public String getSpeaker() {
            return speakerName;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public CommentTranslated getCommentTranslated() {
            return commentTranslated;
        }

        public String getValue(){
            return src;
        }

        public void setNbtEnable(String nbtEnable) {
            this.nbtEnable = nbtEnable;
        }

        public String getNbtEnable() {
            return nbtEnable;
        }

        public void setNewDialogName(String newDialogName) {
            this.newDialogName = newDialogName;
        }

        public String getNewDialogName() {
            return newDialogName;
        }

        @Override
        public String toString() {
            return (speakerName==null?"":("["+speakerName.toUpperCase()+"] "))+src;
        }
    }
    public static class CommentTranslated{
        private String translate;
        private TreeItem<CommentTranslated> item;

        public void setItem(TreeItem<CommentTranslated> item) {
            this.item = item;
        }

        public TreeItem<CommentTranslated> getItem() {
            return item;
        }

        public void setTranslate(String translate) {
            this.translate = translate;
        }

        public String getTranslated() {
            return translate;
        }

        @Override
        public String toString() {
            return translate;
        }
    }


    public static File saveFileAs(String filter,String suffix){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Loc.SRC_FOLDER+"dialog"));

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(filter,suffix);
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(null);
        return file;
    }
    public static File openFile(String filter,String suffix){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(Loc.SRC_FOLDER+"dialog"));

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(filter,suffix);
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showOpenDialog(null);
        return file;
    }
}
