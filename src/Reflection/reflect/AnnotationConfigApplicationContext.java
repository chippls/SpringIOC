package Reflection.reflect;

import Reflection.Annotation.ComponentScan;
import Reflection.Annotation.AutoWired;
import Reflection.Annotation.Component;
import Reflection.BootStrap;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * 实现扫描和解析注解等功能
 */
public class AnnotationConfigApplicationContext<T> {
    //使用HashMap存储Bean
    private HashMap<Class,Object> beanFactory = new HashMap<>();
    //获取bean的方法
    public T getBean(Class clazz){
        return (T) beanFactory.get(clazz);
    }
    String path;//编译后的字节码存储路径

    public void initContextByAnnotation(){
        //编译后的项目根目录
        path = AnnotationConfigApplicationContext.class.getClassLoader().getResource("").getFile();
        //查看启动类Bootstrap是否有定义扫描包
        ComponentScan annotation = BootStrap.class.getAnnotation(ComponentScan.class);
        if (annotation!=null){
            //有定义就扫描自定义的
            String[] definedPaths = annotation.value();
            if (definedPaths!=null&&definedPaths.length>0){
                loadClassInDefinedDir(path,definedPaths);
            }
        }else {
            //默认是扫描整个项目的目录
            System.out.println(path);
            findClassFile(new File(path));
        }
        assembleObject();
    }

    /*给@Autowired修饰的属性赋值*/
    private void assembleObject(){
        Set<Map.Entry<Class, Object>> entries = beanFactory.entrySet();
        //扫描所有容器中的Bean
        for (Map.Entry<Class, Object> entry : entries) {
            Object value = entry.getValue();
            //所有属性
            Field[] fields = value.getClass().getDeclaredFields();
            for (Field field : fields) {
                //如果被@AutoWired注解修饰则进行赋值
                AutoWired annotation = field.getAnnotation(AutoWired.class);

                if (annotation!= null){
                    try {
                        field.setAccessible(true);
                        field.set(value,beanFactory.get(field.getType()));
                    }catch (IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /*用户扫描自定义的包*/
    public void loadClassInDefinedDir(String path,String[] definedPaths){
        for (String definedPath : definedPaths) {
            //转换成绝对路径
            String s = definedPath.replaceAll("\\.", "/");
            String fullName = path+s;
            System.out.println(s);
            findClassFile(new File(fullName));
        }
    }

    /*扫描每一个文件夹找到所有class文件*/
    public void findClassFile(File pathParent){
        if (pathParent.isDirectory()){
            File[] childrenFiles = pathParent.listFiles();
            if (pathParent==null&&pathParent.length()==0){
                return;
            }
            for (File childrenFile : childrenFiles) {
                if (childrenFile.isDirectory()){    //childrenFile是文件夹
                    //递归调用直到找到所有文件
                    findClassFile(childrenFile);
                }else {                             //childrenFile是文件
                    //找到文件
                    loadClassWithAnnotation(childrenFile);
                }
            }
        }
    }

    /*装配找到的所有带有@Component注解的类到容器*/
    public void loadClassWithAnnotation(File file){
        //1.去掉前面的项目绝对路径
        String pathWithClass = file.getAbsolutePath().substring(path.length() - 1);
        //2.将路径的"/"转化为“.”和去掉后面的.class
        if (pathWithClass.contains(".class")){
            String fullName = pathWithClass.replaceAll("\\\\", ".").replaceAll(".class", "");
            /*根据获取的类的全限定名使用反射将实例添加到beanFactory中*/
            try {
                Class<?> clazz = Class.forName(fullName);
                //3.判断是不是接口，不是接口才创建实例
                if (!clazz.isInterface()){
                    //4.判断是不是注解
                    Component annotation = clazz.getAnnotation(Component.class);
                    if (annotation!=null){
                        //5.创建实例对象
                        Object instance = clazz.newInstance();
                        //6.判断是否有实现的接口
                        Class<?>[] interfaces = clazz.getInterfaces();
                        if (interfaces!=null&&interfaces.length>0){
                            //如果有接口就将其接口的class作为key,实例对象作为value
                            System.out.println("正在加载【"+interfaces[0].getName()+"】 实例对象："+instance.getClass().getName());
                            beanFactory.put(interfaces[0],instance);
                        }else{
                            System.out.println("正在加载【"+clazz.getName()+"】 实例对象："+instance.getClass().getName());
                            beanFactory.put(clazz,instance);
                        }
                        //如果没有接口就将自己的class作为key，实例对象作为value;
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
