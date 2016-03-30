package ca.paulshin.yunatube.data.remote;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.data.model.flickr.CollectionsObject;
import ca.paulshin.yunatube.data.model.flickr.PhotosetObject;
import ca.paulshin.yunatube.data.model.flickr.Stream;
import ca.paulshin.yunatube.util.LogUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;
import timber.log.Timber;

public interface FlickrService {

    @GET("/services/feeds/photos_public.gne")
    Observable<Stream> getStream(
            @QueryMap Map<String, String> options
    );

    @GET("/services/rest")
    Observable<CollectionsObject> getCollections(
            @QueryMap Map<String, String> options
    );

    @GET("/services/rest")
    Observable<PhotosetObject> getPhotoList(
            @QueryMap Map<String, String> options
    );

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static FlickrService newFlickrService() {
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
                    .baseUrl(Config.BASE_FLICKR_URL_HTTPS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(clientBuilder.build())
                    .build();
            return retrofit.create(FlickrService.class);
        }
    }
}
