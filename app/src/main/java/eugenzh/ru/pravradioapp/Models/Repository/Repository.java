package eugenzh.ru.pravradioapp.Models.Repository;

public interface Repository {
    void requestCategories(HandlerRequestItems handler);
    void requestPodcasts(long categoryId, HandlerRequestItems handler);

}
