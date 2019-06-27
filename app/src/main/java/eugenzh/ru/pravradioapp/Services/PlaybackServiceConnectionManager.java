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
    private ServiceConnectionCallback mCallback;


    public ServiceConnection connection(Context context){
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                PlaybackService.PlayerServiceBinder binder = (PlaybackService.PlayerServiceBinder)iBinder;
                try {
                    mMediaController = new MediaControllerCompat(context, binder.getMediaSessionToken());

                    if (mCallback != null) {
                        mCallback.onSuccConnection();
                    }
                } catch (RemoteException e) {
                    if (mCallback != null) {
                        mCallback.onFailConnection(e);
                    }
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

    public void setCallback(ServiceConnectionCallback callback){
        mCallback = callback;
    }

    public void disconnection(Context context, ServiceConnection connection){
        context.unbindService(connection);
    }

    public MediaControllerCompat getMediaController() {
        return mMediaController;
    }


    public interface ServiceConnectionCallback{
        void onSuccConnection();
        void onFailConnection(RemoteException e);
    }
}
