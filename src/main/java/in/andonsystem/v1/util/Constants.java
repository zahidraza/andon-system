package in.andonsystem.v1.util;

/**
 * Created by razamd on 3/29/2017.
 */
public class Constants {

    public static final String DATASOURCE_URL = "spring.datasource.url";
    public static final String DATASOURCE_USERNAME = "spring.datasource.username";
    public static final String DATASOURCE_PASSWORD = "spring.datasource.password";
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    public static final String INITIAL_POOL_SIZE = "c3p0.initialPoolSize";
    public static final String MIN_POOL_SIZE = "c3p0.minPoolSize";
    public static final String MAX_POOL_SIZE = "c3p0.maxPoolSize";
    public static final String MAX_IDLE_TIME = "c3p0.maxIdleTime.sec";

    public static final String PROBLEMS = "problems";
    public static final String TEAMS = "teams";

    public static final String APP_VERSION = "app.version";
    public static final String APP_INITIALIZE = "app.initialize";

    //Issue update operations
    public static final String OP_ACK = "OP_ACK";
    public static final String OP_FIX = "OP_FIX";

}
