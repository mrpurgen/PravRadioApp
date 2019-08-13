package eugenzh.ru.pravradiopodcast.Models.DataStore;

import eugenzh.ru.pravradiopodcast.Models.Repository.Repository;
import eugenzh.ru.pravradiopodcast.Models.Repository.ServerRepository;

public class CategoriesServerStoreSinglton extends DataStoreCategory
{
    private static final CategoriesServerStoreSinglton ourInstance = new CategoriesServerStoreSinglton();

    public static CategoriesServerStoreSinglton getInstance() {
        return ourInstance;
    }

    private CategoriesServerStoreSinglton() {

    }

    @Override
    Repository createRepositoryLoader() {
        return new ServerRepository();
    }
}
