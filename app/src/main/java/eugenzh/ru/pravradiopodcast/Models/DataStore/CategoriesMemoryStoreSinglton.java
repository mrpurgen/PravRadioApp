package eugenzh.ru.pravradiopodcast.Models.DataStore;

import eugenzh.ru.pravradiopodcast.Models.Repository.MemoryRepository;
import eugenzh.ru.pravradiopodcast.Models.Repository.Repository;

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
