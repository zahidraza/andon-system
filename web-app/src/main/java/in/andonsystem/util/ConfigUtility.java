package in.andonsystem.util;


import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * Created by razamd on 1/31/2017.
 */
public class ConfigUtility {

    private static ConfigUtility INSTANCE;

    private final String appFile = "application.properties";
    private final String configFile = MiscUtil.getAndonHome() + File.separator + "conf" + File.separator + "config.properties";
    private Properties appProps = new Properties();

    private ConfigUtility() {
        InputStream is = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream(appFile);
            appProps.load(is);
            is.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (is == null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static ConfigUtility getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ConfigUtility();
        }
        return INSTANCE;
    }

    public String getAppProperty(String key) {
        return appProps.getProperty(key);
    }

    public String getAppProperty(String key, String defaultValue) {
        return appProps.getProperty(key, defaultValue);
    }

    public Properties getConfigProps() {
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(new File(configFile));
            props.load(is);
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (is == null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }

    public String getConfigProperty(String key){
        return getConfigProperty(key, null);
    }

    public String getConfigProperty(String key, String defaultValue){
        Properties props = new Properties();
        InputStream is = null;
        try {
            is = new FileInputStream(new File(configFile));
            props.load(is);
            is.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (is == null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props.getProperty(key, defaultValue);
    }

    public void setConfigProperty(String key, String value){
        Properties prop = new Properties();
        OutputStream output = null;
        InputStream input = null;
        try {
            input = new FileInputStream(configFile);
            prop.load(input);
            input.close();
            output = new FileOutputStream(configFile);
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
