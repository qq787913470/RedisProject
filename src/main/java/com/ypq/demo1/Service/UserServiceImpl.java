package com.ypq.demo1.Service;

import com.ypq.demo1.Model.User;
import com.ypq.demo1.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User getUserById(int id) {
        User u = new User();
        u.setId(id);
        return (User) userDao.findById(u).orElse(u);
    }

    @Override
    public List<User> getAllUser() {
        return userDao.findAll();
    }

    @Override
    public void delete(int id) {
        User u =new User();
        u.setId(id);
     userDao.delete(u);
    }

    @Override
    public void deleteUser(User u) {
        userDao.delete(u);
    }

    @Override
    public boolean update(User u) {
        return (boolean)userDao.save(u);
    }
}
