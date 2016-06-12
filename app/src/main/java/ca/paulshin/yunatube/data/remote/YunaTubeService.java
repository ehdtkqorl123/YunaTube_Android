package ca.paulshin.yunatube.data.remote;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ca.paulshin.yunatube.Config;
import ca.paulshin.yunatube.data.model.main.Notice;
import ca.paulshin.yunatube.data.model.message.Message;
import ca.paulshin.yunatube.data.model.video.Comment;
import ca.paulshin.yunatube.data.model.video.Section;
import ca.paulshin.yunatube.data.model.video.SimpleResult;
import ca.paulshin.yunatube.data.model.video.Video;
import ca.paulshin.yunatube.util.LogUtil;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;
import timber.log.Timber;

public interface YunaTubeService {

	/*****
     * Main
     *****/
    @GET("/yunatube/mobile/php/main_{lang}.php")
    Observable<Notice> getNotice(
            @Path("lang") String lang,
            @Query("random") int random
    );

    @GET("/yunatube/mobile/php/get_new_videos.php")
    Observable<List<Video>> getNewVideos(
            @QueryMap Map<String, String> options
    );

    @GET("/yunatube/mobile/php/search.php")
    Observable<List<Video>> getSearchVideos(
            @QueryMap Map<String, String> options
    );

	/*****
	 * Messages
     *****/

    @GET("/yunatube/mobile/php/get_messages.php")
    Observable<List<Message>> getMessages(
            @QueryMap Map<String, String> options
    );

    @GET("/yunatube/mobile/php/submit_message.php")
    Observable<SimpleResult> submitMessage(
            @QueryMap Map<String, String> options
    );

	/*****
     * Videos
     *****/

    @GET("/yunatube/mobile/php/get_detail.php")
    Observable<List<Video>> getVideo(
            @QueryMap Map<String, String> options
    );

    @GET("/yunatube/mobile/php/get_comments.php")
    Observable<List<Comment>> getComments(
            @QueryMap Map<String, String> options
    );

    @GET("/yunatube/mobile/php/submit_comment.php")
    Observable<SimpleResult> submitComment(
            @QueryMap Map<String, String> options
    );

    @GET("/yunatube/mobile/php/report_video_blocked.php")
    Observable<SimpleResult> report(
            @Query("ytid") String ytid
    );

    @GET("/yunatube/mobile/php/get_sections.php")
    Observable<List<Section>> getSections(
            @QueryMap Map<String, String> options
    );

    @GET("/yunatube/mobile/php/get_videos.php")
    Observable<List<Video>> getVideos(
            @QueryMap Map<String, String> options
    );

    @GET("/yunatube/mobile/php/get_random_video.php")
    Observable<List<Video>> getRandomVideo(
            @Query("lo") String lo
    );

    @GET("/yunatube/mobile/php/gifs/get_gifs.php")
    Observable<List<String>> getGifList();

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static YunaTubeService newYunaTubeService() {
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
                    .baseUrl(Config.BASE_URL_HTTP)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(clientBuilder.build())
                    .build();
            return retrofit.create(YunaTubeService.class);
        }
    }
}
