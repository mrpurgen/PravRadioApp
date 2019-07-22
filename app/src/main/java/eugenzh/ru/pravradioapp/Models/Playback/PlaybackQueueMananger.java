package eugenzh.ru.pravradioapp.Models.Playback;

import android.content.Context;
import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreCategory;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreFacade;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreFacadeImp;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStorePodcast;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;
import eugenzh.ru.pravradioapp.Preferences.PreferencesPlaybackManager;

public class PlaybackQueueMananger {
    private final MediaMetadataCompat.Builder metaDataBuilder = new MediaMetadataCompat.Builder();

    private List<Podcast> mPlayList = new ArrayList<>();

    private long mCurrentPlayableID = 0L;
    private long mRequestedPlayID = 0L;
    private long mPodcastDuration = 0L;
    private int mCurrentPlayablePosition = 0;

    private TypeSourceItems mCurrentTypeSource = TypeSourceItems.TYPE_SOURCE_UNDEFINED;
    private TypeSourceItems mRequestedTypeSource = TypeSourceItems.TYPE_SOURCE_UNDEFINED;

    private Category mPlayableCategory;
    private MetadataUpdateListener mMetadataUpdateListener;

    private DataStoreFacade mDataStore;

    public PlaybackQueueMananger(MetadataUpdateListener listener){
        mMetadataUpdateListener = listener;
        mPlayableCategory = new Category("undefined");
        mDataStore = new DataStoreFacadeImp();
    }

    public TypeSourceItems getCurrentTypeSource() {
        return mCurrentTypeSource;
    }

    public void setCurrentTypeSource(TypeSourceItems typeSource) {
        mCurrentTypeSource = typeSource;
    }

    public TypeSourceItems getRequestedTypeSource() {
        return mRequestedTypeSource;
    }

    public void setRequestedTypeSource(TypeSourceItems typeSource) {
        mRequestedTypeSource = typeSource;
    }

    public void setPlayableCategory(Category category) {
        mPlayableCategory = category;
    }

    public void setMetadataChangedListener(MetadataUpdateListener listener){
        mMetadataUpdateListener = listener;
    }

    public void setPodcastDuration(long duration) {
        mPodcastDuration = duration;
    }

    public void setPlayList(List<Podcast> list){
        mPlayList.clear();
        mPlayList.addAll(list);
    }

    public void setCurrentPlayableID(long id){
        mCurrentPlayableID = id;
        mCurrentPlayablePosition = getPositionFromId(id);
    }

    public long getCurrentPlayableID() {
        return mCurrentPlayableID;
    }

    public long getRequestedPlayID() {
        return mRequestedPlayID;
    }

    public void setRequestedPlayID(long id) {
        mRequestedPlayID = id;
    }

    public void updateMetadata(){
        MediaMetadataCompat metadata = metaDataBuilder
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mPodcastDuration)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, Long.toString(mCurrentPlayableID))
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, mPlayableCategory.getName())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, getNamePlayablePodcast())
                .build();

        mMetadataUpdateListener.onMetadataChanged(metadata);
    }


    public String getNamePlayablePodcast(){
        for (Podcast podcast: mPlayList) {
            if (podcast.getId().equals(mCurrentPlayableID)){
                return podcast.getName();
            }
        }
        return "unknow podcast";
    }

    public String getNameRequstedPodcast(){
        DataStorePodcast dataStorePodcast = mDataStore.getDataStorePodcast(mRequestedTypeSource);
        return dataStorePodcast.getNameItem(mRequestedPlayID);
    }

    public String getRequestedPathToItem(){
        DataStorePodcast dataStorePodcast = mDataStore.getDataStorePodcast(mRequestedTypeSource);
        return dataStorePodcast.getURL(mRequestedPlayID);
    }

    public boolean skipQueuePosition(int countSkipPosition){
        int position = mCurrentPlayablePosition + countSkipPosition;

        if (position < 0){
            position = 0;
        }

        if ( position <= (mPlayList.size() - 1)){
            mRequestedPlayID = mPlayList.get(position).getId();
            return true;
        }
        return false;
    }

    private int getPositionFromId(long id){
        for(Podcast podcast: mPlayList){
            if (podcast.getId() == id){
                return mPlayList.indexOf(podcast);
            }
        }
        return 0;
    }

    public void updateInfoPlayback(long durationTrack){
        DataStorePodcast dataStorePodcast = mDataStore.getDataStorePodcast(mRequestedTypeSource);
        DataStoreCategory dataStoreCategory = mDataStore.getDataStoreCategory(mRequestedTypeSource);
        long requestedIDCategory = dataStoreCategory.getSelectedItemID();

        if ( (mCurrentTypeSource != mRequestedTypeSource) || (mPlayableCategory.getId() != requestedIDCategory)){
            mPlayList.clear();
            mPlayList.addAll(dataStorePodcast.getItemsSrc());
            mPlayableCategory = dataStoreCategory.getItemToId(requestedIDCategory);
        }

        mCurrentTypeSource = mRequestedTypeSource;
        mCurrentPlayableID = mRequestedPlayID;
        mPodcastDuration = durationTrack;
        mCurrentPlayablePosition = getPositionFromId(mCurrentPlayableID);

        dataStorePodcast.setSelectedItem(mCurrentPlayableID);

        if (mCurrentTypeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            DataStorePodcast repo = mDataStore.getDataStorePodcast(TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER);
            repo.setSelectedItem(0L);
        }
        else if (mCurrentTypeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            DataStorePodcast repo = mDataStore.getDataStorePodcast(TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY);
            repo.setSelectedItem(0L);
        }
    }

    public void saveInfoToPreferences(Context ctx){
        PreferencesPlaybackManager pref = new PreferencesPlaybackManager(ctx);

        pref.saveTypeSource(mCurrentTypeSource);
        pref.savePlaybackIds(mPlayableCategory.getId(), mCurrentPlayableID);
        pref.savePlaybackMainInfo(mPlayableCategory.getName(), getNamePlayablePodcast(), mPodcastDuration);
    }

    public void loadInfoPlaybackPref(Context ctx){
        PreferencesPlaybackManager pref = new PreferencesPlaybackManager(ctx);

        if (pref.readTypeSource() != TypeSourceItems.TYPE_SOURCE_UNDEFINED){
            MediaMetadataCompat metadata = metaDataBuilder
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, pref.readDuration())
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, Long.toString(pref.readPodcastId()))
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, pref.readCategoryName())
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, pref.readPodcastName())
                    .build();

            mMetadataUpdateListener.onMetadataChanged(metadata);
        }
    }

    public void savePositionToPreferences(Context ctx, long position){
        PreferencesPlaybackManager pref = new PreferencesPlaybackManager(ctx);

        pref.savePosition(position);
    }

    public void cleanInfoPlayback(){
        DataStorePodcast dataStorePodcast = mDataStore.getDataStorePodcast(mCurrentTypeSource);
        if (dataStorePodcast != null){
            dataStorePodcast.setSelectedItem(0L);
        }

        mCurrentTypeSource = TypeSourceItems.TYPE_SOURCE_UNDEFINED;
        mRequestedTypeSource = TypeSourceItems.TYPE_SOURCE_UNDEFINED;

        mCurrentPlayableID = 0L;
        mRequestedPlayID = 0L;

        mPlayList.clear();
        mPlayableCategory = new Category("undefined");
    }


    public interface MetadataUpdateListener{
        void onMetadataChanged(MediaMetadataCompat metadata);
    }
}
