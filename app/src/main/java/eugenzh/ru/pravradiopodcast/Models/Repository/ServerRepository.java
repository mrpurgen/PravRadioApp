package eugenzh.ru.pravradiopodcast.Models.Repository;

import java.util.List;

import eugenzh.ru.pravradiopodcast.Common.RequestResult;
import eugenzh.ru.pravradiopodcast.Models.HttpConnection.BuildHttpConnection;
import eugenzh.ru.pravradiopodcast.Models.HttpConnection.CategoryHttpAPI;
import eugenzh.ru.pravradiopodcast.Models.HttpConnection.PodcastHttpAPI;
import eugenzh.ru.pravradiopodcast.Models.Item.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServerRepository implements Repository{

    BuildHttpConnection connection = BuildHttpConnection.getInstance();

    public ServerRepository(){
        connection.buildConnection("http://81.200.119.172:8080/");
    }

    @Override
    public void requestCategories(HandlerRequestItems handler) {
        CategoryHttpAPI categoryAPI = connection.getRetrofit().create(CategoryHttpAPI.class);
        loader(categoryAPI.getCategories(), handler);
    }

    @Override
    public void requestPodcasts(long categoryId, HandlerRequestItems handler) {
        PodcastHttpAPI podcastAPI = connection.getRetrofit().create(PodcastHttpAPI.class);
        loader(podcastAPI.getPodcastAll(categoryId), handler);
    }

    private <T extends Item> void loader(Call<List<T>> list, final HandlerRequestItems handler) {
        list.enqueue(new Callback<List<T>>() {
            @Override
            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                handler.onSuccRequestItems(response.body());
            }

            @Override
            public void onFailure(Call<List<T>> call, Throwable t) {
               handler.onFailRequestResultItem(RequestResult.REQUEST_RESULT_FAIL_NETWORK);
            }
        });
    }


}
