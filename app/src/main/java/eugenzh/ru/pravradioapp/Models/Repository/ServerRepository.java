package eugenzh.ru.pravradioapp.Models.Repository;

import java.util.List;

import eugenzh.ru.pravradioapp.Models.HttpConnection.BuildHttpConnection;
import eugenzh.ru.pravradioapp.Models.HttpConnection.CategoryHttpAPI;
import eugenzh.ru.pravradioapp.Models.Item.Category;
import eugenzh.ru.pravradioapp.Models.Item.Item;
import eugenzh.ru.pravradioapp.Models.Item.Podcast;
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

    }

    private <T extends Item> void loader(Call<List<T>> list, final HandlerRequestItems handler) {


        list.enqueue(new Callback<List<T>>() {
            @Override
            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                handler.onSuccRequestItems(response.body());
            }

            @Override
            public void onFailure(Call<List<T>> call, Throwable t) {
                System.out.print("wtf");
            }
        });
    }


}
