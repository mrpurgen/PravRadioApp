package eugenzh.ru.pravradiopodcast.Preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import eugenzh.ru.pravradiopodcast.Common.TypeSourceItems;

public class PreferencesPlaybackManager {
    private static String PREF_FILE_NAME = "PravRadioAppPrefPlayback";

    private static String PREF_CATEGORY_NAME = "PREF_CATEGORY_NAME";
    private static String PREF_PODCAST_NAME = "PREF_PODCAST_NAME";
    private static String PREF_DURATION = "PREF_DURATION";

    private static String PREF_TYPE_SOURCE = "PREF_TYPE_SOURCE";

    private static String PREF_CATEGORY_ID = "PREF_CATEGORY_ID";
    private static String PREF_PODCAST_ID = "PREF_PODCAST_ID";

    private static String PREF_POSITION = "PREF_POSITION";

    private SharedPreferences mPreferences;

    public PreferencesPlaybackManager(Context ctx) {
        mPreferences = ctx.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void saveTypeSource(TypeSourceItems typeSource) {
        Editor editor = mPreferences.edit();

        editor.putInt(PREF_TYPE_SOURCE, typeSource.getValue());
        editor.commit();
    }

    public TypeSourceItems readTypeSource() {
        int type = mPreferences.getInt(PREF_TYPE_SOURCE, 0);

        return TypeSourceItems.valueOf(type);
    }

    public void savePlaybackMainInfo(String nameCategory, String namePodcast, long duration) {
        Editor editor = mPreferences.edit();

        editor.putString(PREF_CATEGORY_NAME, nameCategory);
        editor.putString(PREF_PODCAST_NAME, namePodcast);
        editor.putLong(PREF_DURATION, duration);

        editor.commit();
    }

    public void savePlaybackIds(Long categoryId, Long podcastId) {
        Editor editor = mPreferences.edit();

        editor.putLong(PREF_CATEGORY_ID, categoryId);
        editor.putLong(PREF_PODCAST_ID, podcastId);

        editor.commit();
    }

    public void savePosition(Long position) {
        Editor editor = mPreferences.edit();

        editor.putLong(PREF_POSITION, position);

        editor.commit();
    }

    public String readCategoryName() {
        return mPreferences.getString(PREF_CATEGORY_NAME, "undef_catgory");
    }

    public String readPodcastName() {
        return mPreferences.getString(PREF_PODCAST_NAME, "undef_podcast");
    }

    public Long readDuration() {
        return mPreferences.getLong(PREF_DURATION, 0L);
    }

    public Long readPosition() {
        return mPreferences.getLong(PREF_POSITION, 0L);
    }

    public Long readCategoryId(){
        return mPreferences.getLong(PREF_CATEGORY_ID, 0L);
    }

    public Long readPodcastId(){
        return mPreferences.getLong(PREF_PODCAST_ID, 0L);
    }
}
