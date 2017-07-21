package in.andonsystem.service;

import java.util.List;

import in.andonsystem.App;
import in.andonsystem.entity.ProblemDesignation;
import in.andonsystem.entity.ProblemDesignationDao;

/**
 * Created by razamd on 4/8/2017.
 */

public class ProblemDesignationService {

    private ProblemDesignationDao problemDesignationDao;

    public ProblemDesignationService(App app){
        this.problemDesignationDao = app.getDaoSession().getProblemDesignationDao();
    }

    public long save(ProblemDesignation problemDesignation){
        return problemDesignationDao.insert(problemDesignation);
    }

    public void saveBatch(List<ProblemDesignation> list){
        problemDesignationDao.insertInTx(list);
    }

    public void deleteByProblem(Long problemId){
        List<ProblemDesignation> list = problemDesignationDao.queryBuilder().where(ProblemDesignationDao.Properties.ProblemId.eq(problemId)).list();
        problemDesignationDao.deleteInTx(list);
    }

    public void deleteAll(){
        problemDesignationDao.deleteAll();
    }
}
