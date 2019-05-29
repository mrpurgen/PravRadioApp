package eugenzh.ru.pravradioapp.Presenters;

import com.arellomobile.mvp.InjectViewState;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesDateViewFactory;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewCategory;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewPodcast;
import eugenzh.ru.pravradioapp.Models.DataView.PodcastsDateViewFactory;
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
        DateViewCategory repositoryCategory = CategoriesDateViewFactory.getCategories(typeSource);
        long categoryID = repositoryCategory.getSelectedItemID();

        return repositoryCategory.getNameItem(categoryID);
    }

    @Override
    public void filter(String textFilter) {
        DateViewPodcast repoPodcast = PodcastsDateViewFactory.getPodcasts(typeSource);

        repoPodcast.filter(textFilter);
    }

    public void reverseListItems(){
        DateViewPodcast repoPodcast = PodcastsDateViewFactory.getPodcasts(typeSource);
        repoPodcast.swapListItems();
    }
}
