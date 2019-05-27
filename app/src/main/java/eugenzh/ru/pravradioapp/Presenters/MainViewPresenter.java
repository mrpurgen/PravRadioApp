package eugenzh.ru.pravradioapp.Presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradioapp.View.MainView;

@InjectViewState
public class MainViewPresenter extends MvpPresenter<MainView> {

    public MainViewPresenter(){
        int a = 1;

        a++;
    }
}
