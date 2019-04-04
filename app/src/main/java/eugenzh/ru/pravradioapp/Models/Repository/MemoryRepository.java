package eugenzh.ru.pravradioapp.Models.Repository;

import java.util.List;

import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;

public class MemoryRepository implements Repository {

    @Override
    public void requestCategories(HandlerRequestItems handler) {

    }

    @Override
    public void requestPodcasts(long categoryId, HandlerRequestItems handler) {

    }
}
