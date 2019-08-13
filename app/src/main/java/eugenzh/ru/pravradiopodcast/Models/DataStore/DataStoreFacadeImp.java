package eugenzh.ru.pravradiopodcast.Models.DataStore;

import eugenzh.ru.pravradiopodcast.Common.TypeSourceItems;

public class DataStoreFacadeImp implements DataStoreFacade {
    @Override
    public DataStoreCategory getDataStoreCategory(TypeSourceItems typeSource) {
        return CategoriesStoreFactory.getCategories(typeSource);
    }

    @Override
    public DataStorePodcast getDataStorePodcast(TypeSourceItems typeSource) {
        return PodcastsStoreFactory.getPodcasts(typeSource);
    }
}
