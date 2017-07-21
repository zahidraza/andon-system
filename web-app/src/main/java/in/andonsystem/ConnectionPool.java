package in.andonsystem;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import in.andonsystem.util.ConfigUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static in.andonsystem.Constants.*;

/**
 * Created by Md Zahid Raza on 3/9/2017.
 */
public class ConnectionPool {

    private static Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    private static ComboPooledDataSource cpds;

    private ConnectionPool(){}  //private contructor to avoid instatantiation

    static {

        ConfigUtility util = ConfigUtility.getInstance();

        int initialPool = Integer.parseInt(util.getConfigProperty(Constants.INITIAL_POOL_SIZE, "3"));
        int minPool = Integer.parseInt(util.getConfigProperty(MIN_POOL_SIZE, "3"));
        int maxPool = Integer.parseInt(util.getConfigProperty(MAX_POOL_SIZE, "15"));
        int maxIdleTime = Integer.parseInt(util.getConfigProperty(MAX_IDLE_TIME, "300")); // 5 minutes

        String url = util.getAppProperty(Constants.DATASOURCE_URL,null);
        String user = util.getAppProperty(Constants.DATASOURCE_USERNAME, null);
        String password = util.getAppProperty(Constants.DATASOURCE_PASSWORD, null);

        if(user == null || password == null || url == null|| user.trim().equals("") || url.trim().equals("")){
            throw new RuntimeException("MySql user or url not found");
        }

        try {
            Properties myProp = new Properties();
            myProp.put("user",user);
            myProp.put("password",password);

            cpds = new ComboPooledDataSource();
            cpds.setDriverClass(Constants.MYSQL_DRIVER); //loads the jdbc driver
            cpds.setJdbcUrl(url);

            // the settings below are optional -- c3p0 can work with defaults
//            cpds.setAcquireRetryAttempts(0); //attempt to acquire connection idefenitely
            cpds.setAcquireRetryDelay(30*1000); // 30 seconds
            cpds.setAutoCommitOnClose(true);   // Do not auto commit on connection close
            cpds.setInitialPoolSize(initialPool);
            cpds.setMinPoolSize(minPool);
            cpds.setMaxPoolSize(maxPool);
            cpds.setMaxIdleTime(maxIdleTime);
            cpds.setProperties(myProp);
            cpds.setUnreturnedConnectionTimeout(5*60); //5 minutes
            logger.info("c3p0 Connection pool initialised with following config:{initialPool = "+ initialPool+", minPool = "+minPool+ ", maxPool = "+maxPool+", maxIdleTime = "+maxIdleTime+" seconds}");
            System.out.println("c3p0 Connection pool initialised with following config:{initialPool = "+ initialPool+", minPool = "+minPool+ ", maxPool = "+maxPool+", maxIdleTime = "+maxIdleTime+" seconds}");
        }catch (Exception e){
            e.printStackTrace();
            logger.error("Error occured initialising c3p0 Connection pool.");
            System.out.println("Error occured initialising c3p0 Connection pool.");
        }
    }

    /**
     * This method returns a connection object from c3p0 Connection pool. It is required to close the connection after operation is complete.
     * Do not forget to commit at the end of operation, or else changes will be rolled back.
     * @return Connection object
     * @throws SQLException If unable to get connection from pool due to some reason
     */
    public static Connection getConnection() throws SQLException {
        return cpds.getConnection();
    }

}
