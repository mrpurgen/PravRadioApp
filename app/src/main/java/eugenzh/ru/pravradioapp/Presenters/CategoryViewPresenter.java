package eugenzh.ru.pravradioapp.Presenters;


import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesServerSinglton;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewCategory;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewObserver;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewSubject;
import eugenzh.ru.pravradioapp.Models.Item.Item;

@InjectViewState
public class CategoryViewPresenter extends ItemViewPresenter implements DateViewObserver {


    public CategoryViewPresenter(){
        DateViewSubject subject = CategoriesServerSinglton.getInstance();
        subject.subscrip(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DateViewSubject subject = CategoriesServerSinglton.getInstance();
        subject.unsubscrip(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        updateContent();
    }

    @Override
    public void onClick() {

    }

    @Override
    public void updateContent() {
        DateViewCategory repository = CategoriesServerSinglton.getInstance();
        repository.update();
    }

    @Override
    public <T extends Item> void update(RequestResult result, List<T> list) {
        if (result == RequestResult.REQUEST_RESUTL_SUCC){
            getViewState().updateList((List<Item>)list);
        }
    }
}
