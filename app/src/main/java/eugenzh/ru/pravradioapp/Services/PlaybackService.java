package eugenzh.ru.pravradioapp.Services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import java.util.List;

import eugenzh.ru.pravradioapp.Models.Playback.Playback;
import eugenzh.ru.pravradioapp.Models.Playback.PlaybackManager;
import eugenzh.ru.pravradioapp.Models.Playback.PlaybackQueueMananger;
import eugenzh.ru.pravradioapp.Models.Playback.PodcastPlayback;
import eugenzh.ru.pravradioapp.View.MainActivity;

public class PlaybackService extends MediaBrowserServiceCompat implements PlaybackManager.PlaybackServiceCallback{
    private PlaybackManager mPlaybackManager;

    private MediaSessionCompat mMediaSession;
    private PlaybackQueueMananger mPlaybackQueueMananger;
    private Playback mPlayback;

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return null;
    }


    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new PlayerServiceBinder();
    }

    public class PlayerServiceBinder extends Binder {
        public MediaSessionCompat.Token getMediaSessionToken(){
            return mMediaSession.getSessionToken();
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Context appContext = getApplicationContext();

        mPlaybackQueueMananger = new PlaybackQueueMananger(new PlaybackQueueMananger.MetadataUpdateListener() {
            @Override
            public void onMetadataChanged(MediaMetadataCompat metadata) {
                mMediaSession.setMetadata(metadata);
            }
        });

        mPlayback = new PodcastPlayback(this, mPlaybackQueueMananger);

        mMediaSession = new MediaSessionCompat(this, "PravRadioService");

        mPlaybackManager = new PlaybackManager(mPlaybackQueueMananger, this, mPlayback);

        mMediaSession. setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setCallback(mPlaybackManager.getMediaSessionCallback());

        Intent activityIntent = new Intent(appContext, MainActivity.class);
        mMediaSession.setSessionActivity(PendingIntent.getActivity(appContext, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null, appContext, MediaButtonReceiver.class);
        mMediaSession.setMediaButtonReceiver(PendingIntent.getBroadcast(appContext, 0, mediaButtonIntent, 0));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlaybackManager.handleStopRequest();
        mMediaSession.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mMediaSession, intent);
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    /// Implements metods PlaybackManager.PlaybackServiceCallback
    @Override
    public void onPlaybackStart() {
        mMediaSession.setActive(true);
        startService(new Intent(getApplicationContext(), PlaybackService.class));
    }

    @Override
    public void onPlaybackStop() {
        mMediaSession.setActive(false);
        stopForeground(true);
    }

    @Override
    public void onNotificationRequired() {

    }


    @Override
    public void onPlaybackStateUpdate(PlaybackStateCompat newState) {
        mMediaSession.setPlaybackState(newState);
    }

}
