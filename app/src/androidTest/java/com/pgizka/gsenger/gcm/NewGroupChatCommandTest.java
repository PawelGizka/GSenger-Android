package com.pgizka.gsenger.gcm;


import com.google.gson.Gson;
import com.pgizka.gsenger.dagger.TestApplicationComponent;
import com.pgizka.gsenger.dagger2.GSengerApplication;
import com.pgizka.gsenger.gcm.commands.NewGroupChatCommand;
import com.pgizka.gsenger.gcm.commands.NewTextMessageCommand;
import com.pgizka.gsenger.gcm.data.NewChatData;
import com.pgizka.gsenger.provider.Chat;
import com.pgizka.gsenger.provider.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

import static com.pgizka.gsenger.TestUtils.*;
import static org.junit.Assert.*;

public class NewGroupChatCommandTest {

    private NewGroupChatCommand newGroupChatCommand;

    private GSengerApplication gSengerApplication;

    @Before
    public void setUp() throws IOException {
        setupRealm();
        gSengerApplication = getApplication();
        TestApplicationComponent applicationComponent = getTestApplicationComponent();
        GSengerApplication.setApplicationComponent(applicationComponent);

        newGroupChatCommand = new NewGroupChatCommand();
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testHandlingNewChat() throws Exception {
        User owner = getOrCreateOwner();
        User participant1 = createUser();
        User participant2 = createNotPersistedUser();

        int chatId = 15;
        String chatName = "Sample Chat";
        NewChatData chatData = new NewChatData();
        chatData.setName(chatName);
        chatData.setStartedDate(System.currentTimeMillis());
        chatData.setChatId(chatId);
        List<User> participants = new ArrayList<>();
        participants.add(new User(owner));
        participants.add(new User(participant1));
        participants.add(new User(participant2));
        chatData.setParticipants(participants);

        String data = new Gson().getAdapter(NewChatData.class).toJson(chatData);
        newGroupChatCommand.execute(gSengerApplication, NewChatData.ACTION, data);

        Realm realm = Realm.getDefaultInstance();
        realm.refresh();

        Chat chat = realm.where(Chat.class)
                .equalTo("serverId", chatId)
                .findFirst();

        assertNotNull(chat);
        int numberOfParticipants = 3;
        assertEquals(numberOfParticipants, chat.getUsers().size());
        assertEquals(chatName, chat.getChatName());
    }

}
