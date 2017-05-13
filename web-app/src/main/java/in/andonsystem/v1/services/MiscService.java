/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Properties;
import javax.servlet.ServletContext;


/**
 * @author Administrator
 */
public class MiscService {
    public MiscService() {
    }

    public static long getTime(Timestamp time) {
        if (time != null) {
            return time.getTime();
        } else {
            return 0L;
        }
    }

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

//    //Read and write to Properties file
//    public String readProperties(ServletContext context, String key) {
//        String path = context.getRealPath("/WEB-INF/andon_config.properties");
//        String result = null;
//        Properties prop = new Properties();
//        InputStream input = null;
//        try {
//            input = new FileInputStream(path);
//            // load a properties file
//            prop.load(input);
//            result = prop.getProperty(key, null);
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return result;
//    }
//
//    public void writeProperties(ServletContext context, String key, String value) {
//        String path = context.getRealPath("/WEB-INF/andon_config.properties");
//        Properties prop = new Properties();
//        OutputStream output = null;
//        InputStream input = null;
//        try {
//            input = new FileInputStream(path);
//            prop.load(input);
//            input.close();
//            output = new FileOutputStream(path);
//            prop.setProperty(key, value);
//            prop.store(output, null);
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        } finally {
//            if (output != null) {
//                try {
//                    output.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

}
