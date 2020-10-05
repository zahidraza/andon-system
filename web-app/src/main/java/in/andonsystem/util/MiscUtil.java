package in.andonsystem.util;

import in.andonsystem.Constants;
import in.andonsystem.v1.entity.Designation;
import in.andonsystem.v1.entity.Problem;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.User;
import in.andonsystem.Level;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by razamd on 3/30/2017.
 */
public class MiscUtil {
    private static final Logger logger = LoggerFactory.getLogger(MiscUtil.class);

    private static final char[] symbols;
    private static final Random random = new Random();
    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '1'; ch <= '9'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();
    }

    public static String getAndonHome() {
        return System.getenv("ANDON_HOME");
    }

    public static Date getTodayMidnight(){
        Long time = new Date().getTime();
        Long oneDayMillis = (long)24*60*60*1000;
        return new Date(time -((time + (5*60 + 30)*60*1000)%oneDayMillis));
    }

    /**
     * Find number of minutes passed since today midnight
     * @return
     */
    public static int getMinutesSinceMidnight(Date date){
        long timeNow = date.getTime();
        long days = TimeUnit.MILLISECONDS.toDays(timeNow);
        long millisToday = (timeNow - TimeUnit.DAYS.toMillis(days));  //GMT:This number of milliseconds after 12:00 today
        int minutesToday = (int )TimeUnit.MILLISECONDS.toMinutes(millisToday);  //GMT
        minutesToday += (60*5) + 30;  //GMT+05:30 minutesToday
        minutesToday = minutesToday % (24*60);
        return minutesToday;
    }

    public static Date getYesterdayMidnight() {
        Long time = new Date().getTime();
        Long oneDayMillis = (long)24*60*60*1000;
        return new Date(time -((time + (5*60 + 30)*60*1000)%oneDayMillis + oneDayMillis));
    }

//    public static Boolean sendSMS(String to,String message){return true;}

    public static Boolean sendSMS(String to,String message){

        logger.debug("sendSMS(): to = {}, message = {}", to, message);
        boolean result = false;
        String apiKey = "7f60ad5ac8d06c099885ae3a1c763edc605d0fb5";
        String sender = "LAGUNA";
        String urlString = "http://alerts.variforrm.in/api?";
        String body = "api_key="+apiKey+"&method=sms.normal&to="+to+"&sender="+sender+"&message=" + message+"&flash=0&unicode=0";

        try{
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
            writer.writeBytes(body);
            writer.flush();
            writer.close();

            conn.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
            br.close();

            String response = buffer.toString();
            logger.debug("sms send response: {}",response);

            JSONObject jsonResponse = new JSONObject(response);

            try{
                int status = jsonResponse.getInt("status");
                if (status == 200){
                    result = true;
                }
            }catch(Exception e){
                e.printStackTrace();
            }


        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Check If City Office is Open/Close.
     * @return -1:  Not opened yet, 0: open, 1: Colsed
     */
    public static int checkApp2Closed(){
        long timeNow = System.currentTimeMillis();

        DateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        String time = df.format(new Date(timeNow));
        int hours = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);
        int totalMinutes = (60 * hours) + minutes;

        ConfigUtility configUtility = ConfigUtility.getInstance();
        int startHour = Integer.parseInt(configUtility.getConfigProperty(Constants.APP_V2_START_HOUR,"9"));
        int startMinutes = Integer.parseInt(configUtility.getConfigProperty(Constants.APP_V2_START_MINUTE,"0"));
        int endHour = Integer.parseInt(configUtility.getConfigProperty(Constants.APP_V2_END_HOUR,"18"));
        int endMinutes = Integer.parseInt(configUtility.getConfigProperty(Constants.APP_V2_END_MINUTE,"0"));

        int startTime = (60 * startHour) + startMinutes;
        int endTime = (60 * endHour) + endMinutes;

        if (totalMinutes < startTime) {
            return -1;
        }
        if (totalMinutes > endTime) {
            return 1;
        }
        return 0;
    }

    /**
     * Check If Factory is Open/Close.
     * @return -1:  Not opened yet, 0: open, 1: Colsed
     */
    public static int checkApp1Closed(){
        long timeNow = System.currentTimeMillis();

        DateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        String time = df.format(new Date(timeNow));
        int hours = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);
        int totalMinutes = (60 * hours) + minutes;

        ConfigUtility configUtility = ConfigUtility.getInstance();
        int startHour = Integer.parseInt(configUtility.getConfigProperty(Constants.APP_V1_START_HOUR,"8"));
        int startMinutes = Integer.parseInt(configUtility.getConfigProperty(Constants.APP_V1_START_MINUTE,"45"));
        int endHour = Integer.parseInt(configUtility.getConfigProperty(Constants.APP_V1_END_HOUR,"18"));
        int endMinutes = Integer.parseInt(configUtility.getConfigProperty(Constants.APP_V1_END_MINUTE,"15"));

        int startTime = (60 * startHour) + startMinutes;
        int endTime = (60 * endHour) + endMinutes;

        if (totalMinutes < startTime) {
            return -1;
        }
        if (totalMinutes > endTime) {
            return 1;
        }
        return 0;
    }

    public static String getOtp(int length) {
        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

    /**
     * Get mobile number of users
     * @param problem
     * @param level
     * @return comma separated mobile number of users if users are found else null
     */
    public static String getUserMobileNumbers(Problem problem, Level level){
        logger.debug("getUserMobileNumbers-1: level = {}", level.getValue());
        int l = (level == Level.LEVEL1 ? 1 : (level == Level.LEVEL2 ? 2 : (level == Level.LEVEL3 ? 3 : -1)));
        List<Designation> designations = problem.getDesignations().stream()
                .filter(designation -> designation.getLevel() == l)
                .collect(Collectors.toList());

        List<User> users = designations.stream()
                .flatMap(designation -> designation.getUsers().stream())
                .filter(user -> user.getActive() && user.getLevel().equalsIgnoreCase(level.getValue()))
                .collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        if (users.size() > 0) {
            users.forEach(user -> builder.append(user.getMobile() + ","));
            builder.setLength(builder.length() - 1);
            return builder.toString();
        }
        return null;
    }

    /**
     * Get mobile number of users
     * @param buyer
     * @param level
     * @return comma separated mobile number of users if users are found else null
     */
    public static String getUserMobileNumbers(Buyer buyer, Level level){
        logger.debug("getUserMobileNumbers-2: level = {}", level.getValue());
        List<User> users = buyer.getUsers().stream()
                .filter(user -> user.getActive() && user.getLevel().equalsIgnoreCase(level.getValue()))
                .collect(Collectors.toList());

        StringBuilder builder = new StringBuilder();
        if (users.size() > 0) {
            users.forEach(user -> builder.append(user.getMobile() + ","));
            builder.setLength(builder.length() - 1);
            return builder.toString();
        }
        return null;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDesc(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
