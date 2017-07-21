package in.andonsystem.service;

import android.util.Log;

import java.util.List;

import in.andonsystem.App;
import in.andonsystem.entity.User;
import in.andonsystem.entity.UserDao;

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

    public List<User> findAllCity(String userType){
        Log.d(TAG,"findAllCity");
        return userDao.queryBuilder().where(UserDao.Properties.UserType.notEq(userType)).list();
    }

    public List<User> findAllFactory(String userType){
        Log.d(TAG,"findAllFactory");
        return userDao.queryBuilder().where(UserDao.Properties.UserType.eq(userType)).list();
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
