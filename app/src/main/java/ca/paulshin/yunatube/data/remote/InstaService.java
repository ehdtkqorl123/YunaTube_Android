package ca.paulshin.yunatube.data.remote;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.data.model.instagram.Feed;
import ca.paulshin.yunatube.util.LogUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;
import timber.log.Timber;

public interface InstaService {

    @GET("v1/users/{user}/media/recent/")
    Observable<Feed> getFeedItemByUser(
            @Path("user") String user,
            @QueryMap Map<String, String> options
    );

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static InstaService newInstaService() {
            // Set logger
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor((message) -> Timber.tag("OkHttp").d(message));
            interceptor.setLevel(LogUtil.getNetworkLogLevel());

            // Set timeout
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.connectTimeout(3, TimeUnit.SECONDS);
            clientBuilder.readTimeout(3, TimeUnit.SECONDS);
            clientBuilder.writeTimeout(3, TimeUnit.SECONDS);
            clientBuilder.addInterceptor(interceptor);
            clientBuilder.addNetworkInterceptor(new StethoInterceptor());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Config.BASE_INSTA_URL_HTTP)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(clientBuilder.build())
                    .build();
            return retrofit.create(InstaService.class);
        }
    }
}
