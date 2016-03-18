package com.pgizka.gsenger.mainView.chats;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.pgizka.gsenger.provider.GSengerContract;

public class ChatsPresenterImpl extends Fragment implements ChatsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {


    private ChatsContract.View<ChatsToDisplayModel> chatsView;

    private AppCompatActivity activity;
    private ChatsToDisplayModel chatsToDisplayModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatsView = (ChatsContract.View<ChatsToDisplayModel>) getTargetFragment();
        activity = chatsView.getHoldingActivity();
        chatsToDisplayModel = new ChatsToDisplayModel();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public void chatClicked(int chatId, int position, ChatToDisplay chatToDisplay) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = GSengerContract.Chats.buildChatsToDisplayUri();
        CursorLoader cursorLoader = new CursorLoader(activity, uri, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        chatsToDisplayModel.readDataFromCursor(data);
        chatsView.displayChatsList(chatsToDisplayModel);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
