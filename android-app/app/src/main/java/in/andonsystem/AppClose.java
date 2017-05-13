package in.andonsystem;


import android.app.Activity;

/**
 * Created by razamd on 4/2/2017.
 */

public class AppClose {
    public static Activity activity1;   //LoadingActivity
    public static Activity activity2;   //AuthenticatorActivity
    public static Activity activity3;   //HomeActivity
    public static Activity activity4;   //Child Activity

    public static void close() {
        if( AppClose.activity4 != null){
            AppClose.activity4.finish();
        }
        if (AppClose.activity3 != null) {
            AppClose.activity3.finish();
        }
        if (AppClose.activity2 != null) {
            AppClose.activity2.finish();
        }
        if (AppClose.activity1 != null) {
            AppClose.activity1.finish();
        }

    }
}
