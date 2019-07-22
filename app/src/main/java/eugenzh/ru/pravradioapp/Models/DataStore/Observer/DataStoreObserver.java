package eugenzh.ru.pravradioapp.Models.DataStore.Observer;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.Item.Item;

public interface DataStoreObserver {
    <T extends Item> void update(RequestResult result, List<T> list);
}
