package in.andonsystem.util;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashSet;
import java.util.Set;

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
}
