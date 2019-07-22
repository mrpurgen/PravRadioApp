package eugenzh.ru.pravradioapp.Presenters;

import com.arellomobile.mvp.InjectViewState;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataStore.CategoriesStoreFactory;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreCategory;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStorePodcast;
import eugenzh.ru.pravradioapp.Models.DataStore.PodcastsStoreFactory;
import eugenzh.ru.pravradioapp.View.ListView;

@InjectViewState(view = ListView.class)
public class ListViewPresenter extends MainBasePresenter<ListView> {
    private TypeSourceItems typeSource;

    public ListViewPresenter(){
    }

    public void setTypeSource(TypeSourceItems typeSource){
        this.typeSource = typeSource;
    }

    public String getNameCategoryView(){
        DataStoreCategory repositoryCategory = CategoriesStoreFactory.getCategories(typeSource);
        long categoryID = repositoryCategory.getSelectedItemID();

        return repositoryCategory.getNameItem(categoryID);
    }

    @Override
    public void filter(String textFilter) {
        DataStorePodcast repoPodcast = PodcastsStoreFactory.getPodcasts(typeSource);

        repoPodcast.filter(textFilter);
    }

    public void reverseListItems(){
        DataStorePodcast repoPodcast = PodcastsStoreFactory.getPodcasts(typeSource);
        repoPodcast.swapListItems();
    }
}
