package eugenzh.ru.pravradioapp.Models.DataView;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Repository.HandlerRequestItems;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;
import eugenzh.ru.pravradioapp.Models.Repository.ServerRepository;

public class CategoriesServerSinglton extends DateViewCategory implements HandlerRequestItems<Category>

{
    private Repository repository = new ServerRepository();

    private static final CategoriesServerSinglton ourInstance = new CategoriesServerSinglton();

    public static CategoriesServerSinglton getInstance() {
        return ourInstance;
    }

    private CategoriesServerSinglton() {

    }

    @Override
    public void update() {
        repository.requestCategories(this);
    }

    @Override
    public void onSuccRequestItems(List<Category> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyObserversDateView(RequestResult.REQUEST_RESUTL_SUCC, this.items);
    }
}
