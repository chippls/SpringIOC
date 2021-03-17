package Reflection.service.impl;

import Reflection.Annotation.AutoWired;
import Reflection.Annotation.Component;
import Reflection.dao.UserDao;
import Reflection.pojo.User;
import Reflection.service.UserService;

@Component
public class UserServiceImpl implements UserService {
    @AutoWired
    private UserDao userDao;

    @Override
    public void login(User user) {
        System.out.println("调用UserServiceImpl的login方法");
        userDao.loginByUsername(user);
    }
}
