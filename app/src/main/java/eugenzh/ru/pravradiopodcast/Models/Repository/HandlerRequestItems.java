package eugenzh.ru.pravradiopodcast.Models.Repository;

import java.util.List;

import eugenzh.ru.pravradiopodcast.Common.RequestResult;
import eugenzh.ru.pravradiopodcast.Models.Item.Item;

public interface HandlerRequestItems<T extends Item> {
    void onSuccRequestItems(List<T> items);
    void onFailRequestResultItem(RequestResult code);
}
