package eugenzh.ru.pravradiopodcast.Models.DataStore;

import eugenzh.ru.pravradiopodcast.Common.TypeSourceItems;

public class PodcastsStoreFactory {
    public static DataStorePodcast getPodcasts(TypeSourceItems typeSource){
        if (typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            return PodcastsMemoryStoreSinglton.getInstance();
        }
        else if (typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            return PodcastsServerStoreSinglton.getInstance();
        }
         ///TODO: add throw exception
        return null;
    }
}
