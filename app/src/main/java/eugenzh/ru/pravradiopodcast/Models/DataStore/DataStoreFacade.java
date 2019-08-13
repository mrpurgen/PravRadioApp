package eugenzh.ru.pravradiopodcast.Models.DataStore;

import eugenzh.ru.pravradiopodcast.Common.TypeSourceItems;

public interface DataStoreFacade {
    DataStoreCategory getDataStoreCategory(TypeSourceItems typeSource);
    DataStorePodcast getDataStorePodcast(TypeSourceItems typeSource);
}
