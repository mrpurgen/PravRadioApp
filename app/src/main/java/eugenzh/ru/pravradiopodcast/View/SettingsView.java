package eugenzh.ru.pravradiopodcast.View;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

public interface SettingsView extends MvpView {
    @StateStrategyType(OneExecutionStateStrategy.class)
    void updateTheme();
}
