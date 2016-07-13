package com.pgizka.gsenger.gcm;

import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;
import com.pgizka.gsenger.TestUtils;
import com.pgizka.gsenger.api.MessageRestService;
import com.pgizka.gsenger.api.dtos.messages.MessagesStateChangedRequest;
import com.pgizka.gsenger.config.GSengerApplication;
import com.pgizka.gsenger.dagger.TestApplicationComponent;
import com.pgizka.gsenger.gcm.commands.NewMediaMessageCommand;
import com.pgizka.gsenger.api.dtos.messages.MediaMessageData;
import com.pgizka.gsenger.api.dtos.messages.TextMessageData;
import com.pgizka.gsenger.provider.MediaMessage;
import com.pgizka.gsenger.provider.Message;
import com.pgizka.gsenger.provider.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import javax.inject.Inject;

import io.realm.Realm;
import okhttp3.ResponseBody;

import static com.pgizka.gsenger.TestUtils.createCall;
import static com.pgizka.gsenger.TestUtils.getApplication;
import static com.pgizka.gsenger.TestUtils.getOrCreateOwner;
import static com.pgizka.gsenger.TestUtils.getTestApplicationComponent;
import static com.pgizka.gsenger.TestUtils.prepareMessageData;
import static com.pgizka.gsenger.TestUtils.setupRealm;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class NewMediaMessageCommandTest {

    @Mock
    ResponseBody responseBody;

    @Inject
    MessageRestService messageRestService;

    @Inject
    Gson gson;

    private NewMediaMessageCommand mediaMessageCommand;
    private GSengerApplication gSengerApplication;

    @Before
    public void setUp() throws IOException {
        setupRealm();
        gSengerApplication = getApplication();
        TestApplicationComponent applicationComponent = getTestApplicationComponent();
        applicationComponent.inject(this);
        GSengerApplication.setApplicationComponent(applicationComponent);

        mediaMessageCommand = new NewMediaMessageCommand();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testReceivingMediaMessage() throws Exception {
        User owner = getOrCreateOwner();
        User sender = TestUtils.createUser();

        int messageServerId = 15;
        String data = gson.toJson(prepareMediaMessageData(sender, messageServerId));

        when(messageRestService.updateMessageState(Mockito.<MessagesStateChangedRequest>any())).thenReturn(createCall(responseBody));
        mediaMessageCommand.execute(gSengerApplication, TextMessageData.ACTION, data);

        verifyNewMediaMessageHandledCorrectly(messageServerId);
    }

    private MediaMessageData prepareMediaMessageData(User sender, int messageServerId) {
        MediaMessageData mediaMessageData = new MediaMessageData();
        mediaMessageData.setFileName("fileName");
        mediaMessageData.setType(MediaMessage.Type.PHOTO.code);
        mediaMessageData.setDescription("description");
        prepareMessageData(mediaMessageData, sender, messageServerId);
        return mediaMessageData;
    }

    private void verifyNewMediaMessageHandledCorrectly(int messageServerId) throws Exception {
        verify(messageRestService, timeout(2000)).updateMessageState(Mockito.<MessagesStateChangedRequest>any());
        verify(messageRestService, timeout(2000)).getMediaMessageData(messageServerId);

        Realm realm = Realm.getDefaultInstance();
        Message message = realm.where(Message.class)
                .equalTo("serverId", messageServerId)
                .findFirst();

        assertNotNull(message);
        MediaMessage mediaMessage = message.getMediaMessage();
        assertNotNull(mediaMessage);
        assertNotNull(mediaMessage.getFileName());
        assertNotNull(mediaMessage.getDescription());
    }

}
