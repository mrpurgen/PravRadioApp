package eugenzh.ru.pravradioapp.Models.DataStore;


import eugenzh.ru.pravradioapp.Models.Repository.MemoryRepository;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;

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
