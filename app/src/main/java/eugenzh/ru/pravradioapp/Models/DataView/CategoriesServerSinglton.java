package eugenzh.ru.pravradioapp.Models.DataView;

import java.util.ArrayList;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewObserver;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewSubject;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Item;
import eugenzh.ru.pravradioapp.Models.Repository.HandlerRequestItems;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;
import eugenzh.ru.pravradioapp.Models.Repository.ServerRepository;

public class CategoriesServerSinglton extends DateViewCategory implements HandlerRequestItems<Category>,
                                                                          DateViewSubject
{
    Repository repository = new ServerRepository();
    private List<DateViewObserver> observers = new ArrayList<>();

    private static final CategoriesServerSinglton ourInstance = new CategoriesServerSinglton();

    public static CategoriesServerSinglton getInstance() {
        return ourInstance;
    }

    private CategoriesServerSinglton() {
        items = new ArrayList<>();
    }

    @Override
    public void update() {
        repository.requestCategories(this);
    }

    @Override
    public void onSuccRequestItems(List<Category> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyObserver(RequestResult.REQUEST_RESUTL_SUCC, this.items);
    }

    @Override
    public void subscrip(DateViewObserver observer) {
        observers.add(observer);
    }

    @Override
    public void unsubscrip(DateViewObserver observer) {
        observers.remove(observer);
    }

    @Override
    public <T extends Item> void notifyObserver(RequestResult result, List<T> list) {
        for (DateViewObserver observer: observers){
            observer.update(result, list);
        }
    }
}
