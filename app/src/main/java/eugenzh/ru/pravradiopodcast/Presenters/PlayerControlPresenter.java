package eugenzh.ru.pravradiopodcast.Presenters;


import android.content.Context;

import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.List;

import eugenzh.ru.pravradiopodcast.Common.RequestResult;
import eugenzh.ru.pravradiopodcast.Common.TypeSourceItems;
import eugenzh.ru.pravradiopodcast.Models.DataStore.DataStoreCategory;
import eugenzh.ru.pravradiopodcast.Models.DataStore.DataStoreFacade;
import eugenzh.ru.pravradiopodcast.Models.DataStore.DataStoreFacadeImp;
import eugenzh.ru.pravradiopodcast.Models.DataStore.DataStorePodcast;
import eugenzh.ru.pravradiopodcast.Models.DataStore.Observer.DataStoreObserver;
import eugenzh.ru.pravradiopodcast.Models.Item.Item;
import eugenzh.ru.pravradiopodcast.Preferences.PreferencesPlaybackManager;
import eugenzh.ru.pravradiopodcast.Services.PlaybackServiceConnectionManager;
import eugenzh.ru.pravradiopodcast.Services.SheduleService;
import eugenzh.ru.pravradiopodcast.View.CustomToast;
import eugenzh.ru.pravradiopodcast.View.PlayerControlView;

