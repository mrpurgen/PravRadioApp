package eugenzh.ru.pravradioapp.Presenters;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.arellomobile.mvp.InjectViewState;

import eugenzh.ru.pravradioapp.Models.DataView.CategoriesMemorySinglton;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesServerSinglton;
import eugenzh.ru.pravradioapp.Services.PlaybackService;
import eugenzh.ru.pravradioapp.Services.PlaybackServiceConnectionManager;
import eugenzh.ru.pravradioapp.View.MainView;

@InjectViewState(view = MainView.class)
public class MainViewPresenter extends MainBasePresenter<MainView> {

    CategoriesMemorySinglton repoMemory;
    CategoriesServerSinglton repoServer;
    Context mApplicationContext;

    public MainViewPresenter(Context appCtx){
        repoMemory = CategoriesMemorySinglton.getInstance();
        repoServer = CategoriesServerSinglton.getInstance();
        mApplicationContext = appCtx;
    }

    @Override
    public void filter(String textFilter){
        repoMemory.filter(textFilter);
        repoServer.filter(textFilter);
    }
}
