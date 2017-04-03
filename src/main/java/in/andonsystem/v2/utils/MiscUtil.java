package in.andonsystem.v2.utils;

import in.andonsystem.v1.models.Preferences;
import in.andonsystem.v1.util.Constants;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created by razamd on 3/30/2017.
 */
public class MiscUtil {
    private static final Logger logger = LoggerFactory.getLogger(MiscUtil.class);

    public static Date getTodayMidnight(){
        Long time = new Date().getTime();
        return new Date(time - time % (24 * 60 * 60 * 1000));
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
}
