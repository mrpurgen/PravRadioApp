package eugenzh.ru.pravradioapp.Presenters;

import eugenzh.ru.pravradioapp.Common.TypeItems;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;

public class ItemViewPresenterFactory {
    static public ItemViewPresenter getPresenter(TypeItems ti, TypeSourceItems ts){
        switch (ti){
            case TYPE_ITEM_PODCAST:
                return new PodcastViewPresenter(ts);
            case TYPE_ITEM_CATEGORY:
                return new CategoryViewPresenter(ts);
        }
        //TODO: add throw execption
        return null;
    }
}
