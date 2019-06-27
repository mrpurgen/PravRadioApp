package eugenzh.ru.pravradioapp.Models.Playback;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;

public class PlaybackManager implements Playback.Callback{

    private PlaybackQueueMananger mPlaybackQueueMananger;
    private PlaybackServiceCallback mServiceCallback;
    private Playback mPlayback;
    private MediaSessionCallback mMediaSessionCallback;

    public PlaybackManager(PlaybackQueueMananger queueMananger,
                           PlaybackServiceCallback serviceCallback,
                           Playback playback){
        mPlaybackQueueMananger = queueMananger;
        mServiceCallback = serviceCallback;
        mPlayback = playback;
        mPlayback.setCallback(this);

        mMediaSessionCallback = new MediaSessionCallback();

    }

    public MediaSessionCallback getMediaSessionCallback(){
        return mMediaSessionCallback;
    }

    public void handlePlayRequest(){
        mServiceCallback.onPlaybackStart();
        mPlayback.play();
    }

    public void handlePauseRequest(){
        if (mPlayback.isPlaying()){
            mPlayback.pause();
            mServiceCallback.onPlaybackStop();
        }
    }

    public void handleContinuePlay(){
        mPlayback.returnFromPause();
    }

    public void handleStopRequest(){
        mPlayback.stop();
        mServiceCallback.onPlaybackStop();
        updatePlaybackState();
    }

    public void updatePlaybackState(){
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;

        if ( (mPlayback != null) && (mPlayback.isConnected()) ){
            position = mPlayback.getCurrentStreamPosition();
        }

        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(getAvailableActions());

        int state = mPlayback.getState();

        stateBuilder.setState(state, position, 1.0f, SystemClock.elapsedRealtime());

        mServiceCallback.onPlaybackStateUpdate(stateBuilder.build());
    }

    private long getAvailableActions(){
        long actions =  PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mPlayback.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }

    /// Implements metods Playback.Callback interface
    @Override
    public void onCompletion() {
        if (mPlaybackQueueMananger.skipQueuePosition(1)){
            handlePlayRequest();
        }
        else {
            handleStopRequest();
        }
    }

    @Override
    public void onError(String errMsg) {

    }

    @Override
    public void onPlaybackStatusChanged(int state) {
        updatePlaybackState();
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback{
        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            mPlaybackQueueMananger.setRequestedPlayID(Long.valueOf(mediaId));

            TypeSourceItems typeSource = (TypeSourceItems) extras.getSerializable("TYPE_SOURCE");
            mPlaybackQueueMananger.setRequestedTypeSource(typeSource);
            handlePlayRequest();
        }

        @Override
        public void onPlay() {
            handleContinuePlay();
        }

        @Override
        public void onSeekTo(long pos) {
            mPlayback.seekTo(pos);
        }

        @Override
        public void onPause() {
            handlePauseRequest();
        }

        @Override
        public void onStop() {
            handleStopRequest();
        }

        @Override
        public void onSkipToNext() {
            if (mPlaybackQueueMananger.skipQueuePosition(1)){
                handlePlayRequest();
            }
            else{
                handleStopRequest();
            }
        }

        @Override
        public void onSkipToPrevious() {
            if (mPlaybackQueueMananger.skipQueuePosition(-1)){
                handlePlayRequest();
            }
            else{
                handleStopRequest();
            }
        }
    }

    public interface PlaybackServiceCallback{
        void onPlaybackStart();
        void onNotificationRequired();
        void onPlaybackStop();
        void onPlaybackStateUpdate(PlaybackStateCompat newState);
    }

}
