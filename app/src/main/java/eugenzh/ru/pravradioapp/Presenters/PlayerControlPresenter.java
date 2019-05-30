package eugenzh.ru.pravradioapp.Presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.InjectPresenter;

import eugenzh.ru.pravradioapp.View.PlayerControlCustomView;
import eugenzh.ru.pravradioapp.View.PlayerControlView;

@InjectViewState
public class PlayerControlPresenter extends MvpPresenter<PlayerControlView> {
    public void onPlayPressed(){
        int a = 1;
        int b;

        b = a + 2;
    }
}
