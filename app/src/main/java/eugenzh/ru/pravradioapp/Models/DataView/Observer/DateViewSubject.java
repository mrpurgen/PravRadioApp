package eugenzh.ru.pravradioapp.Models.DataView.Observer;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.Item.Item;

public interface DateViewSubject {
    void subscrip(DateViewObserver observer);
    void unsubscrip(DateViewObserver observer);
    <T extends Item> void notifyObserver(RequestResult result, List<T> list);
}
