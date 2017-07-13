package in.andonsystem.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by razamd on 3/31/2017.
 */

public class MiscUtil {

    public static Boolean isConnectedToInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        Boolean isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        return isConnected;
    }

    public static Set<Integer> getLines(String line){
        Set<Integer> result = new HashSet<>();
        String[] lines = line.substring(1,line.length()-1).split(",");
        for (String l: lines) {
            if (l.trim().length() > 0){
                result.add(Integer.parseInt(l));
            }
        }
        return result;
    }

    public static long getDowntime(Date fixAt, Date raisedAt) {
        long downtime = -1L;
        if (fixAt != null) {
            long fDays = TimeUnit.MILLISECONDS.toDays(fixAt.getTime());
            long rDays = TimeUnit.MILLISECONDS.toDays(raisedAt.getTime());
            downtime = (fixAt.getTime() - raisedAt.getTime() - (fDays-rDays)*(1000*60*(60*14 + 30))); //no of days multiplied with 14 hours 30 minute
        }
        return downtime;
    }

    public static long getDowntime(Long fixAt, Long raisedAt) {
        long downtime = -1L;
        if (fixAt != null) {
            long fDays = TimeUnit.MILLISECONDS.toDays(fixAt);
            long rDays = TimeUnit.MILLISECONDS.toDays(raisedAt);
            downtime = (fixAt - raisedAt - (fDays-rDays)*(1000*60*(60*14 + 30))); //no of days multiplied with 14 hours 30 minute
        }
        return downtime;
    }
}
