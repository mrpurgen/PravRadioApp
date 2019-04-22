package eugenzh.ru.pravradioapp.Models.DataView;

import eugenzh.ru.pravradioapp.Models.Repository.Repository;
import eugenzh.ru.pravradioapp.Models.Repository.ServerRepository;

public class CategoriesServerSinglton extends DateViewCategory
{
    private static final CategoriesServerSinglton ourInstance = new CategoriesServerSinglton();

    public static CategoriesServerSinglton getInstance() {
        return ourInstance;
    }

    private CategoriesServerSinglton() {

    }

    @Override
    Repository createRepositoryLoader() {
        return new ServerRepository();
    }
}
