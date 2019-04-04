package eugenzh.ru.pravradioapp.Models.DataView;

import java.util.List;

import eugenzh.ru.pravradioapp.Models.Item.Podcast;
import eugenzh.ru.pravradioapp.Models.Repository.Repository;
import eugenzh.ru.pravradioapp.Models.Repository.ServerRepository;

public class PodcastsServerSinglton extends DateViewPodcast{
    Repository repository = new ServerRepository();
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
