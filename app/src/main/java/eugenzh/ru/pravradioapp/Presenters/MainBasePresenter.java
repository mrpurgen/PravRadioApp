package eugenzh.ru.pravradioapp.Presenters;

import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradioapp.View.MainBaseView;

abstract public class MainBasePresenter<T extends MainBaseView> extends MvpPresenter<T> {
     abstract public void filter(String textFilter);
}
