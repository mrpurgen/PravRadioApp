package eugenzh.ru.pravradioapp.Models.DataView;

import java.util.ArrayList;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewObserver;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewSubject;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.SelectedItemObserver;
import eugenzh.ru.pravradioapp.Models.Item.Item;

abstract public class DataView<T extends Item> implements DateViewSubject {
    protected List<DateViewObserver> observersUpdateDateView = new ArrayList<>();
    protected List<SelectedItemObserver> observersSelectedItem = new ArrayList<>();

    protected List<T> itemsSrc = new ArrayList<>();
    protected List<T> itemsView = new ArrayList();

    long selectedItemID = 0;

    public void setSelectedItem(int position){
        Item item = itemsView.get(position);
        selectedItemID = item.getId();
        notifyObserversSelectedItem(selectedItemID);
    }

    public long getSelectedItemID(){
        return selectedItemID;
    }

    public List<T> getItemsSrc(){
        return itemsSrc;
    }

    public T getItem(int position) { return itemsSrc.get(position); }

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

    public void filter(String filterTetx){
        itemsView.clear();

        String text = filterTetx.toLowerCase();
        for(T item: itemsSrc){
            String name = item.getName().toLowerCase();
            if (name.contains(text)){
                itemsView.add(item);
            }
        }
        notifyObserversDateView(RequestResult.REQUEST_RESUTL_SUCC, itemsView);
    }

    @Override
    public <T extends Item> void notifyObserversDateView(RequestResult result, List<T> list) {
        for (DateViewObserver observer: observersUpdateDateView){
            observer.update(result, list);
        }
    }

    @Override
    public void notifyObserversSelectedItem(long id) {
        for (SelectedItemObserver observer: observersSelectedItem){
            observer.update(selectedItemID);
        }
    }

    @Override
    public void subscripEventUpdateView(DateViewObserver observer) {
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
    public void unsubscripEventUpdateView(DateViewObserver observer) {
        observersUpdateDateView.remove(observer);
    }

}
