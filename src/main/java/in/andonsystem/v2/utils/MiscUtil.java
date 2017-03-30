package in.andonsystem.v2.utils;

import in.andonsystem.v1.models.Preferences;
import in.andonsystem.v1.util.Constants;

import java.util.Date;

/**
 * Created by razamd on 3/30/2017.
 */
public class MiscUtil {

    public static Date getTodayMidnight(){
        Long time = new Date().getTime();
        return new Date(time - time % (24 * 60 * 60 * 1000));
    }


}
