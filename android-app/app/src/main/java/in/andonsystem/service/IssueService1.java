package in.andonsystem.service;

import android.util.Log;

import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import in.andonsystem.App;
import in.andonsystem.entity.Buyer;
import in.andonsystem.entity.BuyerDao;
import in.andonsystem.entity.Designation;
import in.andonsystem.entity.Issue1;
import in.andonsystem.entity.Issue1Dao;
import in.andonsystem.entity.Problem;
import in.andonsystem.entity.ProblemDao;
import in.andonsystem.entity.User;
import in.andonsystem.util.MiscUtil;

/**
 * Created by razamd on 3/31/2017.
 */

public class IssueService1 {

    private final String TAG = IssueService2.class.getSimpleName();

    private final Issue1Dao issueDao;

    public IssueService1(App app){
        issueDao = app.getDaoSession().getIssue1Dao();
    }

    public void saveOrUpdate(List<Issue1> issues){
        issueDao.insertOrReplaceInTx(issues);
    }

    public Issue1 findOne(Long id){
        return issueDao.loadDeep(id);
    }

    public List<Issue1> findAll(){
        Log.d(TAG, "findAllCity" );
        return issueDao.queryBuilder()
                .where(Issue1Dao.Properties.Deleted.eq(false))
                .list();
    }

    public List<Issue1> findAllWithFilter(Integer line, String section, String department) {
        Log.d(TAG,"findAllWithFilter: line = " + line + ", section = " + section + ", dept = " + department);
        QueryBuilder<Issue1> qb = issueDao.queryBuilder();
        if (line != null && section != null && department != null){
            qb.where(
                    Issue1Dao.Properties.Line.eq(line),
                    Issue1Dao.Properties.Section.eq(section),
                    Issue1Dao.Properties.Deleted.eq(false)
            );

            qb.join(Issue1Dao.Properties.ProblemId, Problem.class)
                    .where(ProblemDao.Properties.Department.eq(department));
        }
        else if (line == null && section != null && department != null) {
            qb.where(
                    Issue1Dao.Properties.Section.eq(section),
                    Issue1Dao.Properties.Deleted.eq(false)
            );
            qb.join(Issue1Dao.Properties.ProblemId, Problem.class)
                    .where(ProblemDao.Properties.Department.eq(department));
        }
        else if (line != null && section == null && department != null) {
            qb.where(
                    Issue1Dao.Properties.Line.eq(line),
                    Issue1Dao.Properties.Deleted.eq(false)
            );
            qb.join(Issue1Dao.Properties.ProblemId, Problem.class)
                    .where(ProblemDao.Properties.Department.eq(department));
        }
        else if (line != null && section != null && department == null) {
            qb.where(
                        Issue1Dao.Properties.Line.eq(line),
                        Issue1Dao.Properties.Section.eq(section),
                        Issue1Dao.Properties.Deleted.eq(false)
                );
        }
        else if (line == null && section == null && department != null) {
            qb.where(Issue1Dao.Properties.Deleted.eq(false));
            qb.join(Issue1Dao.Properties.ProblemId, Problem.class)
                    .where(ProblemDao.Properties.Department.eq(department));
        }
        else if (line == null && section != null && department == null) {
            qb.where(
                    Issue1Dao.Properties.Section.eq(section),
                    Issue1Dao.Properties.Deleted.eq(false)
            );
        }
        else if (line != null && section == null && department == null) {
            qb.where(
                    Issue1Dao.Properties.Line.eq(line),
                    Issue1Dao.Properties.Deleted.eq(false)
            );
        }
        else {
            qb.where(Issue1Dao.Properties.Deleted.eq(false));
        }
        return qb.list();
    }

//    public List<Issue1> findAllByTeam(String team){
//        Log.d(TAG, "findAllByTeam: team = " + team);
//        QueryBuilder<Issue1> queryBuilder = issueDao.queryBuilder();
//        queryBuilder.join(Issue1Dao.Properties.BuyerId,Buyer.class)
//                .where(BuyerDao.Properties.Team.eq(team));
//        return   queryBuilder.list();
//    }

//    public List<Issue1> findAllByBuyers(List<Buyer> buyers){
//        Log.d(TAG, "findAllByBuyers");
//        List<Issue1> result = new ArrayList<>();
//        QueryBuilder<Issue1> queryBuilder = issueDao.queryBuilder();
//        for(Buyer b: buyers){
//            result.addAll(issueDao.queryBuilder().where(Issue1Dao.Properties.BuyerId.eq(b.getId())).list());
//        }
//        return result;
//    }

    public List<Issue1> getAllIssueForDesignation(Designation designation) {
        Set<Integer> lines = MiscUtil.getLines(designation.getLines());
        QueryBuilder<Issue1> qb = issueDao.queryBuilder();
        Join<Issue1,Problem> problem = qb.join(Issue1Dao.Properties.ProblemId, Problem.class);
        List<Issue1> list = qb.list();
        List<Issue1> result = new ArrayList<>();
        for (Issue1 issue: list){
            if (issue.getProblem().getDesignations().contains(designation)) {
                if (lines.contains(issue.getLine())){
                    result.add(issue);
                }
            }
        }
        return result;
    }

    public List<Issue1> findAllByUser(User user){
        Log.d(TAG, "findAllByUser: user = " + user.getName());
        return issueDao.queryBuilder().where(Issue1Dao.Properties.RaisedBy.eq(user.getId())).list();
    }

    public void deleteAllOlder(){
        Log.d(TAG, "deleteOlder than 2 days.");
        Long time = new Date().getTime();
        Date midnight = new Date(time -(time % (24 * 60 * 60 * 1000)));
        List<Issue1> issues = issueDao.queryBuilder()
                .where(Issue1Dao.Properties.RaisedAt.lt(midnight))
                .list();
        issueDao.deleteInTx(issues);
    }

    public void deleteAll() {
        issueDao.deleteAll();
    }

}
