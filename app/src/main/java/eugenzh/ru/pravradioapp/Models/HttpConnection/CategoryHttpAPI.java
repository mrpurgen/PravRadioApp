package eugenzh.ru.pravradioapp.Models.HttpConnection;

import java.util.List;

import eugenzh.ru.pravradioapp.Models.Item.Category;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryHttpAPI{
    @GET("category/getList")
    Call<List<Category>> getCategories();
}
