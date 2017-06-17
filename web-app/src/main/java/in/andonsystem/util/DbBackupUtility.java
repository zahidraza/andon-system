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

    public static void backup() {
        logger.info("Backing up database...");
        String user = ConfigUtility.getInstance().getAppProperty(Constants.DATASOURCE_USERNAME, "root");
        String password = ConfigUtility.getInstance().getAppProperty(Constants.DATASOURCE_PASSWORD, "zahid");
        String dbName = "andonsys";
        DateFormat sdf = new SimpleDateFormat("ddMMYYYY");
        String date = sdf.format(new Date());
        String filename = MiscUtil.getAndonHome() + File.separator + "backup" + File.separator + dbName + "-" + date + ".sql";

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
}
