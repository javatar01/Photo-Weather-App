package javatar.com.photoweatherapp.data.apis;

import io.reactivex.Single;
import javatar.com.photoweatherapp.data.models.WeatherInfo;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface Apis {
    @GET("weather")
    Single<WeatherInfo> weather(@Query("lat") double lat,
                                @Query("lon") double lon,
                                @Query("appid") String appid);
}
