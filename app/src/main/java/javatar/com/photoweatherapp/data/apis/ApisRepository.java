package javatar.com.photoweatherapp.data.apis;

import io.reactivex.Single;
import javatar.com.photoweatherapp.data.models.WeatherInfo;
import javatar.com.photoweatherapp.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApisRepository {
    private static ApisRepository ourInstance;

    public static ApisRepository getInstance() {
        if (ourInstance == null){
            ourInstance = new ApisRepository();
        }
        return ourInstance;
    }

    private ApisRepository() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build();

        apis = retrofit.create(Apis.class);
    }

    private final Apis apis;

    public Single<WeatherInfo> weather(double lat,double lon){
        return apis.weather(lat,lon,Constants.API_KEY);
    }
}
