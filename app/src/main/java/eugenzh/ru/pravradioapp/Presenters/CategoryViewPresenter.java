package eugenzh.ru.pravradioapp.Presenters;


import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesDateViewFactory;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewCategory;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewObserver;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewSubject;
import eugenzh.ru.pravradioapp.Models.Item.Item;

@InjectViewState
public class CategoryViewPresenter extends ItemViewPresenter implements DateViewObserver {
    DateViewSubject subject;
    DateViewCategory repository;

    public CategoryViewPresenter(TypeSourceItems type){
        super(type);

        subject = CategoriesDateViewFactory.getCategories(typeSourceItems);
        repository = CategoriesDateViewFactory.getCategories(typeSourceItems);
        subject.subscripEventUpdateView(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        updateContent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subject.unsubscripEventUpdateView(this);
    }

    @Override
    public void onClick(int position) {
        repository.setSelectedItem(position);
    }

    @Override
    public void updateContent() {
        getViewState().showWaitLoad();
        repository.update();
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void handlerResultRequestPermissionWriteStorage(Context context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getViewState().showWaitLoad();
                repository.update();
            }
            else{
                getViewState().showFailRequestPermissionWriteStorage();
            }
        }
    }

    @Override
    public <T extends Item> void update(RequestResult result, List<T> list) {
        getViewState().hideWaitLoad();
        if (result == RequestResult.REQUEST_RESUTL_SUCC){
            getViewState().updateList((List<Item>)list);
        }
        else if (result == RequestResult.REQUEST_RESULT_FAIL_STORAGE_INACCESSIBLE){
            getViewState().requestPermission(REQUEST_PERMISSION_WRITE_STORAGE_CODE);
        }
    }


}
