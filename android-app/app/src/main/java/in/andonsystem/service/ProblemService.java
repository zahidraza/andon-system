package in.andonsystem.service;

import android.util.Log;

import java.util.List;

import in.andonsystem.App;
import in.andonsystem.entity.DaoSession;
import in.andonsystem.entity.Problem;
import in.andonsystem.entity.ProblemDao;

/**
 * Created by mdzahidraza on 18/06/17.
 */

public class ProblemService {

    private final String TAG = ProblemService.class.getSimpleName();

    private final ProblemDao problemDao;

    public ProblemService(App app){
        DaoSession session= app.getDaoSession();
        problemDao = app.getDaoSession().getProblemDao();
    }

    public long save(Problem problem){
        return problemDao.insert(problem);
    }

    public void saveAll(List<Problem> problems){
        Log.i(TAG, "saveAll()");
        problemDao.insertInTx(problems);
    }

    public Problem findOne(Long id){
        return problemDao.load(id);
    }

//    public List<Problem> findByTeam(String team){
//        Log.i(TAG, "findByTeam()");
//        return problemDao.queryBuilder()
//                .where(ProblemDao.Properties.Team.eq(team))
//                .orderAsc(ProblemDao.Properties.Name)
//                .list();
//    }

    public void deleteAll(){
        problemDao.deleteAll();
    }
}
