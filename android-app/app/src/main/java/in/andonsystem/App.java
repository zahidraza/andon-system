package in.andonsystem;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import in.andonsystem.entity.DaoMaster;
import in.andonsystem.entity.DaoSession;

/**
 * Created by razamd on 3/31/2017.
 */

public class App extends Application {
    private static final String TAG = App.class.getSimpleName();

    public static final boolean ENCRYPTED = true;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "andonsys-db-encrypted" : "andonsys-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession(){
        return daoSession;
    }
}
