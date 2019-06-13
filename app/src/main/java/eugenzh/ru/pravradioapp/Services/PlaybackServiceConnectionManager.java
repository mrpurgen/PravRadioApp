package eugenzh.ru.pravradioapp.Services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;

public class PlaybackServiceConnectionManager {
    private MediaControllerCompat mMediaController;

    public ServiceConnection connection(Context context){
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PlaybackService.PlayerServiceBinder binder = (PlaybackService.PlayerServiceBinder)iBinder;
                try {
                    mMediaController = new MediaControllerCompat(context, binder.getMediaSessionToken());
                } catch (RemoteException e) {
                    ///TODO
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mMediaController = null;
            }
        };

        Intent startServiceIntent = new Intent(context, PlaybackService.class);
        context.bindService(startServiceIntent, connection, Context.BIND_AUTO_CREATE);

        return connection;
    }

    public void disconnection(Context context, ServiceConnection connection){
        context.unbindService(connection);
    }

    public MediaControllerCompat getMediaController() {
        return mMediaController;
    }
}