@InjectViewState
public class PlayerControlPresenter extends MvpPresenter<PlayerControlView>
                                    implements PlaybackServiceConnectionManager.ServiceConnectionCallback,
                                               DataStoreObserver {
    private Context mContext;
    private ServiceConnection mServiceConnection;
    private MediaControllerCompat mMediaController;
    private PlaybackServiceConnectionManager mServiceConnectionManager;
    private MediaControllerCallback mMediaControlCallback;

    private PlaybackStateCompat mCurrentPlaybackState;
    private SheduleService mSheduleServiceUpdateProgress;

    private long mCurrentPositionPB;

    private boolean isCategoryUpdate;

    public PlayerControlPresenter(Context context){
        mContext = context;
        connectToPlayerService();

        getViewState().hidePanel();
    }
    private void connectToPlayerService(){
        mServiceConnectionManager = new PlaybackServiceConnectionManager();
        mServiceConnection = mServiceConnectionManager.connection(mContext);
        mMediaControlCallback = new MediaControllerCallback();
        mServiceConnectionManager.setCallback(this);

        mSheduleServiceUpdateProgress = new SheduleService(new Runnable() {
            @Override
            public void run() {
                updateProgress();
            }
        });
    }

    @Override
    public void onDestroy() {
        mMediaController.unregisterCallback(mMediaControlCallback);
        mServiceConnectionManager.disconnection(mContext, mServiceConnection);
    }

    public void onPlayPressed(){
        if (mCurrentPlaybackState == null){
            updatePodcastStoreAndPlay();
        }
        else{
            int state = mCurrentPlaybackState.getState();
            if (state == PlaybackStateCompat.STATE_PAUSED) {
                mMediaController.getTransportControls().play();
            }
        }
    }

    private void updatePodcastStoreAndPlay(){
        PreferencesPlaybackManager pref = new PreferencesPlaybackManager(mContext);
        TypeSourceItems typeSource = pref.readTypeSource();

        DataStoreFacade dataStore = new DataStoreFacadeImp();

        DataStoreCategory dataStoreCategory = dataStore.getDataStoreCategory(typeSource);
        dataStoreCategory.unsubscripEventUpdateView(this);
        dataStoreCategory.subscripEventUpdateView(this);

        ///Не знаю как сделать по другому
        isCategoryUpdate = true;
        dataStoreCategory.update();

    }

    @Override
    public <T extends Item> void update(RequestResult result, List<T> list) {
        if (mCurrentPlaybackState != null){
            return;
        }

        PreferencesPlaybackManager pref = new PreferencesPlaybackManager(mContext);
        TypeSourceItems typeSource = pref.readTypeSource();

        DataStoreFacade dataStore = new DataStoreFacadeImp();

        if (isCategoryUpdate && result == RequestResult.REQUEST_RESUTL_SUCC){
            isCategoryUpdate = false;

            Long catgoryId = 0L;
            DataStoreCategory dataStoreCategory = dataStore.getDataStoreCategory(typeSource);

            if (typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER) {
                 catgoryId = pref.readCategoryId();
            }
            else if (typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
                String categoryName = pref.readCategoryName();
                catgoryId = dataStoreCategory.getIdByName(categoryName);
            }

            dataStoreCategory.setSelectedItem(catgoryId);

            DataStorePodcast dataStorePodcast = dataStore.getDataStorePodcast(typeSource);
            dataStorePodcast.subscripEventUpdateView(this);
        }
        else if (result == RequestResult.REQUEST_RESUTL_SUCC){
            Long podcastId = 0L;

            if (typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER) {
                podcastId = pref.readPodcastId();
            }
            else if (typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
                String podcastName = pref.readPodcastName();
                DataStorePodcast dataStorePodcast = dataStore.getDataStorePodcast(typeSource);

                podcastId = dataStorePodcast.getIdByName(podcastName);
            }

            Bundle bundle = new Bundle();
            bundle.putSerializable("TYPE_SOURCE", typeSource);

            mMediaController.getTransportControls().playFromMediaId(String.valueOf(podcastId), bundle);
            mMediaController.getTransportControls().seekTo(mCurrentPositionPB);
        }
    }

   public void onPausePressed(){
        mMediaController.getTransportControls().pause();
    }

   public void onStartScroll(){
       mSheduleServiceUpdateProgress.stop();
   }

   public void onSetPosition(long position){

        getViewState().setPosiotionProgressBar(position);
   }

   public void onStopScroll(long position){
       getViewState().setPosiotionProgressBar(position);
       mMediaController.getTransportControls().seekTo(position);
   }

   public void onPrevPressed(){
        mMediaController.getTransportControls().skipToPrevious();
   }

   public void onNextPressed(){
        mMediaController.getTransportControls().skipToNext();
   }

    @Override
    public void onSuccConnection() {
        mMediaController = mServiceConnectionManager.getMediaController();
        mMediaController.registerCallback(mMediaControlCallback);

        mMediaControlCallback.onMetadataChanged(mMediaController.getMetadata());
        mMediaControlCallback.onPlaybackStateChanged(mMediaController.getPlaybackState());
    }

    @Override
    public void onFailConnection(RemoteException e) {
        CustomToast.showMessage(mContext, e.getMessage());
    }

    private void playbackStateChanged(PlaybackStateCompat state){
        if (state == null){
            return;
        }

        mCurrentPlaybackState = state;
        switch(state.getState()){
            case PlaybackStateCompat.STATE_PLAYING:
                mSheduleServiceUpdateProgress.start();
                getViewState().showPanel();
                getViewState().playView();
            break;

            case PlaybackStateCompat.STATE_PAUSED:
            //    mSheduleServiceUpdateProgress.stop();
                getViewState().pauseView();
            break;

            case PlaybackStateCompat.STATE_STOPPED:
                getViewState().hidePanel();
                break;
            case PlaybackStateCompat.STATE_ERROR:
                String errorMsg = mCurrentPlaybackState.getErrorMessage().toString();
                getViewState().hidePanel();
                CustomToast.showMessage(mContext,errorMsg);
        }

    }

    private void updateProgress(){
        if (mCurrentPlaybackState == null){
            return;
        }

        mCurrentPositionPB = mCurrentPlaybackState.getPosition();
        if (mCurrentPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING){
            long time = mCurrentPlaybackState.getLastPositionUpdateTime();

            long timeDelta = SystemClock.elapsedRealtime() - time;
            mCurrentPositionPB += ((int) timeDelta * mCurrentPlaybackState.getPlaybackSpeed());
            getViewState().setTrackPosition(mCurrentPositionPB);
        }
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback{
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            if (state != null) {
                playbackStateChanged(state);
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);

            if (metadata != null) {
                if (mCurrentPlaybackState == null){
                    getPositionFromPref();

                    getViewState().showPanel();
                    getViewState().setPosiotionProgressBar(mCurrentPositionPB);
                }

                String titlePodcast = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
                getViewState().setTrackName(titlePodcast);

                long durationPodcast = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
                getViewState().setTrackDuration(durationPodcast);
            }
        }

        void getPositionFromPref(){
            PreferencesPlaybackManager pref = new PreferencesPlaybackManager(mContext);
            mCurrentPositionPB = pref.readPosition();
        }
    }
}
