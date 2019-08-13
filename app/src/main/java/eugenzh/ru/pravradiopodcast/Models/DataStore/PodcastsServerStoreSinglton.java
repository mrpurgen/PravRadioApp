package eugenzh.ru.pravradiopodcast.Models.DataStore;

import eugenzh.ru.pravradiopodcast.Models.Repository.Repository;
import eugenzh.ru.pravradiopodcast.Models.Repository.ServerRepository;

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
