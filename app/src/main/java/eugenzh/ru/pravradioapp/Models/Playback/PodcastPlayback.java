package eugenzh.ru.pravradioapp.Models.Playback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Services.PlaybackService;
import retrofit2.http.Url;

public class PodcastPlayback implements Playback{
    private static final int AUDIO_NO_FOCUS_NO_DUCK = 0;
    private static final int AUDIO_NO_FOCUS_CAN_DUCK = 1;
    private static final int AUDIO_FOCUSED = 2;

    public static final float VOLUME_DUCK = 0.2f;
    public static final float VOLUME_NORMAL = 1.0f;


    private Context mContext;
    private Callback mCallback;
    private SimpleExoPlayer mPlayer;
    private AudioManager mAudioManager;
    private PlaybackQueueMananger mPlayListManager;
    private boolean mAudioNoisyReceiverRegistered;
    private boolean mPlayOnFocusGain;
    private int mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
    ExoPlayerEventListener mPlayerEventListener = new ExoPlayerEventListener();

    private final IntentFilter mAudioNoisyIntentFilter =  new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private BroadcastReceiver mAudioReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isPlaying()){
                Intent cmd = new Intent(context, PlaybackService.class);
                cmd.setAction(PlaybackService.ACTION_CMD);
                cmd.putExtra(PlaybackService.CMD_NAME, PlaybackService.CMD_PAUSE);
                mContext.startService(cmd);
            }
        }
    };

    public PodcastPlayback(Context ctx, PlaybackQueueMananger playList){
        mContext = ctx.getApplicationContext();
        mPlayListManager = playList;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        giveUpAudioFocus();
        unRegisterAudioNoisyReceiver();
        mPlayer.stop();
        releaseResourse(true);
    }

    @Override
    public void setState(int state) {

    }

    @Override
    public int getState() {

        /// FIXME
        if (mPlayer == null){
            return PlaybackStateCompat.STATE_NONE;
        }

        switch(mPlayer.getPlaybackState()){
            case Player.STATE_IDLE:
                return PlaybackStateCompat.STATE_STOPPED;

            case Player.STATE_BUFFERING:
                return PlaybackStateCompat.STATE_BUFFERING;

            case Player.STATE_READY:
                return mPlayer.getPlayWhenReady() ? PlaybackStateCompat.STATE_PLAYING
                                                  : PlaybackStateCompat.STATE_PAUSED;

            case Player.STATE_ENDED:
                return PlaybackStateCompat.STATE_STOPPED;

            default:
                return PlaybackStateCompat.STATE_NONE;
        }
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean isPlaying() {
        return ( mPlayOnFocusGain || (mPlayer != null && mPlayer.getPlayWhenReady()) );
    }

    @Override
    public long getCurrentStreamPosition() {
        return mPlayer != null ? mPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void updateLastKnowStreamPosition() {
    }

    @Override
    public void play() {
        mPlayOnFocusGain = true;

        tryToGetAudioFocus();
        registerAudioNoisyReceiver();

        if (isPodcastChanged()){
            releaseResourse(false);

            if (mPlayer == null) {
                mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, new DefaultRenderersFactory(mContext),
                        new DefaultTrackSelector(), new DefaultLoadControl());

                mPlayer.addListener(mPlayerEventListener);
            }

            MediaSource mediaSource = mediaSourceBuild();
            mPlayer.prepare(mediaSource);
        }

        configurePlayerState();
    }

    private MediaSource mediaSourceBuild(){
        TypeSourceItems requestedSourceItem = mPlayListManager.getRequestedTypeSource();
        Uri uri = Uri.parse(mPlayListManager.getRequestedPathToItem());

        if (requestedSourceItem == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            return localMediaSourceBuild(uri);
        }

        if (requestedSourceItem == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            return httpMediaSourceBuild(uri);
        }

        return null;
    }

    private MediaSource httpMediaSourceBuild(Uri uri){
            DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                    "mrpurgen_rulezzzzz",
                    null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true);
            return new ExtractorMediaSource.Factory(httpDataSourceFactory).createMediaSource(uri);
    }

    private MediaSource localMediaSourceBuild(Uri uri){
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "PravRadioApp"));
        return new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    private boolean isPodcastChanged(){
        TypeSourceItems currentSource = mPlayListManager.getRequestedTypeSource();
        TypeSourceItems reqeustedSource = mPlayListManager.getRequestedTypeSource();

        if (currentSource != reqeustedSource){
            return true;
        }

        if (currentSource == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            /// В случае воспроизведения из памяти уникальным идентификатором подкаста является Имя.
            String currentNamePodcast = mPlayListManager.getNamePlayablePodcast();
            String requestedNamePodcast = mPlayListManager.getNameRequstedPodcast();

            return !TextUtils.equals(currentNamePodcast, requestedNamePodcast);
        }

        if (currentSource == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            long currentID = mPlayListManager.getCurrentPlayableID();
            long requstedID = mPlayListManager.getRequestedPlayID();

            return (currentID != requstedID);
        }

        return true;
    }

    private void tryToGetAudioFocus(){
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                                                     AudioManager.STREAM_MUSIC,
                                                     AudioManager.AUDIOFOCUS_GAIN);
        if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mCurrentAudioFocusState = AUDIO_FOCUSED;
        }
        else{
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void configurePlayerState(){
        if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_NO_DUCK){
            pause();
        }
        else{
            registerAudioNoisyReceiver();

            if (mCurrentAudioFocusState == AUDIO_NO_FOCUS_CAN_DUCK) {
                mPlayer.setVolume(VOLUME_DUCK);
            } else {
                mPlayer.setVolume(VOLUME_NORMAL);
            }
            if (mPlayOnFocusGain) {
                mPlayer.setPlayWhenReady(true);
                mPlayOnFocusGain = false;
            }
        }
    }

    private void releaseResourse(boolean releasePlayer){
        if ( releasePlayer && (mPlayer != null) ) {
            mPlayer.release();
            mPlayer.removeListener(mPlayerEventListener);
            mPlayer = null;
            mPlayOnFocusGain = false;
        }
    }


    private void giveUpAudioFocus(){
        if (mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void registerAudioNoisyReceiver(){
        if (!mAudioNoisyReceiverRegistered){
            mContext.registerReceiver(mAudioReceiver, mAudioNoisyIntentFilter);
            mAudioNoisyReceiverRegistered = true;
        }
    }

    private void unRegisterAudioNoisyReceiver(){
        if (mAudioNoisyReceiverRegistered){
            mContext.unregisterReceiver(mAudioReceiver);
            mAudioNoisyReceiverRegistered = false;
        }
    }

    @Override
    public void pause() {
        if (mPlayer != null){
            mPlayer.setPlayWhenReady(false);
            mPlayListManager.savePositionToPreferences(mContext, getCurrentStreamPosition());
        }
        unRegisterAudioNoisyReceiver();
        releaseResourse(false);
    }

    @Override
    public void returnFromPause() {
        if (mPlayer != null){
            mPlayer.setPlayWhenReady(true);
        }
    }

    @Override
    public void seekTo(long position) {
        if (mPlayer != null){
            registerAudioNoisyReceiver();
            mPlayer.seekTo(position);
            mPlayListManager.savePositionToPreferences(mContext, getCurrentStreamPosition());
        }
    }

    @Override
    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            switch(i){
                case AudioManager.AUDIOFOCUS_GAIN:
                    mCurrentAudioFocusState = AUDIO_FOCUSED;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mCurrentAudioFocusState = AUDIO_NO_FOCUS_CAN_DUCK;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                    mPlayOnFocusGain = ( (mPlayer != null) && (mPlayer.getPlayWhenReady()) );
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mCurrentAudioFocusState = AUDIO_NO_FOCUS_NO_DUCK;
                    break;
            }
            if (mPlayer != null){
                configurePlayerState();
            }
        }
    };

    private final class ExoPlayerEventListener implements Player.EventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE:
                case Player.STATE_BUFFERING:
                case Player.STATE_READY:

                    if (playWhenReady) {
                        setInfoCurrentPlaylist(playbackState);
                        if (mCallback != null) {
                            mCallback.onPlaybackStatusChanged(getState());
                        }
                    }
                    else {
                        if (mCallback != null) {
                            mCallback.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED);
                        }
                    }
                    break;
                case Player.STATE_ENDED:
                    if (mCallback != null){
                        mCallback.onCompletion();
                    }
                    break;
            }
        }
        private void setInfoCurrentPlaylist(int playbackState){
            if (playbackState == Player.STATE_READY) {
                mPlayListManager.updateInfoPlayback(mPlayer.getDuration());
                mPlayListManager.updateMetadata();
                mPlayListManager.saveInfoToPreferences(mContext);
            }
            else if (playbackState == Player.STATE_IDLE){
                mPlayListManager.savePositionToPreferences(mContext, getCurrentStreamPosition());
                mPlayListManager.cleanInfoPlayback();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            String errorMessage;

            switch(error.type){
                case ExoPlaybackException.TYPE_SOURCE:
                    errorMessage = error.getSourceException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    errorMessage = error.getRendererException().getMessage();
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                    errorMessage = error.getUnexpectedException().getMessage();
                    break;
                default:
                    errorMessage = "unknow error: " + error.getMessage();
                    break;
            }

            if (mCallback != null){
                mCallback.onError("Player error: " + errorMessage);
            }
        }


    }
}
