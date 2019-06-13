package eugenzh.ru.pravradioapp.Presenters;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Common.TypeSourceItems;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesDateViewFactory;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewCategory;
import eugenzh.ru.pravradioapp.Models.DataView.DateViewPodcast;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewObserver;
import eugenzh.ru.pravradioapp.Models.DataView.Observer.DateViewSubject;
import eugenzh.ru.pravradioapp.Models.DataView.PodcastsDateViewFactory;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Item;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;
import eugenzh.ru.pravradioapp.Services.DownloadService;

@InjectViewState
public class PodcastViewPresenter extends ItemViewPresenter implements DateViewObserver {

    private DateViewSubject mSubject;
    private DateViewPodcast mRepository;
    private long mCategoryID = 0;
    private int mItemDownloadingPosition = 0;
    private Context mContext;

    public PodcastViewPresenter(TypeSourceItems type, Context ctx) {
        super(type);

        mContext = ctx;
        mSubject = PodcastsDateViewFactory.getPodcasts(typeSourceItems);
        mRepository = PodcastsDateViewFactory.getPodcasts(typeSourceItems);
        mSubject.subscripEventUpdateView(this);

        DateViewCategory repositoryCategory = CategoriesDateViewFactory.getCategories(typeSourceItems);
        mCategoryID = repositoryCategory.getSelectedItemID();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        updateContent();
    }


    @Override
    public void onDestroy() {
        mSubject.unsubscripEventUpdateView(this);
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void updateContent() {
        getViewState().showWaitLoad();
        mRepository.update(mCategoryID);
    }

    @Override
    public void onLongClick(View view, int position) {
        getViewState().showPopupPodcast(view, position);
    }

    @Override
    public <T extends Item> void update(RequestResult result, List<T> list) {
        if (result == RequestResult.REQUEST_RESUTL_SUCC) {
            getViewState().hideWaitLoad();
            getViewState().updateList((List<Item>)list);
        }
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
        Podcast podcast = mRepository.getItem(mItemDownloadingPosition);

        DateViewCategory repositoryCategory = CategoriesDateViewFactory.getCategories(typeSourceItems);
        Category category = repositoryCategory.getItemToId(mCategoryID);

        DownloadService.startService(context, podcast.getUrl(), category.getName(), podcast.getName() + ".mp3");
    }
}
