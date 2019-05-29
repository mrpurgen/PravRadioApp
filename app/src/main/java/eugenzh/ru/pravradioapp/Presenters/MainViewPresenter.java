package eugenzh.ru.pravradioapp.Presenters;

import com.arellomobile.mvp.InjectViewState;

import eugenzh.ru.pravradioapp.Models.DataView.CategoriesMemorySinglton;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesServerSinglton;
import eugenzh.ru.pravradioapp.View.MainView;

@InjectViewState(view = MainView.class)
public class MainViewPresenter extends MainBasePresenter<MainView> {

    CategoriesMemorySinglton repoMemory;
    CategoriesServerSinglton repoServer;

    public MainViewPresenter(){
        repoMemory = CategoriesMemorySinglton.getInstance();
        repoServer = CategoriesServerSinglton.getInstance();
    }

    @Override
    public void filter(String textFilter){
        repoMemory.filter(textFilter);
        repoServer.filter(textFilter);
    }
}
