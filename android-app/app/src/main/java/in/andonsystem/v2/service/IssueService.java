package in.andonsystem.v2.service;

import android.util.Log;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.andonsystem.App;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.BuyerDao;
import in.andonsystem.v2.entity.Issue;
import in.andonsystem.v2.entity.IssueDao;
import in.andonsystem.v2.entity.User;

/**
 * Created by razamd on 3/31/2017.
 */

public class IssueService {

    private final String TAG = IssueService.class.getSimpleName();

    private final IssueDao issueDao;

    public IssueService(App app){
        issueDao = app.getDaoSession().getIssueDao();
    }

    public void saveOrUpdate(List<Issue> issues){
        issueDao.insertOrReplaceInTx(issues);
    }

    public Issue findOne(Long id){
        return issueDao.loadDeep(id);
    }

    public List<Issue> findAll(){
        Log.d(TAG, "findAll" );
        return issueDao.loadAll();
    }

    public List<Issue> findAllByTeam(String team){
        Log.d(TAG, "findAllByTeam: team = " + team);
        QueryBuilder<Issue> queryBuilder = issueDao.queryBuilder();
        queryBuilder.join(IssueDao.Properties.BuyerId,Buyer.class)
                .where(BuyerDao.Properties.Team.eq(team));
        return   queryBuilder.list();
    }

    public List<Issue> findAllByBuyers(List<Buyer> buyers){
        Log.d(TAG, "findAllByBuyers");
        List<Issue> result = new ArrayList<>();
        QueryBuilder<Issue> queryBuilder = issueDao.queryBuilder();
        for(Buyer b: buyers){
            result.addAll(issueDao.queryBuilder().where(IssueDao.Properties.BuyerId.eq(b.getId())).list());
        }
        return result;
    }

    public List<Issue> findAllByUser(User user){
        Log.d(TAG, "findAllByUser: user = " + user.getName());
        return issueDao.queryBuilder().where(IssueDao.Properties.RaisedBy.eq(user.getId())).list();
    }

    public void deleteAllOlder(){
        Log.d(TAG, "deleteOlder than 2 days.");
        Long time = new Date().getTime();
        Date midnight = new Date(time -((24 * 60 * 60 * 1000) + time % (24 * 60 * 60 * 1000)));
        List<Issue> issues = issueDao.queryBuilder()
                .where(IssueDao.Properties.RaisedAt.lt(midnight))
                .list();
        issueDao.deleteInTx(issues);
    }

    public void deleteAll() {
        issueDao.deleteAll();
    }

}
