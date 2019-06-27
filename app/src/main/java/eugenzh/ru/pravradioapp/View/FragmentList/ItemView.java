package eugenzh.ru.pravradioapp.View.FragmentList;

import android.view.View;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
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
    @StateStrategyType(SkipStrategy.class)
    void showPopupPodcast(View holder, int position);
    @StateStrategyType(OneExecutionStateStrategy.class)
    void requestPermission(int requestCode);
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showFailRequestPermissionWriteStorage();
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showToast(String text);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void updatePlayablePosition(int position);

}