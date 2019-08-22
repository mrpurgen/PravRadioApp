package eugenzh.ru.pravradiopodcast.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

abstract public class PreferencesManagerBase {
    protected  SharedPreferences mPreferences;

    protected PreferencesManagerBase(String fileName, Context ctx){
        mPreferences = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }
}
