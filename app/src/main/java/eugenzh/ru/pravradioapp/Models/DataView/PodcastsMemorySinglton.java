package eugenzh.ru.pravradioapp.Models.DataView;

import java.util.List;

import eugenzh.ru.pravradioapp.Models.Item.Podcast;
import eugenzh.ru.pravradioapp.Models.Repository.MemoryRepository;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;

public class PodcastsMemorySinglton extends DateViewPodcast{
    private static final PodcastsMemorySinglton ourInstance = new PodcastsMemorySinglton();

    public static PodcastsMemorySinglton getInstance() {
        return ourInstance;
    }

    private PodcastsMemorySinglton() {
    }

    @Override
    Repository createRepositoryLoader() {
        return new MemoryRepository();
    }
}
