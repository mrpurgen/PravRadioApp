package eugenzh.ru.pravradioapp.Presenters;

import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.View.FragmentList.ItemView;


abstract public class ItemViewPresenter extends MvpPresenter<ItemView>  {
    TypeSourceItems typeSourceItems;

    public void setTypeSourceItems(TypeSourceItems type){
        typeSourceItems = type;
    }

    public ItemViewPresenter(TypeSourceItems type){
        typeSourceItems = type;
    }

    abstract public void onClick(int position);
    abstract public void updateContent();
}
