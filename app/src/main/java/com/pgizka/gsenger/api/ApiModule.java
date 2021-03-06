package com.pgizka.gsenger.api;


import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pgizka.gsenger.provider.User;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmObject;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiModule {

    static final String BASE_URL = "http://192.168.1.5:8080/GSengerGradle-1.0-SNAPSHOT/webresources/";

    @Provides
    @Singleton
    public OkHttpClient provideHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(httpLoggingInterceptor);
        return builder.build();
    }

    @Provides
    public Gson providesGson() {
        return new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        return retrofit;
    }

    @Provides
    @Singleton
    public UserRestService providesUserRestService(Retrofit retrofit) {
        return retrofit.create(UserRestService.class);
    }

    @Provides
    @Singleton
    public MessageRestService providesMessageRestService(Retrofit retrofit) {
        return retrofit.create(MessageRestService.class);
    }

    @Provides
    @Singleton
    public ChatRestService providesChatRestService(Retrofit retrofit) {
        return  retrofit.create(ChatRestService.class);
    }

    public static String buildUserPhotoPath(User user) {
        return BASE_URL + "user/getPhoto/" + user.getServerId();
    }

}
