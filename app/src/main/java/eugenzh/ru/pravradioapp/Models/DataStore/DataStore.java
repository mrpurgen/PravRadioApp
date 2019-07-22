package eugenzh.ru.pravradioapp.Models.DataStore;

import java.util.ArrayList;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.DataStore.Observer.DataStoreObserver;
import eugenzh.ru.pravradioapp.Models.DataStore.Observer.DataStoreSubject;
import eugenzh.ru.pravradioapp.Models.DataStore.Observer.SelectedItemObserver;
import eugenzh.ru.pravradioapp.Models.Item.Item;

abstract public class DataStore<T extends Item> implements DataStoreSubject {
    protected List<DataStoreObserver> observersUpdateDateView = new ArrayList<>();
    protected List<SelectedItemObserver> observersSelectedItem = new ArrayList<>();

    protected List<T> itemsSrc = new ArrayList<>();
    protected List<T> itemsView = new ArrayList();

    boolean isNotifyObserversDataStore = false;

    long selectedItemID = 0;

    public void setSelectedItem(int position){
        Item item = itemsView.get(position);
        selectedItemID = item.getId();
        notifyObserversSelectedItem(selectedItemID);
    }

    public void setSelectedItem(long id){
        selectedItemID = id;
        notifyObserversSelectedItem(selectedItemID);
    }

    public long getSelectedItemID(){
        return selectedItemID;
    }

    public List<T> getItemsSrc(){
        return itemsSrc;
    }

    public T getItem(int position) { return itemsView.get(position); }

    public T getItemToId(long id){
        for (T item: itemsSrc){
            Long currentid = item.getId();
            if (currentid.equals(id)){
                return item;
            }
        }
        /// TODO: throw exception
        return null;
    }

    public String getNameItem(long id){
        T item = getItemToId(id);
        return item.getName();
    }

    public String getURL(long id){
        T item = getItemToId(id);
        return item.getUrl();
    }

    public int getPositionViewListById(long id){
        for(Item item: itemsView){
            if(item.getId().equals(id)){
                return itemsView.indexOf(item);
            }
        }
        return -1;
    }

    public void filter(String filterTetx){
        itemsView.clear();

        String text = filterTetx.toLowerCase();
        for(T item: itemsSrc){
            String name = item.getName().toLowerCase();
            if (name.contains(text)){
                itemsView.add(item);
            }
        }
        notifyObserversDataStore(RequestResult.REQUEST_RESUTL_SUCC, itemsView);
    }

    @Override
    public <T extends Item> void notifyObserversDataStore(RequestResult result, List<T> list) {
        isNotifyObserversDataStore = true;
        for (DataStoreObserver observer: observersUpdateDateView){
            observer.update(result, list);
        }
        isNotifyObserversDataStore = false;
    }

    @Override
    public void notifyObserversSelectedItem(long id) {
        for (SelectedItemObserver observer: observersSelectedItem){
            observer.update(selectedItemID);


        }
    }

    @Override
    public void subscripEventUpdateView(DataStoreObserver observer) {
        observersUpdateDateView.add(observer);
    }

    @Override
    public void subscripEventUpdateSelectedItem(SelectedItemObserver observer) {
        observersSelectedItem.add(observer);
    }

    @Override
    public void unsubscripEventUpdateSelectedItem(SelectedItemObserver observer) {
        observersSelectedItem.remove(observer);
    }

    @Override
    public void unsubscripEventUpdateView(DataStoreObserver observer) {
        observersUpdateDateView.remove(observer);
    }

}
