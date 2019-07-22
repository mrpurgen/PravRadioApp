package eugenzh.ru.pravradioapp.Models.DataStore;

import eugenzh.ru.pravradioapp.Models.Repository.Repository;
import eugenzh.ru.pravradioapp.Models.Repository.ServerRepository;

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
