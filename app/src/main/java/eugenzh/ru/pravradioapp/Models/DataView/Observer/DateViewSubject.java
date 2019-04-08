package eugenzh.ru.pravradioapp.Models.DataView.Observer;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.Item.Item;

public interface DateViewSubject {

    void subscripEventUpdateView(DateViewObserver observer);
    void unsubscripEventUpdateView(DateViewObserver observer);

    void subscripEventUpdateSelectedItem(SelectedItemObserver observer);
    void unsubscripEventUpdateSelectedItem(SelectedItemObserver observer);

    <T extends Item> void notifyObserversDateView(RequestResult result, List<T> list);
    void notifyObserversSelectedItem(long id);
}
