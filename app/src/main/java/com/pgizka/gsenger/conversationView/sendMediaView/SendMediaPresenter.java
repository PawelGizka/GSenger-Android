package com.pgizka.gsenger.conversationView.sendMediaView;


import android.content.Context;
import android.net.Uri;

import com.path.android.jobqueue.JobManager;
import com.pgizka.gsenger.config.GSengerApplication;
import com.pgizka.gsenger.jobqueue.sendMessge.SendMessageJob;
import com.pgizka.gsenger.provider.Chat;
import com.pgizka.gsenger.provider.ChatRepository;
import com.pgizka.gsenger.provider.MediaMessage;
import com.pgizka.gsenger.provider.Message;
import com.pgizka.gsenger.provider.MessageRepository;
import com.pgizka.gsenger.provider.User;
import com.pgizka.gsenger.util.CopyFileTask;
import com.pgizka.gsenger.util.StorageResolver;

import javax.inject.Inject;

import io.realm.Realm;

public class SendMediaPresenter implements SendMediaContract.Presenter {

    @Inject
    MessageRepository messageRepository;

    @Inject
    ChatRepository chatRepository;

    @Inject
    JobManager jobManager;

    private int chatId;
    private int friendId;

    private User friend;
    private Chat chat;

    private boolean groupChat;

    private SendMediaContract.View view;
    private boolean viewIsPresent;

    private Context context;

    @Override
    public void onCreate(SendMediaContract.View view, Context context, int friendId, int chatId) {
        GSengerApplication.getApplicationComponent().inject(this);
        this.view = view;
        this.viewIsPresent = true;
        this.context = context;
        this.friendId = friendId;
        this.chatId = chatId;

        this.groupChat = friendId == -1;
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onResume() {
        Realm realm = Realm.getDefaultInstance();
        if (groupChat) {
            chat = realm.where(Chat.class)
                    .equalTo("id", chatId)
                    .findFirst();
            if (viewIsPresent) view.setToolbarSubtitle(chat.getChatName());
        } else {
            friend = realm.where(User.class)
                    .equalTo("id", friendId)
                    .findFirst();
            if (viewIsPresent) view.setToolbarSubtitle(friend.getUserName());
        }
    }

    @Override
    public void sendPhoto(Uri photoUri, String description) {
        view.showProgressDialog("Copying File...");
        new CopyFileTask(context)
                .from(photoUri)
                .to(StorageResolver.IMAGES_SENT_PATH)
                .onCopyingFinished((newFileName, path) -> {
                    if (viewIsPresent) view.dismissProgressDialog();
                    sendMediaMessage(MediaMessage.Type.PHOTO.code, newFileName, path, description);
                    if (viewIsPresent) view.finish();
                })
                .execute();
    }

    @Override
    public void sendVideo(Uri videoUri, String description) {
        view.showProgressDialog("Copying File...");
        new CopyFileTask(context)
                .from(videoUri)
                .to(StorageResolver.VIDEO_SENT_PATH)
                .onCopyingFinished((newFileName, path) -> {
                    if (viewIsPresent) view.dismissProgressDialog();
                    sendMediaMessage(MediaMessage.Type.VIDEO.code, newFileName, path, description);
                    if (viewIsPresent) view.finish();
                })
                .execute();
    }

    @Override
    public void sendFile(Uri fileUri, String description) {
        view.showProgressDialog("Copying File...");
        new CopyFileTask(context)
                .from(fileUri)
                .to(StorageResolver.FILE_SENT_PATH)
                .onCopyingFinished((newFileName, path) -> {
                    if (viewIsPresent) view.dismissProgressDialog();
                    sendMediaMessage(MediaMessage.Type.FILE.code, newFileName, path, description);
                    if (viewIsPresent) view.finish();
                })
                .execute();
    }

    public void sendMediaMessage(int type, String fileName, String path, String description) {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();
        Message message = messageRepository.createOutgoingMediaMessageWithReceivers(chat, type, fileName, path, description);
        realm.commitTransaction();

        jobManager.addJob(new SendMessageJob(message.getId()));
    }

}
