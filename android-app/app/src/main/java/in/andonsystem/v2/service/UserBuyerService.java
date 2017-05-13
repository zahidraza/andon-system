package in.andonsystem.v2.service;

import java.util.List;

import in.andonsystem.App;
import in.andonsystem.v2.entity.UserBuyer;
import in.andonsystem.v2.entity.UserBuyerDao;

/**
 * Created by razamd on 4/8/2017.
 */

public class UserBuyerService {

    private UserBuyerDao userBuyerDao;

    public UserBuyerService(App app){
        this.userBuyerDao = app.getDaoSession().getUserBuyerDao();
    }

    public long save(UserBuyer userBuyer){
        return userBuyerDao.insert(userBuyer);
    }

    public void saveBatch(List<UserBuyer> list){
        userBuyerDao.insertInTx(list);
    }

    public void deleteByUser(Long userId){
        List<UserBuyer> list = userBuyerDao.queryBuilder().where(UserBuyerDao.Properties.UserId.eq(userId)).list();
        userBuyerDao.deleteInTx(list);
    }

    public void deleteAll(){
        userBuyerDao.deleteAll();
    }
}
