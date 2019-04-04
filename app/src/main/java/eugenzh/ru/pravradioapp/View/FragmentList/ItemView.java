package eugenzh.ru.pravradioapp.View.FragmentList;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import eugenzh.ru.pravradioapp.Models.Item.Item;

public interface ItemView extends MvpView {
    @StateStrategyType(AddToEndSingleStrategy.class)
    void onClick(long duration);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void updateList(List<Item> list);
}
