package eugenzh.ru.pravradioapp.Presenters;


import android.content.Context;

import android.content.ServiceConnection;

import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradioapp.Services.PlaybackServiceConnectionManager;
import eugenzh.ru.pravradioapp.Services.SheduleService;
import eugenzh.ru.pravradioapp.View.CustomToast;
import eugenzh.ru.pravradioapp.View.PlayerControlView;

@InjectViewState
public class PlayerControlPresenter extends MvpPresenter<PlayerControlView> implements PlaybackServiceConnectionManager.ServiceConnectionCallback{
    private Context mContext;
    private ServiceConnection mServiceConnection;
    private MediaControllerCompat mMediaController;
    private PlaybackServiceConnectionManager mServiceConnectionManager;
    private MediaControllerCallback mMediaControlCallback;

    private PlaybackStateCompat mCurrentPlaybackState;
    private SheduleService mSheduleServiceUpdateProgress;

    private long mCurrentPositionPB;

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
        if (mCurrentPlaybackState.getState() == PlaybackStateCompat.STATE_PAUSED) {
            mMediaController.getTransportControls().play();
        }
    }

    public void onPausePressed(){
        mMediaController.getTransportControls().pause();
    }

    @Override
    public void onSuccConnection() {
        mMediaController = mServiceConnectionManager.getMediaController();
        mMediaController.registerCallback(mMediaControlCallback);
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
                mSheduleServiceUpdateProgress.stop();
                getViewState().pauseView();
            break;

        }

    }

    private void updateProgress(){
        if (mCurrentPlaybackState == null){
            return;
        }

        if (mCurrentPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING){
            mCurrentPositionPB = mCurrentPlaybackState.getPosition();
            long time = mCurrentPlaybackState.getLastPositionUpdateTime();;

            long timeDelta = SystemClock.elapsedRealtime() - time;
            mCurrentPositionPB += ((int) timeDelta * mCurrentPlaybackState.getPlaybackSpeed());
            getViewState().setTrackPosition(mCurrentPositionPB);
        }
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback{
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            super.onPlaybackStateChanged(state);

            playbackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            super.onMetadataChanged(metadata);

            String titlePodcast = metadata.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE);
            getViewState().setTrackName(titlePodcast);

            long durationPodcast = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
            getViewState().setTrackDuration(durationPodcast);
        }
    }
}
