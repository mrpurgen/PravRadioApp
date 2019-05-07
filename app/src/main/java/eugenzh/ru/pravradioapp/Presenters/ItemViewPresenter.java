package eugenzh.ru.pravradioapp.Presenters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.View.FragmentList.ItemView;


abstract public class ItemViewPresenter extends MvpPresenter<ItemView>  {
    protected static final int REQUEST_PERMISSION_WRITE_STORAGE_CODE = 1;

    TypeSourceItems typeSourceItems;

    public void setTypeSourceItems(TypeSourceItems type){
        typeSourceItems = type;
    }

    public ItemViewPresenter(TypeSourceItems type){
        typeSourceItems = type;
    }

    abstract public void onClick(int position);
    abstract public void updateContent();
    abstract public void onLongClick(View view, int position);

    abstract public void handlerResultRequestPermissionWriteStorage(Context context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
