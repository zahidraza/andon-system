/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.andonsystem.v1.services;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

/**
 *
 * @author Md Zahid Raza
 */
public class SMSService {
    /*
    public static Boolean sendSMS(String to,String message){return true;}
    */
    public static Boolean sendSMS(String to,String message){
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
            
            System.out.println(response);
            
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
