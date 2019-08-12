package eugenzh.ru.pravradioapp.Models.DataStore;

import android.app.Application;
import android.content.Context;
import android.support.v4.content.ContextCompat;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Repository.HandlerRequestItems;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;
import eugenzh.ru.pravradioapp.Preferences.PreferencesPlaybackManager;

abstract public class DataStoreCategory extends DataStore<Category> implements HandlerRequestItems<Category> {
    abstract Repository createRepositoryLoader();

    public void update()
    {
        Repository repository = createRepositoryLoader();
        repository.requestCategories(this);
    }

    @Override
    public void onSuccRequestItems(List<Category> items) {
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
    protected Category createDefaultItem() {
        return new Category("undefined", 0L);
    }

}
