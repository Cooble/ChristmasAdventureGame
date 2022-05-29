package cooble.ch.fx;

import cooble.ch.canvas.Bitmap;
import cooble.ch.duck.Arrow;
import cooble.ch.duck.Loc;
import cooble.ch.duck.Stuff;
import cooble.ch.tree.Tree;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.File;


/**
 * Created by Matej on 18.5.2017.
 */
public class Main extends Application implements Runnable {

    public static Stage window;
    public static Scene comScene;
    public static Scene dialogScene;
    Canvas canvas;
    boolean hasEditingWindowOpened;
    @FXML
    ListView<String> listView;
    public static final int OFFSET_CANVAS = Controller.WIDTH / 10;

    public Controller controller;
    private Runnable mujTimerListener;
    private boolean isRunning;

    private static final int TPS = 60;

    private Button arrowLocationBtn;

    public void onArrowSelected(boolean selected) {
        arrowLocationBtn.setDisable(!selected);
    }

    public Scene getComScene() {
        return comScene;
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        controller = new Controller(this);
        window = primaryStage;
        window.setTitle("Sandpit");
        setComScene();
        setDialogScene();
        mujTimerListener = this;
        window.setScene(comScene);
        window.show();
        runLoop();
        window.setOnCloseRequest(event -> {
            try {
                controller.onProgramQuited();
                Platform.exit();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        controller.finishPreparations();
        window.setResizable(true);
        window.setX(0);
        window.setY(0);
    }

    private Stage openedWindow;

    private void closeWindows() {

        final Stage closeme = openedWindow;
        if (openedWindow != null) {
            paramsX = (int) openedWindow.getX();
            paramsY = (int) openedWindow.getY();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        closeme.close();
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                }
            });
            openedWindow = null;
            hasEditingWindowOpened = false;
        }
    }

