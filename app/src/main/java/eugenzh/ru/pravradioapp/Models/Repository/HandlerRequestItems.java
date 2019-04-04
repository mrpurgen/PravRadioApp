package eugenzh.ru.pravradioapp.Models.Repository;

import java.util.List;

import eugenzh.ru.pravradioapp.Models.Item.Item;

public interface HandlerRequestItems<T extends Item> {
    void onSuccRequestItems(List<T> items);
}
