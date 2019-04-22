package eugenzh.ru.pravradioapp.Models.DataView;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;

public class PodcastsDateViewFactory {
    public static DateViewPodcast getPodcasts(TypeSourceItems typeSource){
        if (typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_MEMORY){
            return PodcastsMemorySinglton.getInstance();
        }
        else if (typeSource == TypeSourceItems.TYPE_SOURCE_ITEMS_SERVER){
            return PodcastsServerSinglton.getInstance();
        }
         ///TODO: add throw exception
        return null;
    }
}
