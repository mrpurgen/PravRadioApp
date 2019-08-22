package eugenzh.ru.pravradiopodcast.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesSetiingViewManager extends PreferencesManagerBase{
    private static String PREF_FILE_NAME = "SETTINGS_VIEW";

    private static String PREF_THEME = "PREF_THEME";

    public PreferencesSetiingViewManager(Context ctx) {
        super(PREF_FILE_NAME, ctx);
    }

    public void saveCurrentTheme(int id){
        Editor editor = mPreferences.edit();

        editor.putInt(PREF_THEME, id);
        editor.commit();
    }

    public int readCurrentTheme(){
        return mPreferences.getInt(PREF_THEME, -1);
    }
}
