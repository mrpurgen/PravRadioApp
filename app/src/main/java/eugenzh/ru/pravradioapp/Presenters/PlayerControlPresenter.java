package eugenzh.ru.pravradioapp.Presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.session.MediaControllerCompat;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import eugenzh.ru.pravradioapp.Services.PlaybackService;
import eugenzh.ru.pravradioapp.Services.PlaybackServiceConnectionManager;
import eugenzh.ru.pravradioapp.View.PlayerControlView;

@InjectViewState
public class PlayerControlPresenter extends MvpPresenter<PlayerControlView> {
    private Context mContext;
    private ServiceConnection mServiceConnection;
    private PlaybackService.PlayerServiceBinder mPlayerServiceBinder;
    private MediaControllerCompat mMediaController;
    PlaybackServiceConnectionManager mServiceConnectionManager;

    public PlayerControlPresenter(Context context){
        mContext = context;
        connectToPlayerService();
    }
    private void connectToPlayerService(){
        mServiceConnectionManager = new PlaybackServiceConnectionManager();
        mServiceConnection = mServiceConnectionManager.connection(mContext);
    }

    public void onPlayPressed(){

        mServiceConnectionManager.getMediaController().getTransportControls().playFromMediaId("1", null);
    }
}
