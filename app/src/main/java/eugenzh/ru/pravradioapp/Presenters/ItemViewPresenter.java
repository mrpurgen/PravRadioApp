package eugenzh.ru.pravradioapp.Presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradioapp.View.FragmentList.ItemView;


abstract public class ItemViewPresenter extends MvpPresenter<ItemView> {
    abstract public void onClick();
    abstract public void updateContent();
}
