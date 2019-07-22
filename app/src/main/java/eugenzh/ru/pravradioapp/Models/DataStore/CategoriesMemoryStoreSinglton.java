package eugenzh.ru.pravradioapp.Models.DataStore;

import eugenzh.ru.pravradioapp.Models.Repository.MemoryRepository;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;

public class CategoriesMemoryStoreSinglton extends DataStoreCategory {
    private static final CategoriesMemoryStoreSinglton ourInstance = new CategoriesMemoryStoreSinglton();

    public static CategoriesMemoryStoreSinglton getInstance() {
        return ourInstance;
    }

    private CategoriesMemoryStoreSinglton() {
    }

    @Override
    Repository createRepositoryLoader() {
        return new MemoryRepository();
    }
}
