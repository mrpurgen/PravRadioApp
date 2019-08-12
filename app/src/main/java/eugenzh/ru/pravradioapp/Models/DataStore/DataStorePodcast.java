package eugenzh.ru.pravradioapp.Models.DataStore;

import android.content.Context;

import java.util.Collections;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;
import eugenzh.ru.pravradioapp.Models.Repository.HandlerRequestItems;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;

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
