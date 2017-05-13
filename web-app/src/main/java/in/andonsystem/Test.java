package in.andonsystem;

import in.andonsystem.v2.util.MiscUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by razamd on 4/20/2017.
 */
public class Test {
    public static void main(String[] args){
//        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
//        try {
//            Date today = df1.parse("20/04/2017");
//            System.out.println(today.toString());
//            System.out.println(new Date(today.getTime()+ (1000*60*60*24)));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        System.out.println(MiscUtil.getOtp(6));
        System.out.println(MiscUtil.getOtp(6));
        System.out.println(MiscUtil.getOtp(6));
        System.out.println(MiscUtil.getOtp(6));
    }
}

