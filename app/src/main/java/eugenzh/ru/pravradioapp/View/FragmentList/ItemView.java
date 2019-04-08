package eugenzh.ru.pravradioapp.View.FragmentList;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import eugenzh.ru.pravradioapp.Models.Item.Item;

public interface ItemView extends MvpView {
    String TAG_SHOW_LOAD_LIST = "tagShowLoadList";

    @StateStrategyType(AddToEndSingleStrategy.class)
    void updateList(List<Item> list);
    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = TAG_SHOW_LOAD_LIST)
    void showWaitLoad();
    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = TAG_SHOW_LOAD_LIST)
    void hideWaitLoad();
}
