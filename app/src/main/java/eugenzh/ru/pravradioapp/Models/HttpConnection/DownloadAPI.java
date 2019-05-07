package eugenzh.ru.pravradioapp.Models.HttpConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface DownloadAPI {
    @GET
    @Streaming
    Call<ResponseBody> downloadItem(@Url String url);
}
