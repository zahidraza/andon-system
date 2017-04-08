package in.andonsystem.v1.util;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Created by razamd on 1/31/2017.
 */
public class MiscUtil {

    private static MiscUtil INSTANCE;

    private final String appFile = "application.properties";
    private final String configFile = "config.properties";
    private Properties appProps = new Properties();

    private MiscUtil() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(appFile);
            appProps.load(is);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static MiscUtil getInstance(){
        if(INSTANCE == null){
            INSTANCE = new MiscUtil();
        }
        return INSTANCE;
    }

    public String getAppProperty(String key) {
        return appProps.getProperty(key);
    }

    public String getAppProperty(String key, String defaultValue) {
        return appProps.getProperty(key, defaultValue);
    }

    public String getConfigProperty(String key){
        return getConfigProperty(key, null);
    }

    public String getConfigProperty(String key, String defaultValue){
        Properties props = new Properties();
        InputStream is = getClass().getClassLoader().getResourceAsStream(configFile);
        try {
            props.load(is);
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return props.getProperty(key, defaultValue);
    }

    public void setConfigProperty(String key, String value){
        URL url = getClass().getClassLoader().getResource(configFile);
        String path = null;
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            path = url.toURI().getPath();
            input = new FileInputStream(path);
            prop.load(input);
            input.close();
            output = new FileOutputStream(path);
            prop.setProperty(key, value);
            prop.store(output, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
