package eugenzh.ru.pravradioapp.View;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import eugenzh.ru.pravradioapp.View.FragmentList.AddToEndSingleByTagStateStrategy;

public interface PlayerControlView extends MvpView {
    String TAG_STATE_CONTROL_PANEL = "TAG_STATE_CONTROL_PANEL";
    String TAG_SHOW_CONTROL_PANEL = "TAG_SHOW_CONTROL_PANEL";

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = TAG_STATE_CONTROL_PANEL)
    void playView();
    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = TAG_STATE_CONTROL_PANEL)
    void pauseView();
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setTrackName(String trackName);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setTrackDuration(long duration);
    @StateStrategyType(AddToEndSingleStrategy.class)
    void setTrackPosition(long position);
    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = TAG_SHOW_CONTROL_PANEL)
    void hidePanel();
    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = TAG_SHOW_CONTROL_PANEL)
    void showPanel();
    @StateStrategyType(OneExecutionStateStrategy.class)
    void setPosiotionProgressBar(long position);


}
