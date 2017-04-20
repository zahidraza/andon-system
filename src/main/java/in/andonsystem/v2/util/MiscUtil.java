package in.andonsystem.v2.util;

import in.andonsystem.v1.util.Constants;
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
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by razamd on 3/30/2017.
 */
public class MiscUtil {
    private static final Logger logger = LoggerFactory.getLogger(MiscUtil.class);

    private static final char[] symbols;
    private static final Random random = new Random();
    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();
    }

    public static Date getTodayMidnight(){
        Long time = new Date().getTime();
        return new Date(time -((24 * 60 * 60 * 1000)+ time % (24 * 60 * 60 * 1000)));
    }

    public static Boolean sendSMS(String to,String message){
        logger.debug("sendSMS(): to = {}, message = {}", to, message);
        Boolean result = false;

        String apiKey = "Ac6e03dbeee67eae9e178f428f3371b3a";
        String sender = "AndSys";
        String urlString = "http://sms.variforrmsolution.com/apiv2/?";
        String body = "api=http&workingkey="+apiKey+"&type=JSON&to="+to+"&sender="+sender+"&message=" + message;

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
                int responsecode = jsonResponse.getInt("responsecode");
                if(responsecode == 200){
                    result = true;
                }
                if(responsecode == 400){
                    result = false;
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

        in.andonsystem.v1.util.MiscUtil miscUtil = in.andonsystem.v1.util.MiscUtil.getInstance();
        int startHour = Integer.parseInt(miscUtil.getConfigProperty(Constants.APP_V2_START_HOUR,"9"));
        int startMinutes = Integer.parseInt(miscUtil.getConfigProperty(Constants.APP_V2_START_MINUTE,"0"));
        int endHour = Integer.parseInt(miscUtil.getConfigProperty(Constants.APP_V2_END_HOUR,"18"));
        int endMinutes = Integer.parseInt(miscUtil.getConfigProperty(Constants.APP_V2_END_MINUTE,"0"));

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
}
