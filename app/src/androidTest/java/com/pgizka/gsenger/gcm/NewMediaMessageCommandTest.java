package com.pgizka.gsenger.gcm;

import com.google.gson.Gson;
import com.pgizka.gsenger.api.BaseResponse;
import com.pgizka.gsenger.api.MessageRestService;
import com.pgizka.gsenger.dagger.TestApplicationComponent;
import com.pgizka.gsenger.dagger2.GSengerApplication;
import com.pgizka.gsenger.gcm.commands.NewMediaMessageCommand;
import com.pgizka.gsenger.gcm.data.NewMediaMessageData;
import com.pgizka.gsenger.gcm.data.NewMessageData;
import com.pgizka.gsenger.gcm.data.NewTextMessageData;
import com.pgizka.gsenger.jobqueue.setMessageState.MessageStateChangedRequest;
import com.pgizka.gsenger.provider.MediaMessage;
import com.pgizka.gsenger.provider.Message;
import com.pgizka.gsenger.provider.TextMessage;
import com.pgizka.gsenger.provider.User;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import javax.inject.Inject;

import io.realm.Realm;

import static com.pgizka.gsenger.TestUtils.createCall;
import static com.pgizka.gsenger.TestUtils.createUser;
import static com.pgizka.gsenger.TestUtils.getApplication;
import static com.pgizka.gsenger.TestUtils.getOrCreateOwner;
import static com.pgizka.gsenger.TestUtils.getTestApplicationComponent;
import static com.pgizka.gsenger.TestUtils.prepareMessageData;
import static com.pgizka.gsenger.TestUtils.setupRealm;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NewMediaMessageCommandTest {

    @Inject
    MessageRestService messageRestService;

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
    }

    @Test
    public void testReceivingMediaMessage() throws Exception {
        User owner = getOrCreateOwner();
        User sender = createUser();

        int messageServerId = 15;
        String data = new Gson().getAdapter(NewMediaMessageData.class).toJson(prepareMediaMessageData(sender, messageServerId));

        when(messageRestService.setMessageDelivered(Mockito.<MessageStateChangedRequest>any())).thenReturn(createCall(new BaseResponse()));
        mediaMessageCommand.execute(gSengerApplication, NewTextMessageData.ACTION, data);

        verifyNewMediaMessageHandledCorrectly(messageServerId);
    }

    private NewMediaMessageData prepareMediaMessageData(User sender, int messageServerId) {
        NewMediaMessageData mediaMessageData = new NewMediaMessageData();
        mediaMessageData.setFileName("fileName");
        mediaMessageData.setType(MediaMessage.Type.PHOTO.code);
        mediaMessageData.setDescription("description");
        prepareMessageData(mediaMessageData, sender, messageServerId);
        return mediaMessageData;
    }

    private void verifyNewMediaMessageHandledCorrectly(int messageServerId) throws Exception {
        verify(messageRestService, timeout(2000)).setMessageDelivered(Mockito.<MessageStateChangedRequest>any());

        Realm realm = Realm.getDefaultInstance();
        realm.refresh();
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