    private void registerClosingMethod(Window stage) {
        hasEditingWindowOpened = true;
        stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ESCAPE)) {
                    ke.consume(); // <-- stops passing the event to next node
                    closeWindows();
                }
            }
        });
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                closeWindows();
            }
        });
    }

    private void setComScene() {
        //Settings scene 1
        BorderPane mainPane = new BorderPane();
        comScene = new Scene(mainPane, Controller.WIDTH + OFFSET_CANVAS * 2 + 200, Controller.HEIGHT + OFFSET_CANVAS * 2 + 25);
        comScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ESCAPE)) {
                    ke.consume(); // <-- stops passing the event to next node
                    closeWindows();
                }
            }
        });

        canvas = new Canvas(Controller.WIDTH + OFFSET_CANVAS * 2, Controller.HEIGHT + OFFSET_CANVAS * 2);
        mainPane.setCenter(canvas);
        listView = new ListView<String>();
        listView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
                        controller.refreshListView();
                        if (hasEditingWindowOpened) {
                            closeWindows();
                            controller.openEditing();
                        }
                    }
                });
        mainPane.setRight(listView);
        MenuBar bar = new MenuBar();
        HBox leftBox = new HBox();
        BorderPane topPane = new BorderPane();
        mainPane.setTop(topPane);
        leftBox.getChildren().add(bar);
        topPane.setLeft(leftBox);
        HBox rightBox = new HBox();
        topPane.setRight(rightBox);

        Menu fileMenu = new Menu("File");
        Menu addMenu = new Menu("Add");
        Menu editMenu = new Menu("Edit");
        Menu removeMenu = new Menu("REMOVE");
        MenuItem locMenuedit = new MenuItem("Location");
        locMenuedit.setOnAction(controller::openLocParams);
        editMenu.getItems().add(locMenuedit);
        bar.getMenus().addAll(fileMenu, addMenu, editMenu, removeMenu);
        MenuItem saveMenu = new MenuItem("Save");
        saveMenu.setOnAction(controller::saveFileAs);
        saveMenu.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN));
        MenuItem loadMenu = new MenuItem("Open");
        loadMenu.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN));
        loadMenu.setOnAction(controller::openLocation);
        MenuItem newMenu = new MenuItem("New");
        newMenu.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN));
        newMenu.setOnAction(controller::newFileLocation);
        MenuItem setNameMenu = new MenuItem("Set Location Name");
        setNameMenu.setOnAction(controller::setLocationName);
        fileMenu.getItems().addAll(saveMenu, loadMenu, newMenu, setNameMenu);

        MenuItem stuffMenu = new MenuItem("Stuff");
        stuffMenu.setOnAction(controller::addStuff);
        MenuItem stuffpickMenu = new MenuItem("Stuff PickUp-able");
        stuffpickMenu.setOnAction(controller::addPickStuff);
        MenuItem stuffNoBitmapkMenu = new MenuItem("Stuff No-Bitmap");
        stuffNoBitmapkMenu.setOnAction(controller::addStuffNoBitmap);
        MenuItem arrowMenu = new MenuItem("Arrow");
        arrowMenu.setOnAction(controller::addArrow);
        MenuItem arrowMenunot = new MenuItem("Arrow No-Bitmap");
        arrowMenunot.setOnAction(controller::addArrowNoBitmap);
        addMenu.getItems().addAll(stuffMenu, stuffpickMenu, stuffNoBitmapkMenu, arrowMenu, arrowMenunot);

        MenuItem editMenuu = new MenuItem("Other");
        editMenuu.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCodeCombination.CONTROL_DOWN));
        editMenu.getItems().add(editMenuu);
        editMenuu.setOnAction(controller::edit);

        MenuItem removeSelected = new MenuItem("selected");
        removeSelected.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN));
        removeSelected.setOnAction(controller::removeSelected);
        removeMenu.getItems().add(removeSelected);


        Button ARModeBtn = new Button("AR: OFF");
        leftBox.getChildren().add(ARModeBtn);
        ARModeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ARModeBtn.getText().equals("AR: ON ")) {
                    ARModeBtn.setText("AR: OFF");
                    controller.switchARMode(false);
                } else {
                    ARModeBtn.setText("AR: ON ");
                    controller.switchARMode(true);
                }
            }
        });

        Button ArrowModeBtn = new Button("ShowArrows: OFF");
        leftBox.getChildren().add(ArrowModeBtn);
        ArrowModeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ArrowModeBtn.getText().equals("ShowArrows: ON ")) {
                    ArrowModeBtn.setText("ShowArrows: OFF");
                    controller.switchArrowMode(false);
                } else {
                    ArrowModeBtn.setText("ShowArrows: ON ");
                    controller.switchArrowMode(true);
                }
            }
        });
        Button joeBtn = new Button("Joe: OFF");
        leftBox.getChildren().add(joeBtn);
        joeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (joeBtn.getText().equals("Joe: ON ")) {
                    joeBtn.setText("Joe: OFF");
                    controller.switchJoeMode(false);
                } else {
                    joeBtn.setText("Joe: ON ");
                    controller.switchJoeMode(true);
                }
            }
        });
        arrowLocationBtn = new Button("Go to Location");
        arrowLocationBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                controller.goToArrowLocation();
            }
        });
        rightBox.getChildren().add(arrowLocationBtn);
        arrowLocationBtn.setDisable(true);

        Button dialogBtn = new Button("Dialog");
        leftBox.getChildren().add(dialogBtn);
        dialogBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                window.setScene(dialogScene);
            }
        });

    }

    private void setDialogScene() {
        BorderPane mainPane = new BorderPane();
        dialogScene = new Scene(mainPane);
        mainPane.setCenter(Tree.start());
    }

    public void sleep(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void runLoop() {
        Thread thread = new Thread(() -> {
            isRunning = true;
            while (isRunning) {
                run();
                sleep((1000 / TPS));
            }
        });
        thread.start();

    }

    public void stopLoop() {
        isRunning = false;
    }

    @Override
    public void run() {
        controller.tick();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public ListView<String> getListView() {
        return listView;
    }

    public void openStuffParams(Stuff stuff) {
        Stage dialog = new Stage();
        dialog.setTitle("Stuff");
        openedWindow = dialog;
        registerClosingMethod(dialog);
        dialog.setX(paramsX);
        dialog.setY(paramsY);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        VBox vBox = new VBox(20);
        Scene scene = new Scene(vBox);
        dialog.setScene(scene);

        HBox hBox;
        Label label;
        vBox.getChildren().add(addStuffAtribute("ID", stuff.getID(), field -> {
            stuff.setID(field.getText());
            controller.refreshList();
        }));
        vBox.getChildren().add(addStuffAtribute("X", stuff.getX() + "", field -> {
            stuff.setActionOffset(Integer.parseInt(field.getText()), stuff.getY());
        }));
        vBox.getChildren().add(addStuffAtribute("Y", stuff.getY() + "", field -> {
            stuff.setActionOffset(stuff.getX(), Integer.parseInt(field.getText()));
        }));
        vBox.getChildren().add(addStuffAtribute("WIDTH", stuff.getWidth() + "", field -> {
            stuff.setActionDimensions(Integer.parseInt(field.getText()), stuff.getHeight());
        }));
        vBox.getChildren().add(addStuffAtribute("HEIGHT", stuff.getHeight() + "", field -> {
            stuff.setActionDimensions(stuff.getWidth(), Integer.parseInt(field.getText()));
        }));
        vBox.getChildren().add(addStuffAtribute("BITMAP X", stuff.getBitmapOffsetX() + "", field -> {
            stuff.setBitmapOffset(Integer.parseInt(field.getText()), stuff.getBitmapOffsetY());
        }));
        vBox.getChildren().add(addStuffAtribute("BITMAP Y", stuff.getBitmapOffsetY() + "", field -> {
            stuff.setBitmapOffset(stuff.getBitmapOffsetX(), Integer.parseInt(field.getText()));
        }));
        vBox.getChildren().add(addStuffAtribute("BITMAP SCALE", stuff.getScale() + "", field -> {
            stuff.setScale(Double.parseDouble(field.getText()));
        }));
        vBox.getChildren().add(addStuffAtribute("xToCome", stuff.getxToCome() + "", field -> {
            if (controller.getJoe().isEnabled()) {
                controller.getJoe().setPos(Integer.parseInt(field.getText()), controller.getJoe().getPosY());
            }
            stuff.setToCome(Integer.parseInt(field.getText()), stuff.getyToCome());
        }));
        vBox.getChildren().add(addStuffAtribute("yToCome", stuff.getyToCome() + "", field -> {
            if (controller.getJoe().isEnabled()) {
                controller.getJoe().setPos(controller.getJoe().getPosX(), Integer.parseInt(field.getText()));
            }
            stuff.setToCome(stuff.getxToCome(), Integer.parseInt(field.getText()));
        }));
        if (stuff.isAnimation()) {
            vBox.getChildren().add(new Label("Animation:"));
            vBox.getChildren().add(addStuffAtribute("Delay", stuff.getDelay() + "", field -> {
                stuff.setMaxDelay(Integer.parseInt(field.getText()));
            }));
            hBox = new HBox(20);
            label = new Label("SAW OR TOOTH");
            final CheckBox toComeBox = new CheckBox();
            toComeBox.setSelected(stuff.getAnimationType() == 0);
            toComeBox.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    stuff.setAnimationType(toComeBox.isSelected() ? 0 : 1);
                }
            });
            hBox.getChildren().addAll(label, toComeBox);
            vBox.getChildren().add(hBox);
        }
        Button joeBtn = new Button("SET LOC");
        joeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stuff.setToCome(controller.getJoe().getPosX(), controller.getJoe().getPosY());
                stuff.setIsToCome(true);
                paramsX = (int) dialog.getX();
                paramsY = (int) dialog.getY();
                dialog.close();
                openStuffParams(stuff);
            }
        });
        vBox.getChildren().add(joeBtn);


        hBox = new HBox(20);
        label = new Label("To come");
        final CheckBox toComeBox = new CheckBox();
        toComeBox.setSelected(stuff.isToCome());
        toComeBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stuff.setIsToCome(toComeBox.isSelected());
            }
        });
        hBox.getChildren().addAll(label, toComeBox);
        vBox.getChildren().add(hBox);

        hBox = new HBox(20);
        label = new Label("Pickupable");
        final CheckBox pickupanleBox = new CheckBox();
        pickupanleBox.setSelected(stuff.isPickupable());
        pickupanleBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stuff.setIsPickupable(pickupanleBox.isSelected());
            }
        });

        hBox.getChildren().addAll(label, pickupanleBox);
        vBox.getChildren().add(hBox);
      /*  hBox = new HBox(20);
        label = new Label("Scale");
        final TextField textField9 = new TextField();
        textField9.setText(stuff.getyToCome() + "");
        button = new Button("Save");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stuff.getBitmap().scaleAnonym(Double.parseDouble(textField9.getText()));
            }
        });
        hBox.getChildren().addAll(label, textField9, button);
        vBox.getChildren().add(hBox);*/


        dialog.show();
    }

    private Node addStuffAtribute(String name, String defaultVal, RunnableWithTextField runnable) {
        HBox hBox = new HBox(20);
        Label label = new Label(" " + longString(name, 10));
        final TextField textField = new TextField();
        textField.setText(defaultVal);
        Button button = new Button("Save");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                runnable.run(textField);
            }
        });
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER))
                    runnable.run(textField);
            }
        });
        hBox.getChildren().addAll(label, textField, button);
        return hBox;
    }

    private String longString(String s, int size) {
        while (s.length() < size) {
            s += " ";
        }
        return s;
    }

    public void openLocParams(Loc loc, ActionEvent event) {
        Stage dialog = new Stage();
        dialog.setTitle("Location");
        openedWindow = dialog;
        registerClosingMethod(dialog);
        VBox vBox = new VBox(20);
        Scene scene = new Scene(vBox);
        dialog.setScene(scene);
        vBox.getChildren().add(createNodeAtribute("Background", loc.getBack() == null ? "null" : loc.getBack().getPath(), new MyEventConsumer() {
            @Override
            public void consume(ActionEvent button, TextField textField) {
                controller.bitmapBack(button);
                textField.setText(textField.getText());
            }

            @Override
            public void consume(KeyEvent enter, TextField textField) {
                if (enter.getCode().equals(KeyCode.ENTER)) {
                    if (new File(textField.getText()).exists()) {
                        Bitmap b = new Bitmap(textField.getText());
                        b.scale(1280, 720);
                        loc.setForbidTexture(Loc.FORBID_Back, false);
                        loc.setBack(b);
                        return;
                    }
                    if ("-".equals(textField.getText())) {
                        loc.setForbidTexture(Loc.FORBID_Back, true);
                    } else {
                        textField.setText("null");
                    }
                    loc.setBack(null);

                }
            }
        }));
        vBox.getChildren().add(createNodeAtribute("Foreground", loc.getFore() == null ? "null" : loc.getFore().getPath(), new MyEventConsumer() {
            @Override
            public void consume(ActionEvent button, TextField textField) {
                controller.bitmapFore(button);
                textField.setText(textField.getText());

            }

            @Override
            public void consume(KeyEvent enter, TextField textField) {
                if (enter.getCode().equals(KeyCode.ENTER)) {
                    if (new File(textField.getText()).exists()) {
                        Bitmap b = new Bitmap(textField.getText());
                        b.scale(1280, 720);
                        loc.setForbidTexture(Loc.FORBID_Fore, false);
                        loc.setFore(b);
                        return;
                    }
                    if ("-".equals(textField.getText())) {
                        loc.setForbidTexture(Loc.FORBID_Fore, true);
                    } else {
                        textField.setText("null");
                    }
                    loc.setFore(null);
                }
            }
        }));
        vBox.getChildren().add(createNodeAtribute("Shadow", loc.getShadow() == null ? "null" : loc.getShadow().getPath(), new MyEventConsumer() {
            @Override
            public void consume(ActionEvent button, TextField textField) {
                controller.bitmapShadow(button);
                textField.setText(textField.getText());
            }

            @Override
            public void consume(KeyEvent enter, TextField textField) {
                if (new File(textField.getText()).exists()) {
                    Bitmap b = new Bitmap(textField.getText());
                    b.scale(1280, 720);
                    loc.setForbidTexture(Loc.FORBID_Shadow, false);
                    loc.setShadow(b);
                    return;
                }
                if ("-".equals(textField.getText())) {
                    loc.setForbidTexture(Loc.FORBID_Shadow, true);
                } else {
                    textField.setText("null");
                }
                loc.setShadow(null);
            }
        }));
        vBox.getChildren().add(createNodeAtribute("Boolmap", loc.getBoolmap() == null ? "null" : loc.getBoolmap().getPath(), new MyEventConsumer() {
            @Override
            public void consume(ActionEvent button, TextField textField) {
                controller.bitmapBool(button);
                textField.setText(textField.getText());
            }

            @Override
            public void consume(KeyEvent enter, TextField textField) {
                if (new File(textField.getText()).exists()) {
                    Bitmap b = new Bitmap(textField.getText());
                    b.scale(1280, 720);
                    loc.setForbidTexture(Loc.FORBID_Boolmap, false);
                    loc.setFore(b);
                    return;
                }
                if ("-".equals(textField.getText())) {
                    loc.setForbidTexture(Loc.FORBID_Boolmap, true);
                } else {
                    textField.setText("null");
                }
                loc.setBoolmap(null);

            }
        }));
        vBox.getChildren().add(createNodeAtribute("Music", loc.getMusic() == null ? "null" : loc.getMusic(), new MyEventConsumer() {
            @Override
            public void consume(ActionEvent button, TextField textField) {
                if ("".equals(textField.getText()))
                    textField.setText(null);
                loc.setMusic(textField.getText(), loc.getMusicVolume());
            }

            @Override
            public void consume(KeyEvent enter, TextField textField) {
                if ("".equals(textField.getText()))
                    textField.setText(null);
                loc.setMusic(textField.getText(), loc.getMusicVolume());
            }
        }));
        vBox.getChildren().add(createNodeAtribute("Volume", loc.getMusic() == null ? "0" : loc.getMusicVolume() + "", new MyEventConsumer() {
            @Override
            public void consume(ActionEvent button, TextField textField) {
                if ("".equals(textField.getText()))
                    textField.setText(null);
                loc.setMusic(loc.getMusic(), parseDouble(textField.getText()));
            }

            @Override
            public void consume(KeyEvent enter, TextField textField) {
                if ("".equals(textField.getText()))
                    textField.setText(null);
                loc.setMusic(loc.getMusic(), parseDouble(textField.getText()));
            }
        }));
        vBox.getChildren().add(createNodeAtribute("Sound", loc.getSound() == null ? "null" : loc.getSound(), new MyEventConsumer() {
            @Override
            public void consume(ActionEvent button, TextField textField) {
                if ("".equals(textField.getText()))
                    textField.setText(null);
                loc.setSound(textField.getText(), loc.getSoundVolume());
            }

            @Override
            public void consume(KeyEvent enter, TextField textField) {
                if ("".equals(textField.getText()))
                    textField.setText(null);
                loc.setSound(textField.getText(), loc.getSoundVolume());
            }
        }));
        vBox.getChildren().add(createNodeAtribute("Volume", loc.getSound() == null ? "0" : loc.getSoundVolume() + "", new MyEventConsumer() {
            @Override
            public void consume(ActionEvent button, TextField textField) {
                if ("".equals(textField.getText()))
                    textField.setText(null);
                loc.setSound(loc.getSound(), parseDouble(textField.getText()));
            }

            @Override
            public void consume(KeyEvent enter, TextField textField) {
                if ("".equals(textField.getText()))
                    textField.setText(null);
                loc.setSound(loc.getSound(), parseDouble(textField.getText()));
            }
        }));
        HBox box = new HBox(20);
        Label label = new Label("isSubLocation");
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(loc.isSubLoc());
        checkBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loc.setSubLoc(checkBox.isSelected());
            }
        });
        box.getChildren().addAll(label, checkBox);
        vBox.getChildren().add(box);

        dialog.show();
    }

    private Node createNodeAtribute(String name, String defaultText, MyEventConsumer consumer) {
        HBox hBox = new HBox(20);
        Label label = new Label(name);
        final TextField textField = new TextField();
        textField.setText(defaultText);
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                consumer.consume(event, textField);
            }
        });
        Button buttonn = new Button("...");
        buttonn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                consumer.consume(event, textField);
            }
        });
        hBox.getChildren().addAll(label, textField, buttonn);
        return hBox;
    }

    public static String askForString(String quation, boolean nullPossible) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(quation);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        HBox vBox = new HBox(20);
        Label label = new Label(quation);
        Scene scene = new Scene(vBox);
        dialog.setScene(scene);
        final String[] out = {null};

        TextField enterFiled = new TextField();
        vBox.getChildren().addAll(label, enterFiled);
        enterFiled.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (nullPossible || enterFiled.getText() != null) {
                        out[0] = enterFiled.getText();
                        dialog.close();
                    }
                }
            }
        });
        dialog.showAndWait();
        return out[0];
    }

    public void inform(String info, int delayMillis) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Info");
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        HBox vBox = new HBox(20);
        Label label = new Label(info);
        label.setFont(new Font(20));
        Scene scene = new Scene(vBox);
        vBox.getChildren().add(label);
        dialog.setScene(scene);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(dialog::close);
            }
        });
        thread.start();
        dialog.show();
    }

    public Integer askForInt(String quation, boolean nullPossible) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(quation);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        HBox vBox = new HBox(20);
        Label label = new Label(quation);
        Scene scene = new Scene(vBox);
        dialog.setScene(scene);
        final Integer[] out = {null};

        TextField enterFiled = new TextField();
        vBox.getChildren().addAll(label, enterFiled);
        enterFiled.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (nullPossible || enterFiled.getText() != null) {
                        if (enterFiled.getText() == null) {
                            out[0] = null;
                            dialog.close();
                            return;
                        }
                        try {
                            out[0] = Integer.parseInt(enterFiled.getText());
                        } catch (Exception ignored) {
                            enterFiled.setText("");
                            return;
                        }
                        dialog.close();
                    }
                }
            }
        });

        dialog.showAndWait();
        return out[0];
    }

    public Double askForDouble(String quation, boolean nullPossible) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(quation);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        HBox vBox = new HBox(20);
        Label label = new Label(quation);
        Scene scene = new Scene(vBox);
        dialog.setScene(scene);
        final Double[] out = {null};

        TextField enterFiled = new TextField();
        vBox.getChildren().addAll(label, enterFiled);
        enterFiled.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (nullPossible || enterFiled.getText() != null) {
                        if (enterFiled.getText() == null) {
                            out[0] = null;
                            dialog.close();
                            return;
                        }
                        try {
                            out[0] = Double.parseDouble(enterFiled.getText());
                        } catch (Exception ignored) {
                            try {
                                out[0] = Integer.parseInt(enterFiled.getText()) + 0.0;
                            } catch (Exception ig) {
                                enterFiled.setText("");

                            }
                            return;
                        }
                        dialog.close();
                    }
                }
            }
        });

        dialog.showAndWait();
        return out[0];
    }

    public Boolean askForBool(String quation) {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle(quation);
        dialog.setResizable(false);
        dialog.setAlwaysOnTop(true);
        HBox vBox = new HBox(20);
        Label label = new Label(quation);
        Scene scene = new Scene(vBox);
        dialog.setScene(scene);
        final Boolean[] out = {null};
        Button buttonOK = new Button("YES");
        Button buttonCancel = new Button("NO");
        vBox.getChildren().addAll(label, buttonOK, buttonCancel);
        buttonOK.setOnAction(event -> {
            out[0] = true;
            dialog.close();
        });
        buttonCancel.setOnAction(event -> {
            out[0] = false;
            dialog.close();
        });
        dialog.showAndWait();
        return out[0];
    }

    public void setTitle(String title) {
        window.setTitle(title);
    }

    private int paramsX, paramsY;

    public void openArrowParams(Arrow arrow) {
        Stage dialog = new Stage();
        dialog.setTitle("Arrow");
        openedWindow = dialog;
        registerClosingMethod(dialog);
        dialog.setX(paramsX);
        dialog.setY(paramsY);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        VBox vBox = new VBox(20);
        Scene scene = new Scene(vBox);
        dialog.setScene(scene);

        HBox hBox;
        Label label;

        vBox.getChildren().add(addStuffAtribute("ID", arrow.getID(), field -> {
            arrow.setID(field.getText());
            controller.refreshList();
        }));
        vBox.getChildren().add(addStuffAtribute("Location", arrow.getLocation(), field -> {
            arrow.setLocation(field.getText());
        }));
        CheckBox bigCheckBox = new CheckBox("BIG");
        bigCheckBox.setSelected(arrow.isBig());
        bigCheckBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                arrow.setBig(bigCheckBox.isSelected());
            }
        });
        vBox.getChildren().add(bigCheckBox);
        vBox.getChildren().add(addStuffAtribute("X", arrow.getX() + "", field -> {
            arrow.setActionOffset(Integer.parseInt(field.getText()), arrow.getY());
        }));
        vBox.getChildren().add(addStuffAtribute("Y", arrow.getY() + "", field -> {
            arrow.setActionOffset(arrow.getX(), Integer.parseInt(field.getText()));
        }));
        vBox.getChildren().add(addStuffAtribute("WIDTH", arrow.getWidth() + "", field -> {
            arrow.setActionDimensions(Integer.parseInt(field.getText()), arrow.getHeight());
        }));
        vBox.getChildren().add(addStuffAtribute("HEIGHT", arrow.getHeight() + "", field -> {
            arrow.setActionDimensions(arrow.getWidth(), Integer.parseInt(field.getText()));
        }));
        vBox.getChildren().add(addStuffAtribute("BITMAP X", arrow.getBitmapOffsetX() + "", field -> {
            arrow.setBitmapOffset(Integer.parseInt(field.getText()), arrow.getBitmapOffsetY());
        }));
        vBox.getChildren().add(addStuffAtribute("BITMAP Y", arrow.getBitmapOffsetY() + "", field -> {
            arrow.setBitmapOffset(arrow.getBitmapOffsetX(), Integer.parseInt(field.getText()));
        }));

        hBox = new HBox(20);
        label = new Label("To come");
        final CheckBox toComeBox = new CheckBox();
        toComeBox.setSelected(arrow.isToCome());
        toComeBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                arrow.setIsToCome(toComeBox.isSelected());
            }
        });
        hBox.getChildren().addAll(label, toComeBox);
        vBox.getChildren().add(hBox);

        vBox.getChildren().add(addStuffAtribute("xToCome", arrow.getxToCome() + "", field -> {
            if (controller.getJoe().isEnabled()) {
                controller.getJoe().setPos(Integer.parseInt(field.getText()), controller.getJoe().getPosY());
            }
            arrow.setToCome(Integer.parseInt(field.getText()), arrow.getyToCome());
        }));
        vBox.getChildren().add(addStuffAtribute("yToCome", arrow.getyToCome() + "", field -> {
            if (controller.getJoe().isEnabled()) {
                controller.getJoe().setPos(controller.getJoe().getPosX(), Integer.parseInt(field.getText()));
            }
            arrow.setToCome(arrow.getxToCome(), Integer.parseInt(field.getText()));
        }));
        Button joeBtn = new Button("SET ToCome");
        joeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                arrow.setToCome(controller.getJoe().getPosX(), controller.getJoe().getPosY());
                arrow.setIsToCome(true);
                paramsX = (int) dialog.getX();
                paramsY = (int) dialog.getY();
                dialog.close();
                openArrowParams(arrow);

            }
        });
        vBox.getChildren().add(joeBtn);
        vBox.getChildren().add(addStuffAtribute("xFinal", arrow.getFinalX() + "", field -> {
            if (controller.getJoe().isEnabled()) {
                controller.getJoe().setPos(Integer.parseInt(field.getText()), controller.getJoe().getPosY());
            }
            arrow.setFinal(Integer.parseInt(field.getText()), arrow.getyToCome());
        }));
        vBox.getChildren().add(addStuffAtribute("yFinal", arrow.getFinalY() + "", field -> {
            if (controller.getJoe().isEnabled()) {
                controller.getJoe().setPos(controller.getJoe().getPosX(), Integer.parseInt(field.getText()));
            }
            arrow.setFinal(arrow.getxToCome(), Integer.parseInt(field.getText()));
        }));
        Button joeBtn2 = new Button("SET FinalCome");
        joeBtn2.setOnAction(event -> controller.setArrowFinalCome(arrow, dialog));
        vBox.getChildren().add(joeBtn2);


        Button posButton = new Button("Position " + arrow.getPos());
        posButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String pos = askForPos("Set pos of Arrow!");
                if (pos != null) {
                    arrow.setPos(pos);
                    posButton.setText("Position " + pos);
                }
            }
        });
        vBox.getChildren().add(posButton);

        dialog.show();
    }


    private interface MyEventConsumer {
        default void consume(ActionEvent button, TextField textField) {

        }

        default void consume(KeyEvent enter, TextField textField) {

        }
    }

    private interface RunnableWithTextField {
        void run(TextField field);
    }

    public Arrow createArrow() {
        String id = askForString("Arrow Name", false);
        if (id == null)
            return null;
        String location = askForString("Location ToGo LOCID", false);
        if (location == null)
            return null;
        Boolean big = askForBool("Wanna big arrow?");
        if (big == null)
            return null;
        String pos = askForPos("Set Pos!");
        if (pos == null)
            pos = "LEFT";
        Arrow arrow = new Arrow();
        arrow.setPos(pos);
        arrow.setID(id);
        arrow.setBig(big);
        arrow.setLocation(location);
        return arrow;
    }

    public String askForPos(String quation) {
        Stage dialog = new Stage();
        dialog.setTitle(quation);
        dialog.setResizable(false);
        dialog.setAlwaysOnTop(true);
        VBox vBox = new VBox(20);
        HBox hBox0 = new HBox(20);
        HBox hBox1 = new HBox(20);
        HBox hBox2 = new HBox(20);
        vBox.getChildren().addAll(hBox0, hBox1, hBox2);
        Scene scene = new Scene(vBox);
        dialog.setScene(scene);
        final String[] out = {null};

        Button btn00 = new Button("     ");
        btn00.setDisable(true);
        Button btn01 = new Button(" UP  ");
        btn01.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                out[0] = "UP";
                dialog.close();
            }
        });
        Button btn02 = new Button("     ");
        btn02.setDisable(true);

        Button btn10 = new Button("LEFT ");
        btn10.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                out[0] = "LEFT";
                dialog.close();
            }
        });
        Button btn11 = new Button("     ");
        btn11.setDisable(true);
        Button btn12 = new Button("RIGHT");
        btn12.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                out[0] = "RIGHT";
                dialog.close();
            }
        });

        Button btn20 = new Button("     ");
        btn20.setDisable(true);
        Button btn21 = new Button("DOWN ");
        btn21.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                out[0] = "DOWN";
                dialog.close();
            }
        });
        Button btn22 = new Button("     ");
        btn22.setDisable(true);

        hBox0.getChildren().addAll(btn00, btn01, btn02);
        hBox1.getChildren().addAll(btn10, btn11, btn12);
        hBox2.getChildren().addAll(btn20, btn21, btn22);

        dialog.showAndWait();
        return out[0];
    }

    private double parseDouble(String s) {
        try {
            double d = Double.parseDouble(s);
            return d;
        } catch (Exception e) {
            try {
                int i = Integer.parseInt(s);
                return i;
            }catch (Exception ee){
                return 0;
            }
        }
    }

}
