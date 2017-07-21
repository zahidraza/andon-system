package in.andonsystem.util;

import in.andonsystem.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mdzahidraza on 17/06/17.
 */
public class DbBackupUtility {

    private static final Logger logger = LoggerFactory.getLogger(DbBackupUtility.class);
    private static String dbName = "andonsys";
    private static String user = ConfigUtility.getInstance().getAppProperty(Constants.DATASOURCE_USERNAME, "root");
    private static String password = ConfigUtility.getInstance().getAppProperty(Constants.DATASOURCE_PASSWORD, "zahid");
    private static String path = MiscUtil.getAndonHome() + File.separator + "backup";

    public static void backup() {
        logger.info("Backing up database...");
        DateFormat sdf = new SimpleDateFormat("ddMMYYYY");
        String date = sdf.format(new Date());
        String filename = path + File.separator + dbName + "-" + date + ".sql";

        Process p = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec("mysqldump -u" + user + " -p" + password + " --add-drop-database -B " + dbName + " -r " + filename);

            int processComplete = p.waitFor();
            if (processComplete == 0) {
                logger.info("Backup created successfully...");
            } else {
                logger.info("Could not create the backup");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void restore(String filename) {

        /*
            mysql --user=root --password=zahid andonsys < /Users/mdzahidraza/Documents/andonsys-20062017.sql
        */

        logger.info("Restoring database with {} file ...", filename);
        filename = path + File.separator + filename;
        String[] restoreCmd = new String[]{"mysql ", "--user=" + user, "--password=" + password, "-e", "source " + filename};
        String cmd = "mysql --user=" + user+ " --password=" + password +" -e"+ " source " + filename;
        Process runtimeProcess;
        try {

            runtimeProcess = Runtime.getRuntime().exec(cmd);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                logger.info("Restored Successfully");
            } else {
                logger.info("Could not restore backup");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
