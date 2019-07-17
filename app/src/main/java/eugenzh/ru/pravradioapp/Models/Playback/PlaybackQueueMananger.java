package eugenzh.ru.pravradioapp.Models.Playback;

import android.support.v4.media.MediaMetadataCompat;

import java.util.ArrayList;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesDateViewFactory;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewCategory;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewPodcast;
import eugenzh.ru.pravradioapp.Models.DataView.PodcastsDateViewFactory;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;

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

    public PlaybackQueueMananger(MetadataUpdateListener listener){
        mMetadataUpdateListener = listener;
        mPlayableCategory = new Category("undefined");
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
        DateViewPodcast podcastRepo = PodcastsDateViewFactory.getPodcasts(mRequestedTypeSource);
        return podcastRepo.getNameItem(mRequestedPlayID);
    }

    public String getRequestedPathToItem(){
        DateViewPodcast podcastRepo = PodcastsDateViewFactory.getPodcasts(mRequestedTypeSource);
        return podcastRepo.getURL(mRequestedPlayID);
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
        DateViewPodcast podcastRepo = PodcastsDateViewFactory.getPodcasts(mRequestedTypeSource);
        DateViewCategory categoryRepo = CategoriesDateViewFactory.getCategories(mRequestedTypeSource);
        long requestedIDCategory = categoryRepo.getSelectedItemID();

        if ( (mCurrentTypeSource != mRequestedTypeSource) || (mPlayableCategory.getId() != requestedIDCategory)){
            mPlayList.clear();
            mPlayList.addAll(podcastRepo.getItemsSrc());
            mPlayableCategory = categoryRepo.getItemToId(requestedIDCategory);
        }

        mCurrentTypeSource = mRequestedTypeSource;
        mCurrentPlayableID = mRequestedPlayID;
        mPodcastDuration = durationTrack;
        mCurrentPlayablePosition = getPositionFromId(mCurrentPlayableID);

        podcastRepo.setSelectedItem(mCurrentPlayableID);

        if (mCurrentTypeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            DateViewPodcast repo = PodcastsDateViewFactory.getPodcasts(TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER);
            repo.setSelectedItem(0L);
        }
        else if (mCurrentTypeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            DateViewPodcast repo = PodcastsDateViewFactory.getPodcasts(TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY);
            repo.setSelectedItem(0L);
        }
    }

    public void cleanInfoPlayback(){
        DateViewPodcast podcastRepo = PodcastsDateViewFactory.getPodcasts(mCurrentTypeSource);
        if (podcastRepo != null){
            podcastRepo.setSelectedItem(0L);
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
