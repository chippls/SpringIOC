package Reflection.reflect;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ClassPathXMLApplicationContext {
    private File file;
    private HashMap<String,Object> map =  new HashMap<>();

    public ClassPathXMLApplicationContext(String config_file) {
        URL url = this.getClass().getClassLoader().getResource(config_file);
        try {
            file = new File(url.toURI());
            XMLParsing();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void XMLParsing() throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(file);
        Element root = document.getRootElement();
        List elementList = root.getChildren("bean");
        Iterator i = elementList.iterator();
        //读取bean节点的所有信息
        while (i.hasNext()){
            Element bean = (Element) i.next();
            String id = bean.getAttributeValue("id");
            //根据class创建实例
            String cls = bean.getAttributeValue("class");
            Object obj = Class.forName(cls).newInstance();
            Method[] method = obj.getClass().getDeclaredMethods();
            List<Element> list = bean.getChildren("property");
            for (Element el : list) {
                for (int n = 0; n < method.length; n++) {
                    String name = method[n].getName();
                    String temp = null;
                    //找到属性对应的Setter方法进行赋值
                    if (name.startsWith("set")){
                        temp = name.substring(3,name.length()).toLowerCase();
                        if (el.getAttribute("name")!= null){
                            if (temp.equals(el.getAttribute("name").getValue())){
                                method[n].invoke(obj,el.getAttribute("value").getValue());
                            }
                        }
                    }
                }
            }
            map.put(id,obj);
        }
    }

    public Object getBean(String name){
        return map.get(name);
    }
}
