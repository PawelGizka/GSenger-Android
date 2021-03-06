package com.pgizka.gsenger;

import com.pgizka.gsenger.converstation.ConversationPresenterTest;
import com.pgizka.gsenger.converstation.SendMediaPresenterTest;
import com.pgizka.gsenger.gcm.NewGroupChatCommandTest;
import com.pgizka.gsenger.gcm.NewMessageCommandTest;
import com.pgizka.gsenger.provider.ChatRepositoryTest;
import com.pgizka.gsenger.provider.MessageRepositoryTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

// Runs all unit tests.
@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConversationPresenterTest.class,
        SendMediaPresenterTest.class,
        NewMessageCommandTest.class,
        NewGroupChatCommandTest.class,
        MessageRepositoryTest.class,
        ChatRepositoryTest.class})
public class FullTestSuite {}
