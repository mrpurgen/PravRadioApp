package eugenzh.ru.pravradioapp.Models.DataStore.Observer;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.Item.Item;

public interface DataStoreSubject {

    void subscripEventUpdateView(DataStoreObserver observer);
    void unsubscripEventUpdateView(DataStoreObserver observer);

    void subscripEventUpdateSelectedItem(SelectedItemObserver observer);
    void unsubscripEventUpdateSelectedItem(SelectedItemObserver observer);

    <T extends Item> void notifyObserversDataStore(RequestResult result, List<T> list);
    void notifyObserversSelectedItem(long id);
}
