package in.andonsystem.service;

import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.andonsystem.App;
import in.andonsystem.entity.Buyer;
import in.andonsystem.entity.BuyerDao;
import in.andonsystem.entity.Issue2;
import in.andonsystem.entity.Issue2Dao;
import in.andonsystem.entity.User;

/**
 * Created by razamd on 3/31/2017.
 */

public class IssueService2 {

    private final String TAG = IssueService2.class.getSimpleName();

    private final Issue2Dao issueDao;

    public IssueService2(App app){
        issueDao = app.getDaoSession().getIssue2Dao();
    }

    public void saveOrUpdate(List<Issue2> issues){
        issueDao.insertOrReplaceInTx(issues);
    }

    public Issue2 findOne(Long id){
        return issueDao.loadDeep(id);
    }

    public List<Issue2> findAll(){
        Log.d(TAG, "findAll" );
        return issueDao.queryBuilder().where(Issue2Dao.Properties.Deleted.eq(false)).list();
    }

    public List<Issue2> findAllByTeam(String team){
        Log.d(TAG, "findAllByTeam: team = " + team);
        QueryBuilder<Issue2> queryBuilder = issueDao.queryBuilder().where(Issue2Dao.Properties.Deleted.eq(false));
        queryBuilder.join(Issue2Dao.Properties.BuyerId,Buyer.class)
                .where(BuyerDao.Properties.Team.eq(team));
        return   queryBuilder.list();
    }

    public List<Issue2> findAllByBuyers(List<Buyer> buyers){
        Log.d(TAG, "findAllByBuyers");
        List<Issue2> result = new ArrayList<>();
        for(Buyer b: buyers){
            result.addAll(issueDao.queryBuilder().where(Issue2Dao.Properties.BuyerId.eq(b.getId()), Issue2Dao.Properties.Deleted.eq(false)).list());
        }
        return result;
    }

    public List<Issue2> findAllByUser(User user){
        Log.d(TAG, "findAllByUser: user = " + user.getName());
        return issueDao.queryBuilder().where(Issue2Dao.Properties.RaisedBy.eq(user.getId()), Issue2Dao.Properties.Deleted.eq(false)).list();
    }

    public void deleteAllOlder(){
        Log.d(TAG, "deleteOlder than 2 days.");
        Long time = new Date().getTime();
        Date midnight = new Date(time -((24 * 60 * 60 * 1000) + time % (24 * 60 * 60 * 1000)));
        List<Issue2> issue2s = issueDao.queryBuilder()
                .where(Issue2Dao.Properties.RaisedAt.lt(midnight))
                .list();
        Log.d(TAG,"deleting " + issue2s.size() + " issues");
        issueDao.deleteInTx(issue2s);
    }

    public void deleteAll() {
        issueDao.deleteAll();
    }

}
