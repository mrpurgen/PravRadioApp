package eugenzh.ru.pravradioapp.Models.DataStore;

import eugenzh.ru.pravradioapp.Common.TypeSourceItems;

public interface DataStoreFacade {
    DataStoreCategory getDataStoreCategory(TypeSourceItems typeSource);
    DataStorePodcast getDataStorePodcast(TypeSourceItems typeSource);
}
