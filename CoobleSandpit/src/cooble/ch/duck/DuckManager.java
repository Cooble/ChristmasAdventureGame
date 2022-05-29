package cooble.ch.duck;

import cooble.ch.canvas.Renderer;
import javafx.application.Platform;
import javafx.scene.control.ListView;

import java.util.ArrayList;

/**
 * Created by Matej on 18.5.2017.
 */
public class DuckManager {
    private ArrayList<ListViewItem> listViewItems = new ArrayList<>();
    private int currentSelectedIndex;
    private ListView<String> listView;
    private Renderer renderer;


    public DuckManager(ListView<String> listView, Renderer renderer) {
        this.listView = listView;
        this.renderer = renderer;
    }

    public ListViewItem getCurrentDuck() {

        if (listViewItems.size() == 0 || currentSelectedIndex == -1 || currentSelectedIndex > listViewItems.size() - 1)
            return null;
        return listViewItems.get(currentSelectedIndex);
    }

    public void addDuck(ListViewItem listViewItem) {
        if (getCurrentDuck() != null)
            getCurrentDuck().onDeselected();
        listViewItems.add(listViewItem);
        refreshList();
        Platform.runLater(() -> listView.getSelectionModel().select(listViewItems.size() - 1));
        currentSelectedIndex = listViewItems.size() - 1;
        getCurrentDuck().onSelected();
        renderer.addBitmapProvider(listViewItem);

    }

    public void refreshCurrentDuck() {
        if (listView.getSelectionModel().getSelectedIndex() != currentSelectedIndex) {
            if (getCurrentDuck() != null)
                getCurrentDuck().onDeselected();
            currentSelectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (getCurrentDuck() != null) {
                getCurrentDuck().onSelected();
            }
        }
        //refreshList();
    }

    public void removeCurrentDuck() {
        renderer.removeBitmapProvider(listViewItems.get(currentSelectedIndex));
        listViewItems.remove(currentSelectedIndex);
        refreshList();
    }

    public void refreshList() {
        ArrayList<ListViewItem> newList = new ArrayList<>();
        for (ListViewItem listViewItem1 : listViewItems) {
            if (listViewItem1 != null)
                newList.add(listViewItem1);
        }
        listViewItems = newList;
        Platform.runLater(() -> {
            listView.getItems().clear();
            //listView.getSelectionModel().clearSelection();
            for (ListViewItem listViewItem : listViewItems) {
                listView.getItems().add(listViewItem.getID());
            }
            listView.getSelectionModel().select(currentSelectedIndex);

        });
    }

    public ArrayList<ListViewItem> getList() {
        return listViewItems;
    }

    public void tick() {
        for(ListViewItem item:listViewItems){
            if(item!=null)
                item.tick();
        }
    }
}
