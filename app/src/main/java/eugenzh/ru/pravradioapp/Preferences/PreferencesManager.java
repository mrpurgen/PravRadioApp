package eugenzh.ru.pravradioapp.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static String PREF_FILE_NAME = "PravRadioAppPref";

    private SharedPreferences mPreferences;

    public PreferencesManager(Context ctx){
        mPreferences = ctx.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }
}
