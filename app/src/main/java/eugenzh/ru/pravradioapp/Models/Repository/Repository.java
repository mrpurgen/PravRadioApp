package eugenzh.ru.pravradioapp.Models.Repository;

import eugenzh.ru.pravradioapp.Models.Item.Category;

public interface Repository {
    void requestCategories(HandlerRequestItems handler);
    void requestPodcasts(long categoryId, HandlerRequestItems handler);
}
