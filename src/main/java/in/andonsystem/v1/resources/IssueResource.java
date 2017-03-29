package in.andonsystem.v1.resources;


import in.andonsystem.v1.models.Issue;
import in.andonsystem.v1.models.IssueData;
import in.andonsystem.v1.scheduler.FutureTaskManager;
import in.andonsystem.v1.scheduler.MyTask;
import in.andonsystem.v1.services.DeptService;
import in.andonsystem.v1.services.DesignationService;
import in.andonsystem.v1.services.IssueService;
import in.andonsystem.v1.util.MiscUtil;
import in.andonsystem.v1.services.ProblemService;
import in.andonsystem.v1.services.SMSService;
import in.andonsystem.v1.services.SchedulerService;
import in.andonsystem.v1.services.SectionService;
import in.andonsystem.v1.services.UserService;
import in.andonsystem.v1.threads.ScheduleAckThread;
import in.andonsystem.v1.threads.ScheduleSeekHelpThread;
import in.andonsystem.v1.threads.ScheduleSolvedThread;
import in.andonsystem.v1.util.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author Md Jawed Akhtar
 */
@Path("/issue")
public class IssueResource {
    @Context
    private ServletContext context;
    //private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    //Raise Issue
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postIssue(@QueryParam("authToken") String authToken, Issue issue) throws Exception {
        int facoryClosed = checkFactoryClosed();
        if (facoryClosed == 1) {
            return "factory closed";
        } else if (facoryClosed == -1) {
            return "factory not opened yet";
        }

        Connection conn = null;
        try {
            conn = ConnectionPool
                    .getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UserService uService = new UserService(conn);
        IssueService iService = new IssueService(conn);
        DesignationService desgnService = new DesignationService(conn);
        int issueId = 0;
        try {
            //Authenticate user and save
            int userId = uService.authUser(authToken);

            if (userId == issue.getRaisedBy()) {
                issueId = iService.saveIssue(issue);
            } else {
                return "fail, user unauthorised";
            }
            if (issueId == 0) {
                return "fail, Unable to raise issue";
            }
            //Get Issue Details
            int line = issue.getLine();
            int probId = issue.getProbId();

            //Get level 1 concerned users
            System.out.println(
                    "\nTime: " + new Date(System.currentTimeMillis()) + ", Issue Raised, issuedId :" + issueId);
            List<Integer> list = desgnService.getDesgnConcerned(line, probId, 1);
            System.out.println("Level 1 Designations mapped:\n");
            for (int i = 0; i < list.size(); i++) {
                System.out.println(desgnService.getDesgnName(list.get(i)));
            }

            String message = genrateMessage(issue);
            String to = "";
            if (list.size() > 0) {
                //Find all designation Ids
                String desgnIds = "(" + String.valueOf(list.get(0));
                for (int i = 1; i < list.size(); i++) {
                    desgnIds += "," + String.valueOf(list.get(i));
                }
                desgnIds += ")";
                //Find mobile number of all user with these desination ids
                List<String> mobiles = uService.getUserMobiles(desgnIds);
                if (mobiles.size() > 0) {
                    to += mobiles.get(0);
                    for (int i = 1; i < mobiles.size(); i++) {
                        to += "," + mobiles.get(i);
                    }
                }
            }
            System.out.println("Send SMS to mobiles:" + to);
            SMSService.sendSMS(to, message);

            //If user level 1 does not acknowledges in time_ack, trigger notification to level 2
            int time_ack = Integer.parseInt((String) context.getAttribute("time_ack"));
            ScheduleAckThread thread1 = new ScheduleAckThread(issueId, line, probId, message, 2);
            FutureTaskManager
                    .manage(new MyTask(System.currentTimeMillis(), (time_ack * 60 * 1000), thread1, MyTask.QUEUED));

            //If user level 2 does not acknowledges in time_level2, trigger notification to level 3
            int time_level2 = Integer.parseInt((String) context.getAttribute("time_level2"));
            ScheduleAckThread thread2 = new ScheduleAckThread(issueId, line, probId, message, 3);
            FutureTaskManager
                    .manage(new MyTask(System.currentTimeMillis(), ((time_ack + time_level2) * 60 * 1000), thread2,
                                       MyTask.QUEUED));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (issueId != 0? "success" : "fail");
    }

    @GET
    @Path("/{after}")
    @Produces(MediaType.APPLICATION_JSON)
    public IssueData getAllIssues(@PathParam("after") long after) {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        IssueService iService = new IssueService(conn);
        IssueData data = null;
        try {
            List<Issue> list = iService.getIssues(after);
            data = new IssueData(System.currentTimeMillis(), list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return data;
    }

    @POST
    @Path("/ack")
    @Produces(MediaType.TEXT_PLAIN)
    public String acknowledge(@FormParam("authToken") String authToken, @FormParam("issueId") int issueId,
                              @FormParam("ackBy") int ackBy) {

        if (checkFactoryClosed() == 1) {
            return "factory closed";
        }
        if (checkFactoryClosed() == -1) {
            return "factory not opened yet";
        }
        System.out.println(new Date(System.currentTimeMillis()) + " : acknowledge(), issueId = " + issueId);

        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        UserService uService = new UserService(conn);
        IssueService iService = new IssueService(conn);
        Boolean status = false;
        try {
            int userId = uService.authUser(authToken);

            if (userId == ackBy) {
                status = iService.acknowledgeIssue(issueId, ackBy);
            } else {
                return "fail, user unauthorised";
            }
            if (status == false) {
                return "fail, Unable to acknowledge issue";
            }
            //Get Issue Details
            Issue issue = iService.getIssue(issueId);
            int line = issue.getLine();
            int probId = issue.getProbId();

            String message = genrateMessage(issue);
            long time_raise = iService.getRaiseTime(issueId);
            long time_now = System.currentTimeMillis();
            int time_ack = Integer.parseInt((String) context.getAttribute("time_ack"));
            int time_level1 = Integer.parseInt((String) context.getAttribute("time_level1"));
            int time_level2 = Integer.parseInt((String) context.getAttribute("time_level2"));
            long diff = time_now - time_raise;
            int diffMinute = (int) TimeUnit.MILLISECONDS.toMinutes(diff);

            if (diffMinute < time_ack) {      //Level 1 has/can acknowledged: Schedule sms for level 2 & 3
                ScheduleSolvedThread thread1 = new ScheduleSolvedThread(issueId, line, probId, message, 2);
                FutureTaskManager.manage(new MyTask(System.currentTimeMillis(), (time_level1 * 60 * 1000), thread1,
                                                    MyTask.QUEUED));

                ScheduleSolvedThread thread2 = new ScheduleSolvedThread(issueId, line, probId, message, 3);
                FutureTaskManager
                        .manage(new MyTask(System.currentTimeMillis(), ((time_level1 + time_level2) * 60 * 1000),
                                           thread2, MyTask.QUEUED));
            } else if (diffMinute < (time_ack +
                                     time_level2)) { // Level 1 or level 2, any one can/has acknowledge: Level 2 already received sms, schedule for level 3
                int time_schedule = (time_ack + time_level2) - diffMinute;
                ScheduleSolvedThread thread2 = new ScheduleSolvedThread(issueId, line, probId, message, 3);
                //scheduler.schedule(thread2,time_schedule, TimeUnit.MINUTES);
                FutureTaskManager.manage(new MyTask(System.currentTimeMillis(), (time_schedule * 60 * 1000), thread2,
                                                    MyTask.QUEUED));
            } else {
                //Every level has received notification. Do not schedule sms
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (status? "success" : "fail");
    }

    @POST
    @Path("/seek_help")
    @Produces(MediaType.TEXT_PLAIN)
    public String seekHelp(@FormParam("authToken") String authToken, @FormParam("issueId") int issueId,
                           @FormParam("level") int level) {
        if (checkFactoryClosed() == 1) {
            return "factory closed";
        }
        if (checkFactoryClosed() == -1) {
            return "factory not opened yet";
        }
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        IssueService iService = new IssueService(conn);
        DesignationService desgnService = new DesignationService(conn);
        UserService uService = new UserService(conn);
        Boolean result = false;
        try {
            Issue issue = iService.getIssue(issueId);
            int line = issue.getLine();
            int probId = issue.getProbId();

            result = iService.saveSeekHelp(issueId, level);
            if (!result) {
                return "fail, unable to seek help";
            }

            System.out.println("\nTime: " + new Date(System.currentTimeMillis()) + ", Help sought by level : " + level +
                               " , issuedId :" + issueId);
            List<Integer> list = null;
            if (level == 1) {
                list = desgnService.getDesgnConcerned(line, probId, 2);
            }
            if (level == 2) {
                list = desgnService.getDesgnConcerned(line, probId, 3);
            }
            System.out.println("Level " + (level + 1) + " Designations mapped:\n");
            for (int i = 0; i < list.size(); i++) {
                System.out.println(desgnService.getDesgnName(list.get(i)));
            }
            String message = genrateMessage(issue);
            String to = "";
            if (list.size() > 0) {
                //Find all designation Ids
                String desgnIds = "(" + String.valueOf(list.get(0));
                for (int i = 1; i < list.size(); i++) {
                    desgnIds += "," + String.valueOf(list.get(i));
                }
                desgnIds += ")";
                //Find mobile number of all user with these desination ids
                List<String> mobiles = uService.getUserMobiles(desgnIds);
                if (mobiles.size() > 0) {
                    to += mobiles.get(0);
                    for (int i = 1; i < mobiles.size(); i++) {
                        to += "," + mobiles.get(i);
                    }
                }
            }
            System.out.println("Send SMS to mobiles:" + to);
            SMSService.sendSMS(to, message);
            if (level == 1) {
                //if level 1 sought for help, Still provide time_level2 time to level 2 untill Level 3 receives notification
                //If user level 2 unable to solve in time_level2 or has seeked help , trigger notification to level 3
                int time_level2 = Integer.parseInt((String) context.getAttribute("time_level2"));
                ScheduleSeekHelpThread thread = new ScheduleSeekHelpThread(issueId, line, probId, message, 3);
                FutureTaskManager.manage(new MyTask(System.currentTimeMillis(), (time_level2 * 60 * 1000), thread,
                                                    MyTask.QUEUED));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (result? "success" : "fail");
    }

    @POST
    @Path("/fix")
    @Produces(MediaType.TEXT_PLAIN)
    public String fix(@FormParam("authToken") String authToken, @FormParam("issueId") int issueId,
                      @FormParam("fixBy") int fixBy) {
        if (checkFactoryClosed() == 1) {
            return "factory closed";
        }
        if (checkFactoryClosed() == -1) {
            return "factory not opened yet";
        }
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        UserService uService = new UserService(conn);
        IssueService iService = new IssueService(conn);
        Boolean status = false;
        try {
            int userId = uService.authUser(authToken);

            if (userId != 0) {
                status = iService.fixIssue(issueId, fixBy);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return (status? "success" : "fail");
    }

    //Value 0 : open  1 : closed -1 : not opened yet 
    private int checkFactoryClosed() {

        long timeNow = System.currentTimeMillis();

        DateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
        String time = df.format(new Date(timeNow));
        int hours = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);
        int totalMinutes = (60 * hours) + minutes;

        //MiscUtil miscUtil = MiscUtil.getInstance();
        int startHour = Integer.parseInt((String) context.getAttribute("start_hour"));
        int startMinutes = Integer.parseInt((String) context.getAttribute("start_minute"));
        int endHour = Integer.parseInt((String) context.getAttribute("end_hour"));
        int endMinutes = Integer.parseInt((String) context.getAttribute("end_minute"));

        int startTime = (60 * startHour) + startMinutes;
        int endTime = (60 * endHour) + endMinutes;

        if (totalMinutes < startTime) {
            return -1;
        }
        if (totalMinutes > endTime) {
            return 1;
        }
        return 0;
    }

    private String genrateMessage(Issue issue) {
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SectionService sService = new SectionService(conn);
        DeptService dService = new DeptService(conn);
        ProblemService pService = new ProblemService(conn);
        StringBuffer buffer = new StringBuffer();
        try {
            String dept = dService.getDeptName(issue.getDeptId());
            if (dept.contains("Industrial")) {
                dept = "IE";
            } else if (dept.contains("Human")) {
                dept = "HR";
            }


            buffer.append("Line: " + issue.getLine());
            buffer.append("\nSection: " + sService.getSectionName(issue.getSecId()));
            buffer.append("\nDept: " + dept);
            buffer.append("\nIssue: " + pService.getProblemName(issue.getProbId()));
            buffer.append("\nRemarks: " + issue.getDesc());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return buffer.toString();
    }

}
