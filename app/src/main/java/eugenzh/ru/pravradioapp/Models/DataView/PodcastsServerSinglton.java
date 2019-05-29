package eugenzh.ru.pravradioapp.Models.DataView;

import eugenzh.ru.pravradioapp.Models.Repository.Repository;
import eugenzh.ru.pravradioapp.Models.Repository.ServerRepository;

public class PodcastsServerSinglton extends DateViewPodcast{
    private static final PodcastsServerSinglton ourInstance = new PodcastsServerSinglton();

    public static PodcastsServerSinglton getInstance() {
        return ourInstance;
    }

    private PodcastsServerSinglton() {
    }

    @Override
    Repository createRepositoryLoader() {
        return new ServerRepository();
    }
}
