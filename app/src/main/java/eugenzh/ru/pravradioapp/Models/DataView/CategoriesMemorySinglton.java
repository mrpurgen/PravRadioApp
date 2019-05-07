package eugenzh.ru.pravradioapp.Models.DataView;

import eugenzh.ru.pravradioapp.Models.Repository.MemoryRepository;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;

public class CategoriesMemorySinglton extends DateViewCategory{
    private static final CategoriesMemorySinglton ourInstance = new CategoriesMemorySinglton();

    public static CategoriesMemorySinglton getInstance() {
        return ourInstance;
    }

    private CategoriesMemorySinglton() {
    }

    @Override
    Repository createRepositoryLoader() {
        return new MemoryRepository();
    }
}
