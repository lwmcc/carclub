package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the Channel type in your schema. */
public final class ChannelPath extends ModelPath<Channel> {
  private UserPath user;
  private MessagePath messages;
  ChannelPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, Channel.class);
  }
  
  public synchronized UserPath getUser() {
    if (user == null) {
      user = new UserPath("user", false, this);
    }
    return user;
  }
  
  public synchronized MessagePath getMessages() {
    if (messages == null) {
      messages = new MessagePath("messages", true, this);
    }
    return messages;
  }
}
