package eugenzh.ru.pravradiopodcast.Presenters;

import android.content.Context;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradiopodcast.Common.CustomAppThemes;
import eugenzh.ru.pravradiopodcast.Models.Settings.SettingsViews;
import eugenzh.ru.pravradiopodcast.View.SettingsView;

@InjectViewState(view = SettingsView.class)
public class SettingsPresenter extends MvpPresenter<SettingsView> {

    public void switchDarkTheme(boolean state, Context ctx){
        SettingsViews settings = new SettingsViews(ctx);

        if (state == true && settings.getCurrentTheme() == CustomAppThemes.DEFAULT_THEME){
            settings.setCurrentTheme(CustomAppThemes.DARK_THEME);
            getViewState().updateTheme();
        }

        else if (state == false && settings.getCurrentTheme() == CustomAppThemes.DARK_THEME){
            settings.setCurrentTheme(CustomAppThemes.DEFAULT_THEME);
            getViewState().updateTheme();
        }
    }

    public CustomAppThemes getCurrentTheme(Context ctx){
        SettingsViews settings = new SettingsViews(ctx);

        return settings.getCurrentTheme();
    }
}
