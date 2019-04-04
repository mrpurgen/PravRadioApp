package eugenzh.ru.pravradioapp.Models.HttpConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuildHttpConnection {
    private Retrofit mRetrofit;
    private static BuildHttpConnection sBuildHttpConnection = new BuildHttpConnection();

    private BuildHttpConnection(){}

    public void buildConnection(String baseURL){

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static BuildHttpConnection getInstance(){
        return sBuildHttpConnection;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }
}
