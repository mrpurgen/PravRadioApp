package eugenzh.ru.pravradiopodcast.Models.DataStore;

import java.util.Collections;
import java.util.List;

import eugenzh.ru.pravradiopodcast.Common.RequestResult;
import eugenzh.ru.pravradiopodcast.Models.Item.Podcast;
import eugenzh.ru.pravradiopodcast.Models.Repository.HandlerRequestItems;
import eugenzh.ru.pravradiopodcast.Models.Repository.Repository;

abstract public class DataStorePodcast extends DataStore<Podcast> implements HandlerRequestItems<Podcast> {
    abstract Repository createRepositoryLoader();

    public void update(long categoryId){
        Repository repository = createRepositoryLoader();
        repository.requestPodcasts(categoryId, this);
    }

    public void swapListItems(){
        Collections.reverse(this.itemsView);
        notifyObserversDataStore(RequestResult.REQUEST_RESUTL_SUCC, this.itemsView);
    }
    
    @Override
    public void onSuccRequestItems(List<Podcast> items) {
        this.itemsSrc.clear();
        this.itemsView.clear();

        this.itemsSrc.addAll(items);
        this.itemsView.addAll(items);

        notifyObserversDataStore(RequestResult.REQUEST_RESUTL_SUCC, this.itemsView);
    }

    @Override
    public void onFailRequestResultItem(RequestResult code) {
        notifyObserversDataStore(code, this.itemsView);
    }

    @Override
    protected Podcast createDefaultItem() {
        return new Podcast(0L, "undefined");
    }

}
