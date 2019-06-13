package eugenzh.ru.pravradioapp.Models.Playback;

import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewPodcast;
import eugenzh.ru.pravradioapp.Models.DataView.PodcastsDateViewFactory;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;

public class PlaybackQueueMananger {
    private final MediaMetadataCompat.Builder metaDataBuilder = new MediaMetadataCompat.Builder();

    private List<Podcast> mPlayList = new ArrayList<>();

    private long mCurrentPlayableID = 0;
    private long mRequestedPlayID = 0;
    private long mPodcastDuration = 0;
    private int mCurrentplayableposition = 0;

    private TypeSourceItems mCurrentTypeSource = TypeSourceItems.TYPE_SOURCE_UNDEFINED;
    private TypeSourceItems mRequestedTypeSource = TypeSourceItems.TYPE_SOURCE_UNDEFINED;

    private Category mPlayableCategory;
    private MetadataUpdateListener mMetadataUpdateListener;

    public PlaybackQueueMananger(MetadataUpdateListener listener){
        mMetadataUpdateListener = listener;
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
        mCurrentplayableposition = getPositionFromId(id);
        updateMetadata();
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
            if (podcast.getCategoryId().equals(mCurrentPlayableID)){
                return podcast.getName();
            }
        }
        return "unknow podcast";
    }

    public String getNameRequstedPodcast(){
        DateViewPodcast podcastRepo = PodcastsDateViewFactory.getPodcasts(mRequestedTypeSource);
        return podcastRepo.getNameItem(mRequestedPlayID);
    }

    public String getRequestedPathToItem(){
        DateViewPodcast podcastRepo = PodcastsDateViewFactory.getPodcasts(mRequestedTypeSource);
        return podcastRepo.getURL(mRequestedPlayID);
    }

    public boolean skipQueuePosition(int countSkipPosition){
        int position = mCurrentplayableposition + countSkipPosition;

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

    public interface MetadataUpdateListener{
        void onMetadataChanged(MediaMetadataCompat metadata);
    }
}
