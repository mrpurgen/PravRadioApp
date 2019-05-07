package eugenzh.ru.pravradioapp.Models.DataView;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;
import eugenzh.ru.pravradioapp.Models.Repository.HandlerRequestItems;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;

abstract public class DateViewPodcast extends DataView<Podcast> implements HandlerRequestItems<Podcast> {

    abstract Repository createRepositoryLoader();

    public void update(long categoryId){
        Repository repository = createRepositoryLoader();
        repository.requestPodcasts(categoryId, this);
    }

    @Override
    public void onSuccRequestItems(List<Podcast> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyObserversDateView(RequestResult.REQUEST_RESUTL_SUCC, this.items);
    }

    @Override
    public void onFailRequestResultItem(RequestResult code) {

    }
}
