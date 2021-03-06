package com.pgizka.gsenger;


import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;

import com.pgizka.gsenger.api.ApiModule;
import com.pgizka.gsenger.api.dtos.messages.ReceiverInfoData;
import com.pgizka.gsenger.config.ApplicationModule;
import com.pgizka.gsenger.config.GSengerApplication;
import com.pgizka.gsenger.config.RepositoryModule;
import com.pgizka.gsenger.dagger.DaggerTestApplicationComponent;
import com.pgizka.gsenger.dagger.TestApiModule;
import com.pgizka.gsenger.dagger.TestApplicationComponent;
import com.pgizka.gsenger.api.dtos.messages.MessageData;
import com.pgizka.gsenger.dagger.TestApplicationModule;
import com.pgizka.gsenger.dagger.TestRepositoryModule;
import com.pgizka.gsenger.provider.Chat;
import com.pgizka.gsenger.provider.ChatRepository;
import com.pgizka.gsenger.provider.Message;
import com.pgizka.gsenger.provider.ReceiverInfo;
import com.pgizka.gsenger.provider.Repository;
import com.pgizka.gsenger.provider.TextMessage;
import com.pgizka.gsenger.provider.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestUtils {

    public static GSengerApplication getApplication() {
        return (GSengerApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    public static void setupRealm() {
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder(getApplication())
                .inMemory()
                .name("test.realm")
                .deleteRealmIfMigrationNeeded()
                .build());

        Realm realm = Realm.getDefaultInstance();
        if (!realm.isInTransaction()) {
            realm.beginTransaction();
        }
        realm.deleteAll();
        realm.commitTransaction();
    }

    public static TestApplicationComponent getDefaultTestApplicationComponent() {
        return DaggerTestApplicationComponent.builder()
                .applicationModule(new TestApplicationModule(getApplication()))
                .apiModule(new TestApiModule())
                .repositoryModule(new RepositoryModule(getApplication()))
                .build();
    }

    public static TestApplicationComponent getTestApplicationComponent() {
        return DaggerTestApplicationComponent.builder()
                .applicationModule(new TestApplicationModule(getApplication()))
                .apiModule(new TestApiModule())
                .repositoryModule(new RepositoryModule(getApplication()))
                .build();
    }

    public static TestApplicationComponent getTestApplicationComponent(
            ApplicationModule applicationModule, ApiModule apiModule, RepositoryModule repositoryModule) {
        return DaggerTestApplicationComponent.builder()
                .applicationModule(applicationModule)
                .apiModule(apiModule)
                .repositoryModule(repositoryModule)
                .build();
    }

    public static User getOrCreateOwner() {
        Realm realm = Realm.getDefaultInstance();

        User owner = realm.where(User.class)
                .equalTo("id", 0)
                .findFirst();

        if (owner != null) {
            return owner;
        }

        owner = new User();
        owner.setId(0);
        owner.setServerId(0);
        owner.setUserName("Owner");
        owner.setStatus("my super status");
        realm.beginTransaction();
        owner = realm.copyToRealm(owner);
        realm.commitTransaction();

        return owner;
    }

    public static User createUser() {
        Realm realm = Realm.getDefaultInstance();

        User user = createNotPersistedUser();
        realm.beginTransaction();
        user = realm.copyToRealm(user);
        realm.commitTransaction();

        return user;
    }

    public static User createNotPersistedUser() {
        Repository repository = GSengerApplication.getApplicationComponent().repository();

        User user = new User();
        user.setId(repository.getUserNextId());
        user.setServerId(repository.getUserNextId());
        user.setUserName("Pawel");
        user.setStatus("my super status");
        return user;
    }

    public static Chat createChatBetweenUsers(User firstUser, User secondUser) {
        ChatRepository chatRepository = GSengerApplication.getApplicationComponent().chatRepository();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Chat chat = chatRepository.createSingleConversationChatBetweenUsers(firstUser, secondUser);
        realm.commitTransaction();
        return chat;
    }

    public static Chat createGroupChat(List<User> participants) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        Chat chat = new Chat();
        chat.setId(0);
        chat.setServerId(1);
        chat.setChatName("Sample chat name");
        chat.setStartedDate(System.currentTimeMillis());
        chat.setType(Chat.Type.GROUP.code);

        chat = realm.copyToRealm(chat);

        RealmList<User> realmList = new RealmList<>();
        for (User participant : participants) {
            realmList.add(participant);
            participant.getChats().add(chat);
        }

        chat.setUsers(realmList);
        realm.commitTransaction();

        return chat;
    }

    public static Message createMessageWithReceiverInfo(Chat chat, User sender, User receiver) {
        Repository repository = GSengerApplication.getApplicationComponent().repository();
        Realm realm = Realm.getDefaultInstance();
        User owner = getOrCreateOwner();

        realm.beginTransaction();

        Message message = new Message();
        message.setId(repository.getMessageNextId());
        message.setServerId(repository.getMessageNextId());
        message.setSendDate(System.currentTimeMillis());
        message.setState(Message.State.RECEIVED.code);
        message = realm.copyToRealm(message);

        message.setSender(sender);
        message.setChat(chat);
        chat.getMessages().add(message);

        ReceiverInfo receiverInfo = new ReceiverInfo();
        receiverInfo = realm.copyToRealm(receiverInfo);
        receiverInfo.setMessage(message);
        receiverInfo.setUser(owner);
        message.getReceiverInfos().add(receiverInfo);

        realm.commitTransaction();
        return message;
    }

    public static MessageData prepareMessageData(Chat chat, User sender, int messageServerId) {
        MessageData messageData = prepareMessageData(sender, messageServerId);
        messageData.setChatId(chat.getServerId());
        return messageData;
    }

    public static MessageData prepareMessageData(User sender, int messageServerId) {
        MessageData messageData = new MessageData();
        prepareMessageData(messageData, sender, messageServerId);
        return messageData;
    }

    public static void prepareMessageData(MessageData messageData, User sender, int messageServerId) {
        User user = new User(sender);
        messageData.setSender(user);
        messageData.setSendDate(System.currentTimeMillis());
        messageData.setMessageId(messageServerId);
        messageData.setChatId(-1);
    }

    public static List<ReceiverInfoData> prepareReceiversInfoData(int messageServerId, List<User> receivers, long deliveredDate) {
        List<ReceiverInfoData> receiversData = new ArrayList<>();
        for (User receiver : receivers) {
            ReceiverInfoData receiverInfoData = new ReceiverInfoData();
            receiverInfoData.setReceiverId(receiver.getServerId());
            receiverInfoData.setDeliveredDate(deliveredDate);

            List<Integer> messagesIds = new ArrayList<>();
            messagesIds.add(messageServerId);
            receiverInfoData.setMessagesIds(messagesIds);

            receiversData.add(receiverInfoData);
        }
        return receiversData;
    }

    public static <T> Call<T> createCall(T response) {
        return new MockCall<>(response);
    }

    public static <T> Call<T> createCall(int responseCode, T response) {
        return new MockCall<>(response, responseCode);
    }

    private static class MockCall<T> implements Call<T> {

        final T mResponse;

        final int mCode;

        public MockCall(T response, int code) {
            mResponse = response;
            mCode = code;
        }

        public MockCall(T response) {
            this(response, 200);
        }

        @Override
        public Response<T> execute() throws IOException {
            return buildResponse();
        }

        @Override
        public void enqueue(Callback<T> callback) {
            if (mCode > 199 && mCode < 300) {
                callback.onResponse(this, buildResponse());
            } else {
                callback.onFailure(this, new Exception());
            }
        }

        @NonNull
        private Response<T> buildResponse() {
            if (mCode > 199 && mCode < 300) {
                return Response.success(mResponse);
            } else {
                return Response.error(mCode, null);
            }
        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {

        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @SuppressWarnings("CloneDoesntCallSuperClone")
        @Override
        public Call<T> clone() {
            return new MockCall<>(mResponse);
        }

        @Override
        public Request request() {
            return null;
        }
    }

}
