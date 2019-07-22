package eugenzh.ru.pravradioapp.Presenters;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataStore.CategoriesStoreFactory;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreCategory;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreFacade;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreFacadeImp;
import eugenzh.ru.pravradioapp.Models.DataStore.Observer.DataStoreObserver;
import eugenzh.ru.pravradioapp.Models.Item.Item;

@InjectViewState
public class CategoryViewPresenter extends ItemViewPresenter implements DataStoreObserver {
    DataStoreCategory mDataStoreCategory;
    Context mContex;
    DataStoreFacade mDataStore;


    public CategoryViewPresenter(TypeSourceItems type, Context ctx){
        super(type);

        mDataStore = new DataStoreFacadeImp();
        mDataStoreCategory = mDataStore.getDataStoreCategory(type);

        mDataStoreCategory = CategoriesStoreFactory.getCategories(typeSourceItems);
        mDataStoreCategory.subscripEventUpdateView(this);

        mContex = ctx;
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        updateContent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataStoreCategory.unsubscripEventUpdateView(this);
    }

    @Override
    public void onClick(int position) {
        mDataStoreCategory.setSelectedItem(position);
    }

    @Override
    public void updateContent() {
        getViewState().showWaitLoad();
        mDataStoreCategory.update();
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    @Override
    public void handlerResultRequestPermissionWriteStorage(Context context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getViewState().showWaitLoad();
                mDataStoreCategory.update();
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
        else if (result == RequestResult.REQUEST_RESULT_FAIL_RESOURSE_NOT_CREATED){
            if(!checkPermissionWriteStorage(mContex)) {
                getViewState().requestPermission(REQUEST_PERMISSION_WRITE_STORAGE_CODE);
            }
        }
    }

    private boolean checkPermissionWriteStorage(Context context){
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }
}
