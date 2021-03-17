package Reflection;

import Reflection.Annotation.ComponentScan;
import Reflection.pojo.User;
import Reflection.reflect.AnnotationConfigApplicationContext;
import Reflection.reflect.ClassPathXMLApplicationContext;
import Reflection.service.UserService;
import Reflection.service.impl.UserServiceImpl;

@ComponentScan(value = {"Reflection.dao", "Reflection.service"})
public class BootStrap {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.initContextByAnnotation();
        UserServiceImpl userService = (UserServiceImpl) applicationContext.getBean(UserService.class);
        ClassPathXMLApplicationContext xmlApplicationContext = new ClassPathXMLApplicationContext("Reflection/beans.xml");
        User user = (User) xmlApplicationContext.getBean("user");
        System.out.println(user);
        userService.login(user);
        User user2 = (User) xmlApplicationContext.getBean("user2");
        System.out.println(user2);
        userService.login(user2);
    }
}
