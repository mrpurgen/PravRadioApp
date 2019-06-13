package eugenzh.ru.pravradioapp.Presenters;

import android.content.Context;

import eugenzh.ru.pravradioapp.Common.TypeItems;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;

public class ItemViewPresenterFactory {
    static public ItemViewPresenter getPresenter(TypeItems ti, TypeSourceItems ts, Context ctx){
        switch (ti){
            case TYPE_ITEM_PODCAST:
                return new PodcastViewPresenter(ts, ctx);
            case TYPE_ITEM_CATEGORY:
                return new CategoryViewPresenter(ts, ctx);
        }
        //TODO: add throw execption
        return null;
    }
}
