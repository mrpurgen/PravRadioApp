package eugenzh.ru.pravradiopodcast.Presenters;

import android.content.Context;

import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradiopodcast.Common.CustomAppThemes;
import eugenzh.ru.pravradiopodcast.Models.Settings.SettingsViews;
import eugenzh.ru.pravradiopodcast.View.ViewPager.PageHostView;

public class PageHostPresenter extends MvpPresenter<PageHostView> {
    public CustomAppThemes getCurrentTheme(Context ctx){
        SettingsViews settings = new SettingsViews(ctx);

        return settings.getCurrentTheme();
    }
}
