package eugenzh.ru.pravradioapp.Models.Repository;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import eugenzh.ru.pravradioapp.Common.RequestResult;
import eugenzh.ru.pravradioapp.Models.DataView.CategoriesMemorySinglton;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Item;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;
import eugenzh.ru.pravradioapp.Preferences.PreferencesManager;

public class MemoryRepository implements Repository {

    @Override
    public void requestCategories(HandlerRequestItems handler) {
        String pathBaseDir = getBaseDirURL();
        File mainDir = new File(pathBaseDir);
        long id = 1;
        List<Item> listItems = new ArrayList<>();
        File[] dirs = mainDir.listFiles();

        if (dirs == null){
            handler.onFailRequestResultItem(RequestResult.REQUEST_RESULT_FAIL_STORAGE_INACCESSIBLE);
            return;
        }

        for (File dir: dirs){
            Category category = new Category(dir.getName());
            category.setId(id);
            listItems.add(category);
            ++id;
        }

        handler.onSuccRequestItems(listItems);
    }

    @Override
    public void requestPodcasts(long categoryId, HandlerRequestItems handler) {
        String path = getPathToPodcasts(categoryId);
        File dirPodcast = new File(path);
        File[] files = dirPodcast.listFiles();
        List<Item> listItems = new ArrayList<>();
        long id = categoryId * 1000L;

        for (File file: files){
            if (file.isFile()){
                Date date = new Date(file.lastModified());
                Podcast podcast = new Podcast(file.getName());
                podcast.setId(id);
                podcast.setUrl(path + File.separator + file.getName());
                podcast.setDate(date);
                podcast.setCategoryId(categoryId);

                listItems.add(podcast);
                ++id;
            }
        }
        handler.onSuccRequestItems(listItems);
    }

    private String getBaseDirURL(){
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(Environment.getExternalStorageDirectory());
        pathBuilder.append(File.separator);
        pathBuilder.append("PravRadio");
        pathBuilder.append(File.separator);

        return pathBuilder.toString();
    }

    private String getPathToPodcasts(long categoryId){
       StringBuilder pathBuilder = new StringBuilder();

       CategoriesMemorySinglton categorys = CategoriesMemorySinglton.getInstance();
       Category category = categorys.getItemToId(categoryId);

       pathBuilder.append(getBaseDirURL());
       pathBuilder.append(category.getName());
       pathBuilder.append(File.separator);

       return pathBuilder.toString();
    }


}
