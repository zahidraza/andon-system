package in.andonsystem;

/**
 * Created by razamd on 3/29/2017.
 */
public class Constants {

    /*/////////// Common/General Constants ///////////////*/
    public static final String APP_VERSION = "app.version";
    public static final String APP_LAST_SYNC = "app.last.sync";

    public static final String DATASOURCE_URL = "spring.datasource.url";
    public static final String DATASOURCE_USERNAME = "spring.datasource.username";
    public static final String DATASOURCE_PASSWORD = "spring.datasource.password";
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";

    public static final String INITIAL_POOL_SIZE = "c3p0.initialPoolSize";
    public static final String MIN_POOL_SIZE = "c3p0.minPoolSize";
    public static final String MAX_POOL_SIZE = "c3p0.maxPoolSize";
    public static final String MAX_IDLE_TIME = "c3p0.maxIdleTime.sec";

    public static final String OP_ACK = "OP_ACK";
    public static final String OP_FIX = "OP_FIX";
    public static final String OP_DEL = "OP_DEL";
    public static final String OP_SEEK_HELP = "OP_SEEK_HELP";


    /*/////////// App v1 Constants ///////////////*/

    public static final String APP_V1_START_HOUR = "app.v1.start.hour";
    public static final String APP_V1_START_MINUTE = "app.v1.start.minute";
    public static final String APP_V1_END_HOUR = "app.v1.end.hour";
    public static final String APP_V1_END_MINUTE = "app.v1.end.minute";

    public static final String SECTIONS = "app.v1.sections";
    public static final String DEPARTMENTS = "app.v1.departments";

    public static final String APP_V1_ACK_TIME = "app.v1.ack.time.min";
    public static final String APP_V1_FIX_L1_TIME = "app.v1.fix.level1.time.min";
    public static final String APP_V1_FIX_L2_TIME = "app.v1.fix.level2.time.min";

    /*/////////// App v2 Constants ///////////////*/

    public static final String APP_V2_START_HOUR = "app.v2.start.hour";
    public static final String APP_V2_START_MINUTE = "app.v2.start.minute";
    public static final String APP_V2_END_HOUR = "app.v2.end.hour";
    public static final String APP_V2_END_MINUTE = "app.v2.end.minute";

    public static final String PROBLEMS = "app.v2.problems";
    public static final String TEAMS = "app.v2.teams";

    public static final String APP_V2_ACK_TIME = "app.v2.ack.time.min";
    public static final String APP_V2_FIX_L1_TIME = "app.v2.fix.level1.time.min";
    public static final String APP_V2_FIX_L2_TIME = "app.v2.fix.level2.time.min";


}
