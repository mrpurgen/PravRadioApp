package eugenzh.ru.pravradiopodcast.Presenters;

import android.content.Context;

import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradiopodcast.Common.CustomAppThemes;
import eugenzh.ru.pravradiopodcast.Models.Settings.SettingsViews;
import eugenzh.ru.pravradiopodcast.View.MainBaseView;

abstract public class MainBasePresenter<T extends MainBaseView> extends MvpPresenter<T> {
     abstract public void filter(String textFilter);

    public CustomAppThemes getCurrentTheme(Context ctx){
        SettingsViews settings = new SettingsViews(ctx);

        return settings.getCurrentTheme();
    }
}
