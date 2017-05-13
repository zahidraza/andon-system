package in.andonsystem.v2.service;

import android.util.Log;

import java.util.List;

import in.andonsystem.App;
import in.andonsystem.v2.entity.User;
import in.andonsystem.v2.entity.UserDao;

/**
 * Created by razamd on 3/31/2017.
 */

public class UserService {

    private final String TAG = UserService.class.getSimpleName();

    private final UserDao userDao;

    public UserService(App app){
        userDao = app.getDaoSession().getUserDao();
    }

    public User findOne(Long id){
        return userDao.load(id);
    }

    public User findByEmail(String email){
        return userDao.queryBuilder()
                .where(UserDao.Properties.Email.eq(email))
                .unique();
    }

    public List<User> findAll(){
        return userDao.loadAll();
    }

    public void saveOrUpdateBatch(List<User> users){
        userDao.insertOrReplaceInTx(users);
    }

    public void saveOrUpdate(User user){
        //Log.d(TAG,"saveOrUpdate: userId = " + user.getId());
        userDao.insertOrReplace(user);
    }

    public void deleteAll(){
        userDao.deleteAll();
    }

    public boolean exists(Long id){
        return (userDao.load(id) != null) ? true : false;
    }
}
