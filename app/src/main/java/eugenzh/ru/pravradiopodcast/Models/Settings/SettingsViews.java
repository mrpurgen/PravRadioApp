package eugenzh.ru.pravradiopodcast.Models.Settings;

import android.content.Context;

import eugenzh.ru.pravradiopodcast.Common.CustomAppThemes;
import eugenzh.ru.pravradiopodcast.Preferences.PreferencesSetiingViewManager;
import eugenzh.ru.pravradiopodcast.R;

public class SettingsViews {

    PreferencesSetiingViewManager mPrefManager;

    public SettingsViews(Context ctx){
        mPrefManager = new PreferencesSetiingViewManager(ctx);
    }

    public void setCurrentTheme(CustomAppThemes theme){
        mPrefManager.saveCurrentTheme(theme.getValue());
    }

    public CustomAppThemes getCurrentTheme(){
        int id = mPrefManager.readCurrentTheme();

        return (id == -1) ? CustomAppThemes.DEFAULT_THEME : CustomAppThemes.valueOf(id);
    }
}
