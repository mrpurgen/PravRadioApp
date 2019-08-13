package eugenzh.ru.pravradiopodcast.Presenters;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;

import eugenzh.ru.pravradiopodcast.Models.DataStore.CategoriesMemoryStoreSinglton;
import eugenzh.ru.pravradiopodcast.Models.DataStore.CategoriesServerStoreSinglton;
import eugenzh.ru.pravradiopodcast.View.MainView;

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
