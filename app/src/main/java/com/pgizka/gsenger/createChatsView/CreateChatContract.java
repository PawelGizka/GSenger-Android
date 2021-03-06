package com.pgizka.gsenger.createChatsView;


import com.pgizka.gsenger.provider.User;

import java.util.List;

public interface CreateChatContract {

    interface View {

        void displayUsersList(List<User> users);

        void displayErrorMessage(String errorMessage);

        void showProgressDialog();

        void dismissProgressDialog();

        void closeWindow();

    }

    interface Presenter {

        void onCreate(View view);

        void onDestroy();

        void onResume();

        void createChat(String chatName, List<User> participants);

    }

}
