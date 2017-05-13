package in.andonsystem.v2.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
