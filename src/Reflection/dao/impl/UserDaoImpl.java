package Reflection.dao.impl;

import Reflection.Annotation.Component;
import Reflection.dao.UserDao;
import Reflection.pojo.User;

@Component
public class UserDaoImpl implements UserDao {
    @Override
    public void loginByUsername(User user) {
        System.out.println("验证用户【"+user.getUsername()+"】登录");
    }
}
