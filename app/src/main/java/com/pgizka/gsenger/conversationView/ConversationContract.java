package com.pgizka.gsenger.conversationView;

import com.pgizka.gsenger.provider.Chat;
import com.pgizka.gsenger.provider.Message;
import com.pgizka.gsenger.provider.User;

import java.util.List;

public interface ConversationContract {

    interface View {

        void displayConversationItems(List<Message> conversationItems);

        void displayChatImage(User user);

        void displayChatName(String userName);

        void setChat(Chat chat);

        void setPresenter(ConversationContract.Presenter presenter);

        void navigateToMediaDetailView(int messageId);

    }

    interface Presenter {

        void onCreate(View view, int friendId, int chatId);

        void sendMessage(String text);

        void messageClicked(Message message);

        void onResume();

        void onPause();

    }

}
