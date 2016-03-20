package com.pgizka.gsenger.dagger2;

import com.path.android.jobqueue.JobManager;
import com.pgizka.gsenger.api.ApiModule;
import com.pgizka.gsenger.api.UserRestService;
import com.pgizka.gsenger.conversationView.ConversationModel;
import com.pgizka.gsenger.conversationView.ConversationPresenter;
import com.pgizka.gsenger.jobqueue.refreshFriends.RefreshFriendsJob;
import com.pgizka.gsenger.mainView.chats.ChatsToDisplayModel;
import com.pgizka.gsenger.mainView.friends.FriendsPresenter;
import com.pgizka.gsenger.provider.ProviderUtils;
import com.pgizka.gsenger.provider.repositories.ChatRepository;
import com.pgizka.gsenger.provider.repositories.CommonTypeRepository;
import com.pgizka.gsenger.provider.repositories.FriendHasChatRepository;
import com.pgizka.gsenger.provider.repositories.FriendRepository;
import com.pgizka.gsenger.provider.repositories.MediaRepository;
import com.pgizka.gsenger.provider.repositories.MessageRepository;
import com.pgizka.gsenger.provider.repositories.ToFriendRepository;
import com.pgizka.gsenger.util.ContactsUtil;
import com.pgizka.gsenger.util.UserAccountManager;
import com.pgizka.gsenger.welcome.registration.RegistrationTask;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {ApplicationModule.class, ApiModule.class})
public interface ApplicationComponent {

    ProviderUtils providerUtils();

    FriendRepository friendRepository();

    MessageRepository messageRepository();

    CommonTypeRepository commonTypeRepository();

    MediaRepository mediaRepository();

    ToFriendRepository toFriendRepository();

    FriendHasChatRepository friendHasChatRepository();

    ChatRepository chatRepository();

    EventBus eventBus();

    JobManager jobManager();

    OkHttpClient okHttpClient();

    Retrofit retrofit();

    ContactsUtil contactsUtil();

    UserAccountManager userAccountManager();

    UserRestService userRestService();

    void inject(RefreshFriendsJob refreshFriendsJob);

    void inject(RegistrationTask registrationTask);

    void inject(FriendsPresenter friendsPresenter);

    void inject(ChatsToDisplayModel chatsToDisplayModel);

    void inject(ConversationPresenter conversationPresenter);

    void inject(ConversationModel conversationModel);

}
