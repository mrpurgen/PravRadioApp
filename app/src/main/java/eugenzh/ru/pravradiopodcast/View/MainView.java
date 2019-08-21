package eugenzh.ru.pravradiopodcast.View;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface MainView extends MainBaseView {
    @StateStrategyType(OneExecutionStateStrategy.class)
    void showSettingsView();
}
