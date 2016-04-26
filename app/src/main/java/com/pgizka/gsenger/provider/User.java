package com.pgizka.gsenger.provider;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class User extends RealmObject {

    @PrimaryKey
    private int id;

    private int serverId;
    private String userName;
    private long addedDate;
    private long lastLoggedDate;
    private String status;
    private String photoPath;
    private String photoHash;

    private String phoneNumber;
    private boolean inContacts;

    private RealmList<TextMessage> sentTextMessages;
    private RealmList<Receiver> receivers;
    private RealmList<Chat> chats;

    public User() {
    }

    /**
     * Gson does not support serializing realm objects, so to walk around this issue
     * we copy User entity to new User entity NOT covered by UserRealmProxy Object.
     * Maybe Gson Type Adapter would be better idea, but for now this solution is enough.
     *
     * @implNote This solution does not work with Lists inside object
     *
     * @param user to copy
     */
    public User(User user) {
        this.id = user.getId();
        this.serverId = user.getServerId();
        this.userName = user.getUserName();
        this.addedDate = user.getAddedDate();
        this.lastLoggedDate = user.getLastLoggedDate();
        this.status = user.getStatus();
        this.photoPath = user.getPhotoPath();
        this.photoHash = user.getPhotoHash();
        this.phoneNumber = user.getPhoneNumber();
        this.inContacts = user.isInContacts();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(long addedDate) {
        this.addedDate = addedDate;
    }

    public long getLastLoggedDate() {
        return lastLoggedDate;
    }

    public void setLastLoggedDate(long lastLoggedDate) {
        this.lastLoggedDate = lastLoggedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getPhotoHash() {
        return photoHash;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isInContacts() {
        return inContacts;
    }

    public void setInContacts(boolean inContacts) {
        this.inContacts = inContacts;
    }

    public void setPhotoHash(String photoHash) {
        this.photoHash = photoHash;
    }

    public RealmList<TextMessage> getSentTextMessages() {
        return sentTextMessages;
    }

    public void setSentTextMessages(RealmList<TextMessage> sentTextMessages) {
        this.sentTextMessages = sentTextMessages;
    }

    public RealmList<Receiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(RealmList<Receiver> receivers) {
        this.receivers = receivers;
    }

    public RealmList<Chat> getChats() {
        return chats;
    }

    public void setChats(RealmList<Chat> chats) {
        this.chats = chats;
    }
}