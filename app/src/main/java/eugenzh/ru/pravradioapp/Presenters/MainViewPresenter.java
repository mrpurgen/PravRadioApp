package eugenzh.ru.pravradioapp.Presenters;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;

import eugenzh.ru.pravradioapp.Models.DataStore.CategoriesMemoryStoreSinglton;
import eugenzh.ru.pravradioapp.Models.DataStore.CategoriesServerStoreSinglton;
import eugenzh.ru.pravradioapp.View.MainView;

@InjectViewState(view = MainView.class)
public class MainViewPresenter extends MainBasePresenter<MainView> {

    CategoriesMemoryStoreSinglton repoMemory;
    CategoriesServerStoreSinglton repoServer;
    Context mApplicationContext;

    public MainViewPresenter(Context appCtx){
        repoMemory = CategoriesMemoryStoreSinglton.getInstance();
        repoServer = CategoriesServerStoreSinglton.getInstance();
        mApplicationContext = appCtx;
    }

    @Override
    public void filter(String textFilter){
        repoMemory.filter(textFilter);
        repoServer.filter(textFilter);
    }
}
