package eugenzh.ru.pravradioapp.Presenters;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesDateViewFactory;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewCategory;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewPodcast;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewObserver;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewSubject;
import eugenzh.ru.pravradioapp.Models.DataView.PodcastsDateViewFactory;
import eugenzh.ru.pravradioapp.Models.Item.Item;

@InjectViewState
public class PodcastViewPresenter extends ItemViewPresenter implements DateViewObserver {
    DateViewSubject subject;
    DateViewPodcast repository;
    long categoryID = 0;

    public PodcastViewPresenter(TypeSourceItems type) {
        super(type);

        subject = PodcastsDateViewFactory.getPodcasts(typeSourceItems);
        repository = PodcastsDateViewFactory.getPodcasts(typeSourceItems);
        subject.subscripEventUpdateView(this);

        DateViewCategory repositoryCategory = CategoriesDateViewFactory.getCategories(typeSourceItems);
        categoryID = repositoryCategory.getSelectedItemID();

    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        updateContent();
    }


    @Override
    public void onDestroy() {
        subject.unsubscripEventUpdateView(this);
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void updateContent() {
        getViewState().showWaitLoad();
        repository.update(categoryID);
    }

    @Override
    public <T extends Item> void update(RequestResult result, List<T> list) {
        if (result == RequestResult.REQUEST_RESUTL_SUCC) {
            getViewState().hideWaitLoad();
            getViewState().updateList((List<Item>)list);
        }
    }
}
