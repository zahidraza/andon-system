package in.andonsystem.v2.service;

import android.util.Log;

import java.util.List;

import in.andonsystem.App;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.BuyerDao;
import in.andonsystem.v2.entity.DaoSession;

/**
 * Created by razamd on 3/31/2017.
 */

public class BuyerService {

    private final String TAG = BuyerService.class.getSimpleName();

    private final BuyerDao buyerDao;

    public BuyerService(App app){
        DaoSession session= app.getDaoSession();
        buyerDao = app.getDaoSession().getBuyerDao();
    }

    public long save(Buyer buyer){
        return buyerDao.insert(buyer);
    }

    public void saveAll(List<Buyer> buyers){
        Log.i(TAG, "saveAll()");
        buyerDao.insertInTx(buyers);
    }

    public Buyer findOne(Long id){
        return buyerDao.load(id);
    }

    public List<Buyer> findByTeam(String team){
        Log.i(TAG, "findByTeam()");
        return buyerDao.queryBuilder()
                .where(BuyerDao.Properties.Team.eq(team))
                .orderAsc(BuyerDao.Properties.Name)
                .list();
    }

    public void deleteAll(){
        buyerDao.deleteAll();
    }

}
