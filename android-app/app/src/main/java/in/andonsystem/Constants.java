package in.andonsystem;

/**
 * Created by razamd on 3/31/2017.
 */

public class Constants {
    public static final String API1_BASE_URL = "http://andonsystem.in/andon-system/api/v1";
    public static final String API2_BASE_URL = "http://andonsystem.in/andon-system/api/v2";
    public static final String AUTH_BASE_URL = "http://andonsystem.in/andon-system/oauth/token";

    /*Sync Preference file*/
    public static final String SYNC_PREF = "sync.pref";
    public static final String LAST_ISSUE1_SYNC = "last.issue1.sync";
    public static final String LAST_ISSUE2_SYNC = "last.issue2.sync";
    public static final String LAST_USER_SYNC = "last.user.sync";
    public static final Integer NO_OF_LINES = 8;

    /*User Preference file*/
    public static final String USER_PREF = "user.pref";
    public static final String USER_ID = "user.id";
    public static final String USER_EMAIL = "user.email";
    public static final String USER_NAME = "user.name";
    public static final String USER_ROLE = "user.role";
    public static final String USER_TYPE = "user.type";
    public static final String USER_LEVEL = "user.level";

    public static final String USER_ACCESS_TOKEN = "user.access.token";
    public static final String USER_REFRESH_TOKEN = "user.refresh.token";
    public static final String IS_LOGGED_IN = "is.logged.in";

    /*App Preference file*/
    public static final String APP_PREF = "app.pref";
    public static final String APP1_FIRST_LAUNCH = "app1.first.launch";
    public static final String APP2_FIRST_LAUNCH = "app2.first.launch";
    public static final String APP_PROBLEMS = "app.problems";
    public static final String APP_TEAMS = "app.teams";
    public static final String APP_SECTIONS = "app.section";
    public static final String APP_DEPARTMENTS = "app.departments";

    public static final int ACK_TIME = 5;  //30 minutes
    public static final int FIX_L1_TIME = 180; //3 hours
    public static final int FIX_L2_TIME = 120; //2 hours additional

    public static final String USER_FACTORY = "FACTORY";
    public static final String USER_SAMPLING = "SAMPLING";
    public static final String USER_MERCHANDISING = "MERCHANDISING";

    public static final String USER_LEVEL0 = "LEVEL0";
    public static final String USER_LEVEL1 = "LEVEL1";
    public static final String USER_LEVEL2 = "LEVEL2";
    public static final String USER_LEVEL3 = "LEVEL3";
    public static final String USER_LEVEL4 = "LEVEL4";



}
