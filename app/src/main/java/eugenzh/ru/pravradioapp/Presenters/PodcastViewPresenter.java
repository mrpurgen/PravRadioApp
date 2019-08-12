package eugenzh.ru.pravradioapp.Presenters;

import android.Manifest;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataStore.CategoriesStoreFactory;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreCategory;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreFacade;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStoreFacadeImp;
import eugenzh.ru.pravradioapp.Models.DataStore.DataStorePodcast;
import eugenzh.ru.pravradioapp.Models.DataStore.Observer.DataStoreObserver;
import eugenzh.ru.pravradioapp.Models.DataStore.Observer.SelectedItemObserver;
import eugenzh.ru.pravradioapp.Models.DataStore.PodcastsStoreFactory;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Item;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;
import eugenzh.ru.pravradioapp.R;
import eugenzh.ru.pravradioapp.Services.DownloadService;
import eugenzh.ru.pravradioapp.Services.PlaybackServiceConnectionManager;
import eugenzh.ru.pravradioapp.View.CustomToast;

@InjectViewState
public class PodcastViewPresenter extends ItemViewPresenter implements DataStoreObserver,
                                                                       SelectedItemObserver,
                                                                       PlaybackServiceConnectionManager.ServiceConnectionCallback{

    private DataStorePodcast mDataStorePodcast;
    private DataStoreFacade mDataStore;
    private long mCategoryID = 0;
    private int mItemDownloadingPosition = 0;
    private Context mContext;
    private MediaControllerCompat mMediaController;
    PlaybackServiceConnectionManager mServiceConnectionManager;
    private ServiceConnection mServiceConnection;
    private long mPlayablePodcastId = 0;

    public PodcastViewPresenter(TypeSourceItems type, Context ctx) {
        super(type);

        mContext = ctx;

        mDataStore = new DataStoreFacadeImp();
        mDataStorePodcast = mDataStore.getDataStorePodcast(type);

        mDataStorePodcast = PodcastsStoreFactory.getPodcasts(typeSourceItems);
        mDataStorePodcast.subscripEventUpdateView(this);
        mDataStorePodcast.subscripEventUpdateSelectedItem(this);

        DataStoreCategory repositoryCategory = CategoriesStoreFactory.getCategories(typeSourceItems);
        mCategoryID = repositoryCategory.getSelectedItemID();
        mPlayablePodcastId = mDataStorePodcast.getSelectedItemID();

        connectToPlaybackService();
    }

    private void connectToPlaybackService(){
        mServiceConnectionManager = new PlaybackServiceConnectionManager();
        mServiceConnection = mServiceConnectionManager.connection(mContext);
        mServiceConnectionManager.setCallback(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        updateContent();
    }


    @Override
    public void onDestroy() {
        mDataStorePodcast.unsubscripEventUpdateView(this);
        mDataStorePodcast.unsubscripEventUpdateSelectedItem(this);
        mServiceConnectionManager.disconnection(mContext, mServiceConnection);
    }

    @Override
    public void onClick(int position) {
        Podcast podcast = mDataStorePodcast.getItem(position);
        Bundle bundle = new Bundle();

        bundle.putSerializable("TYPE_SOURCE", typeSourceItems);

        mMediaController.getTransportControls().playFromMediaId(String.valueOf(podcast.getId()), bundle);
    }

    @Override
    public void updateContent() {
        getViewState().showWaitLoad();
        mDataStorePodcast.update(mCategoryID);
    }

    @Override
    public void onLongClick(View view, int position) {
        if (typeSourceItems == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER) {
            getViewState().showPopupPodcast(view, position);
        }
    }

    @Override
    public <T extends Item> void update(RequestResult result, List<T> list) {
        if (result == RequestResult.REQUEST_RESUTL_SUCC) {
            getViewState().hideWaitLoad();
            getViewState().updateList((List<Item>)list);

            updatePlayablePosition();
        }
        else if (result == RequestResult.REQUEST_RESULT_FAIL_NETWORK){
            getViewState().hideWaitLoad();
            getViewState().showDiagMsg(mContext.getString(R.string.msg_error_network));
        }
    }

    @Override
    public void update(long selectedItemdID) {
        mPlayablePodcastId = selectedItemdID;
        updatePlayablePosition();
    }

    private void updatePlayablePosition(){
        int position = mDataStorePodcast.getPositionViewListById(mPlayablePodcastId);
        getViewState().updatePlayablePosition(position);
    }

    public void downloadPodcast(Context context, int position){
        mItemDownloadingPosition = position;
        boolean permissionWriteStorageExist = checkPermissionWriteStorage(context);

        if(permissionWriteStorageExist){
            startDownloading(context);
        }
        else {
            getViewState().requestPermission(REQUEST_PERMISSION_WRITE_STORAGE_CODE);
        }
    }

    private boolean checkPermissionWriteStorage(Context context){
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void handlerResultRequestPermissionWriteStorage(Context context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == REQUEST_PERMISSION_WRITE_STORAGE_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownloading(context);
            }
            else{
                getViewState().showFailRequestPermissionWriteStorage();
            }
        }
    }

    private void startDownloading(Context context){
        Podcast podcast = mDataStorePodcast.getItem(mItemDownloadingPosition);

        DataStoreCategory dataStoreCategory = mDataStore.getDataStoreCategory(typeSourceItems);
        Category category = dataStoreCategory.getItemById(mCategoryID);

        DownloadService.startService(context, podcast.getUrl(), category.getName(), podcast.getName() + ".mp3");
    }

    @Override
    public void onSuccConnection() {
        mMediaController = mServiceConnectionManager.getMediaController();
    }

    @Override
    public void onFailConnection(RemoteException e) {
        CustomToast.showMessage(mContext, e.getMessage());
    }

}
