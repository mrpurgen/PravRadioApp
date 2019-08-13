package eugenzh.ru.pravradiopodcast.Models.DataStore.Observer;

import java.util.List;

import eugenzh.ru.pravradiopodcast.Common.RequestResult;
import eugenzh.ru.pravradiopodcast.Models.Item.Item;

public interface DataStoreObserver {
    <T extends Item> void update(RequestResult result, List<T> list);
}
