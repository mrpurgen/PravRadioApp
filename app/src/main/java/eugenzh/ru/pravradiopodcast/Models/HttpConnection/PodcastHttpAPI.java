package eugenzh.ru.pravradiopodcast.Models.HttpConnection;

import java.util.List;

import eugenzh.ru.pravradiopodcast.Models.Item.Podcast;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PodcastHttpAPI{
    @GET("/podcasts/getList/{categoryId}")
    Call<List<Podcast>> getPodcastAll(@Path("categoryId") Long categoryId);
}
