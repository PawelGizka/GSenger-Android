package com.pgizka.gsenger.provider;


import com.pgizka.gsenger.util.UserAccountManager;

import io.realm.Realm;

public class UserRepository {

    private Repository repository;

    public UserRepository(Repository repository) {
        this.repository = repository;
    }

    public User getOrCreateLocalUser(User userFromServer) {
        Realm realm = Realm.getDefaultInstance();

        User localUser = realm.where(User.class)
                .equalTo("serverId", userFromServer.getServerId())
                .findFirst();

        boolean localUserExists = localUser != null;
        if (!localUserExists) {
            localUser = userFromServer;
            localUser.setId(repository.getUserNextId());
            localUser.setInContacts(false);
            localUser = realm.copyToRealm(localUser);
        }

        return localUser;
    }

}