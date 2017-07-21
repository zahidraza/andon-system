package in.andonsystem.service;

import android.util.Log;

import java.util.List;

import in.andonsystem.App;
import in.andonsystem.entity.Designation;
import in.andonsystem.entity.DesignationDao;
import in.andonsystem.entity.DaoSession;

/**
 * Created by razamd on 3/31/2017.
 */

public class DesignationService {

    private final String TAG = DesignationService.class.getSimpleName();

    private final DesignationDao designationDao;

    public DesignationService(App app){
        DaoSession session= app.getDaoSession();
        designationDao = app.getDaoSession().getDesignationDao();
    }

    public long save(Designation designation){
        return designationDao.insert(designation);
    }

    public void saveAll(List<Designation> designations){
        Log.i(TAG, "saveAll()");
        designationDao.insertInTx(designations);
    }

    public Designation findOne(Long id){
        return designationDao.load(id);
    }

//    public List<Designation> findByTeam(String team){
//        Log.i(TAG, "findByTeam()");
//        return designationDao.queryBuilder()
//                .where(DesignationDao.Properties.Team.eq(team))
//                .orderAsc(DesignationDao.Properties.Name)
//                .list();
//    }

    public void deleteAll(){
        designationDao.deleteAll();
    }

}
