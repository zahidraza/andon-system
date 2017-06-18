package in.andonsystem.service;

import java.util.List;

import in.andonsystem.App;
import in.andonsystem.entity.ProblemDesignation;
import in.andonsystem.entity.ProblemDesignationDao;

/**
 * Created by razamd on 4/8/2017.
 */

public class ProblemDesignationService {

    private ProblemDesignationDao userBuyerDao;

    public ProblemDesignationService(App app){
        this.userBuyerDao = app.getDaoSession().getProblemDesignationDao();
    }

    public long save(ProblemDesignation userBuyer){
        return userBuyerDao.insert(userBuyer);
    }

    public void saveBatch(List<ProblemDesignation> list){
        userBuyerDao.insertInTx(list);
    }

    public void deleteByProblem(Long userId){
        List<ProblemDesignation> list = userBuyerDao.queryBuilder().where(ProblemDesignationDao.Properties.ProblemId.eq(userId)).list();
        userBuyerDao.deleteInTx(list);
    }

    public void deleteAll(){
        userBuyerDao.deleteAll();
    }
}
