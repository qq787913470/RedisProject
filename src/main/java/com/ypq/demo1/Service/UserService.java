package com.ypq.demo1.Service;

import com.ypq.demo1.Model.User;

import java.util.List;


public interface UserService {
    User getUserById(int id);
    List<User> getAllUser();
    void delete(int id);
    void deleteUser(User u);
    boolean update(User u);
}
