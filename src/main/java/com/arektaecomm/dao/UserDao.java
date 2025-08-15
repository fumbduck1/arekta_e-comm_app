package com.arektaecomm.dao;

import com.arektaecomm.model.User;

public interface UserDao {
    User fetchById(String userId);
    void updateUser(User u);
}