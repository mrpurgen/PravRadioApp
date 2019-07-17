package eugenzh.ru.pravradioapp.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import eugenzh.ru.pravradioapp.R;
import eugenzh.ru.pravradioapp.View.MainActivity;

public class MediaNotificationManager extends BroadcastReceiver {
    private static final String CHANNEL_ID = "eugenzh.ru.pravradioapp";
    private static final int NOTIFICATION_ID = 404;
    private static final int REQUEST_CODE = 100;

    public static final String ACTION_PAUSE = "eugenzh.ru.pause";
    public static final String ACTION_PLAY = "ceugenzh.ru.play";
    public static final String ACTION_PREV = "eugenzh.ru.prev";
    public static final String ACTION_NEXT = "eugenzh.ru.next";
    public static final String ACTION_STOP = "eugenzh.ru.stop";

    private final PlaybackService mPlaybackService;
    private MediaSessionCompat.Token mSessionToken;
    private MediaControllerCompat mController;
    private MediaControllerCompat.TransportControls mTransportControls;

    private PlaybackStateCompat mPlaybackState;
    private MediaMetadataCompat mMetadata;

    private final NotificationManagerCompat mNotificationManager;

    private final PendingIntent mPauseIntent;
    private final PendingIntent mPlayIntent;
    private final PendingIntent mPreviousIntent;
    private final PendingIntent mNextIntent;
    private final PendingIntent mStopIntent;

    private boolean mStarted = false;

    public MediaNotificationManager(PlaybackService service) throws RemoteException{
        mPlaybackService = service;

        updateSessionToken();

        mNotificationManager = NotificationManagerCompat.from(service);

        String pkg = mPlaybackService.getPackageName();

        mPauseIntent = PendingIntent.getBroadcast(mPlaybackService, REQUEST_CODE,
                new Intent(ACTION_PAUSE).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        mPlayIntent = PendingIntent.getBroadcast(mPlaybackService, REQUEST_CODE,
                new Intent(ACTION_PLAY).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        mPreviousIntent = PendingIntent.getBroadcast(mPlaybackService, REQUEST_CODE,
                new Intent(ACTION_PREV).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        mNextIntent = PendingIntent.getBroadcast(mPlaybackService, REQUEST_CODE,
                new Intent(ACTION_NEXT).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        mStopIntent = PendingIntent.getBroadcast(mPlaybackService, REQUEST_CODE,
                new Intent(ACTION_STOP).setPackage(pkg), PendingIntent.FLAG_CANCEL_CURRENT);

        mNotificationManager.cancelAll();
    }

    public void startNotification(){
        if (mStarted) {
            return;
        }

        mMetadata = mController.getMetadata();
        mPlaybackState = mController.getPlaybackState();

        Notification notification = createNotification();
        if (notification != null){
            mController.registerCallback(mMediaCallback);

            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_NEXT);
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_PLAY);
            filter.addAction(ACTION_PREV);
            filter.addAction(ACTION_STOP);

            mPlaybackService.registerReceiver(this, filter);

            mPlaybackService.startForeground(NOTIFICATION_ID, notification);
            mStarted = true;
        }
    }

    public void stopNotification(){
        if (!mStarted){
            return;
        }

        mStarted = false;
        mController.unregisterCallback(mMediaCallback);

        mNotificationManager.cancel(NOTIFICATION_ID);
        mPlaybackService.unregisterReceiver(this);

        mPlaybackService.stopForeground(true);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        switch(action){
                case ACTION_NEXT:
                    mTransportControls.skipToNext();
                break;

                case ACTION_PAUSE:
                    mTransportControls.pause();
                break;

                case ACTION_PLAY:
                    mTransportControls.play();
                break;

                case ACTION_PREV:
                    mTransportControls.skipToPrevious();
                break;

                case ACTION_STOP:
                    mTransportControls.stop();
            break;

            default:
            break;
        }
    }

    private void updateSessionToken() throws RemoteException {
        MediaSessionCompat.Token token = mPlaybackService.getSessionToken();
        if (mSessionToken == null && token != null ||
                mSessionToken != null && !mSessionToken.equals(token)) {
            if (mController != null) {
                mController.unregisterCallback(mMediaCallback);
            }
            mSessionToken = token;
            if (mSessionToken != null) {
                mController = new MediaControllerCompat(mPlaybackService, mSessionToken);
                mTransportControls = mController.getTransportControls();
                if (mStarted) {
                    mController.registerCallback(mMediaCallback);
                }
            }
        }
    }

    private PendingIntent createContentIntent(){
        Intent intent = new Intent(mPlaybackService, MainActivity.class);
        intent.putExtra("EXTRA_MAIN_ACTION", "ACTION");
        intent.setAction(Intent.ACTION_MAIN);

        return PendingIntent.getActivity(mPlaybackService, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private final MediaControllerCompat.Callback mMediaCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            mPlaybackState = state;

            if ( (state.getState() == PlaybackStateCompat.STATE_NONE) ||
                    (state.getState() == PlaybackStateCompat.STATE_STOPPED) ){
                stopNotification();
            }
            else{
                Notification notification = createNotification();
                if (notification != null){
                    mNotificationManager.notify(NOTIFICATION_ID, notification);
                }
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            mMetadata = metadata;

            Notification notification = createNotification();
            if (notification != null){
                mNotificationManager.notify(NOTIFICATION_ID, notification);
            }
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();

            try {
                updateSessionToken();
            }
            catch (RemoteException e){

            }

        }
    };

    private Notification createNotification(){
        if ( (mMetadata == null) || (mPlaybackState == null) ){
            return null;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mPlaybackService, CHANNEL_ID);

        builder.addAction(R.drawable.icon_previous_not_pressed, mPlaybackService.getString(R.string.previus), mPreviousIntent);

        if (mPlaybackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            builder.addAction(R.drawable.icon_pause_not_pressed, mPlaybackService.getString(R.string.pause), mPauseIntent);
        }
        else {
            builder.addAction(R.drawable.icon_play_not_pressed, mPlaybackService.getString(R.string.play), mPlayIntent);
        }

        builder.addAction(R.drawable.icon_stop_not_pressed, mPlaybackService.getString(R.string.stop), mStopIntent);
        builder.addAction(R.drawable.icon_next_not_pressed, mPlaybackService.getString(R.string.next), mNextIntent);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            createNotificationChannel();
        }

        MediaDescriptionCompat descriptionTrack = mMetadata.getDescription();
        Bitmap iconMediaNotify = BitmapFactory.decodeResource(mPlaybackService.getResources(), R.drawable.ic_big_logo_900x900);

        builder
                .setContentTitle(descriptionTrack.getTitle())
                .setContentText(descriptionTrack.getSubtitle())
                .setContentIntent(mController.getSessionActivity())
                .setDeleteIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(mPlaybackService, PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle()
                .setShowCancelButton(true)
                .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mPlaybackService, PlaybackStateCompat.ACTION_STOP))
                .setMediaSession(mPlaybackService.getSessionToken()))
                .setSmallIcon(R.drawable.exo_notification_small_icon)
                .setLargeIcon(iconMediaNotify)
                .setShowWhen(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setContentIntent(createContentIntent());

            return builder.build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationManager notificationManager = mPlaybackService.getSystemService(NotificationManager.class);
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, mPlaybackService.getString(R.string.notify_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(mPlaybackService.getString(R.string.notify_player_desc));
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
