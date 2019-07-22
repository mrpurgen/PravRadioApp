package eugenzh.ru.pravradioapp.Models.DataStore;

import eugenzh.ru.pravradioapp.Models.Repository.Repository;
import eugenzh.ru.pravradioapp.Models.Repository.ServerRepository;

public class PodcastsServerStoreSinglton extends DataStorePodcast {
    private static final PodcastsServerStoreSinglton ourInstance = new PodcastsServerStoreSinglton();

    public static PodcastsServerStoreSinglton getInstance() {
        return ourInstance;
    }

    private PodcastsServerStoreSinglton() {
    }

    @Override
    Repository createRepositoryLoader() {
        return new ServerRepository();
    }
}
