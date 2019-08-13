package eugenzh.ru.pravradiopodcast.Models.DataStore;


import eugenzh.ru.pravradiopodcast.Models.Repository.MemoryRepository;
import eugenzh.ru.pravradiopodcast.Models.Repository.Repository;

public class PodcastsMemoryStoreSinglton extends DataStorePodcast {
    private static final PodcastsMemoryStoreSinglton ourInstance = new PodcastsMemoryStoreSinglton();

    public static PodcastsMemoryStoreSinglton getInstance() {
        return ourInstance;
    }

    private PodcastsMemoryStoreSinglton() {
    }

    @Override
    Repository createRepositoryLoader() {
        return new MemoryRepository();
    }
}
