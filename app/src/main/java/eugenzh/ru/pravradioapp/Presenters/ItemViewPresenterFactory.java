package eugenzh.ru.pravradioapp.Presenters;

import eugenzh.ru.pravradioapp.Common.TypeItems;

public class ItemViewPresenterFactory {
    static public ItemViewPresenter getPresenter(TypeItems ti){
        switch (ti){
            case TYPE_ITEM_PODCAST:
                return new PodcastViewPresenter();
            case TYPE_ITEM_CATEGORY:
                return new CategoryViewPresenter();
        }
        //TODO: add throw execption
        return null;
    }
}
