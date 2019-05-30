package eugenzh.ru.pravradioapp.Models.DataView;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Repository.HandlerRequestItems;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;

abstract public class DateViewCategory extends DataView<Category> implements HandlerRequestItems<Category> {
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

        notifyObserversDateView(RequestResult.REQUEST_RESUTL_SUCC, this.itemsView);
    }

    @Override
    public void onFailRequestResultItem(RequestResult code) {
        notifyObserversDateView(code, this.itemsView);
    }
}
