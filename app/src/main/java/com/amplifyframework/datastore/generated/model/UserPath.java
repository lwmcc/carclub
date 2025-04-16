package com.amplifyframework.datastore.generated.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amplifyframework.core.model.ModelPath;
import com.amplifyframework.core.model.PropertyPath;

/** This is an auto generated class representing the ModelPath for the User type in your schema. */
public final class UserPath extends ModelPath<User> {
  private UserContactPath contacts;
  private UserGroupPath groups;
  UserPath(@NonNull String name, @NonNull Boolean isCollection, @Nullable PropertyPath parent) {
    super(name, isCollection, parent, User.class);
  }
  
  public synchronized UserContactPath getContacts() {
    if (contacts == null) {
      contacts = new UserContactPath("contacts", true, this);
    }
    return contacts;
  }
  
  public synchronized UserGroupPath getGroups() {
    if (groups == null) {
      groups = new UserGroupPath("groups", true, this);
    }
    return groups;
  }
}
