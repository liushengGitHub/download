package liusheng.main;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class Main2 {
    public static void main(String[] args) throws Exception {
       /* System.out.println(ClassLoader.getSystemClassLoader().getResource("").toURI().toURL().toString());
        System.out.println(new File("").getAbsolutePath());*/
        File file = new File("plugins/aopalliance-1.0.jar");

        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{file.toURI().toURL()});


        Class.forName("org.aopalliance.aop.Advice",true,classLoader);
    }
}
