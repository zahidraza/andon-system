package in.andonsystem;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;
import in.andonsystem.v1.models.Preferences;
import in.andonsystem.v1.scheduler.FutureTaskManager;
import in.andonsystem.v1.services.DBTableService;
import in.andonsystem.v1.services.DeptService;
import in.andonsystem.v1.services.DesignationService;
import in.andonsystem.v1.services.IssueService;
import in.andonsystem.v1.services.MiscService;
import in.andonsystem.v1.services.ProblemService;
import in.andonsystem.v1.services.SchedulerService;
import in.andonsystem.v1.services.SectionService;
import in.andonsystem.v1.util.ConnectionPool;
import in.andonsystem.v1.util.MiscUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;



@WebListener
public class ContextListener implements ServletContextListener{

    //private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    ScheduledFuture<?> fixIssueScheduler;
    FutureTaskManager taskManager;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        taskManager = new FutureTaskManager();
        taskManager.start();

        ServletContext context = sce.getServletContext();
        Connection conn = null;
        try {
            conn = ConnectionPool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        DBTableService dbService = new DBTableService(conn);
        try{
            //Create Tables 
            dbService.createUserTable();
            dbService.createDeptTable();
            dbService.createSectionTable();
            dbService.createProblemTable();
            dbService.createDesignationTable();
            dbService.createDesignationLineTable();
            dbService.createDesignationProblemTable();
            dbService.createIssueTable();
            dbService.createForgotPasswordTable();
            dbService.createForgotPasswordEvent();
            //dbService.addSections();
            //dbService.addDepartments();

            //Instantiate Services
            MiscUtil miscUtil = MiscUtil.getInstance();
            MiscService mService = new MiscService();
            ProblemService pService = new ProblemService(conn);
            DeptService dService = new DeptService(conn);
            SectionService sService = new SectionService(conn);
            DesignationService desgnService = new DesignationService(conn);

            //Write the APP_LAUNCH time in properties file
            long timeNow = System.currentTimeMillis();        //GMT time online
            miscUtil.setConfigProperty(Preferences.APP_LAUNCH, "" + timeNow);

            //Schedule automatic fixing of issue if left unfixed till factory close
            long days = TimeUnit.MILLISECONDS.toDays(timeNow);
            long millisToday = (timeNow - TimeUnit.DAYS.toMillis(days));  //GMT:This number of milliseconds after 12:00 today
            int minutesToday = (int )TimeUnit.MILLISECONDS.toMinutes(millisToday);  //GMT       
            minutesToday += (60*5) + 30;  //GMT+05:30 minutesToday 

            int endHour = Integer.parseInt(miscUtil.getConfigProperty(Preferences.END_HOUR));
            int endMinute = Integer.parseInt(miscUtil.getConfigProperty(Preferences.END_MINUTE));
            int factoryCloseMinute = (60*endHour) + endMinute;

            int initialDelay = 0;
            if(minutesToday <= factoryCloseMinute){
                initialDelay = (factoryCloseMinute - minutesToday) + 1;
            }else{
                initialDelay = ((24*60) - minutesToday) + factoryCloseMinute + 1;
            }
            System.out.println("Initial Schedule delay(in minutes): "+initialDelay);

            ScheduledExecutorService scheduler = FutureTaskManager.getScheduler();

            FixIssuesThread thread = new FixIssuesThread(endHour, endMinute);
            fixIssueScheduler = scheduler.scheduleAtFixedRate(thread, initialDelay, (60*24), TimeUnit.MINUTES);


            //Initialize Application Scope Attributes
            //Set number of Lines
            context.setAttribute(
                    "lines",
                    miscUtil.getConfigProperty(Preferences.LINES)
            );
            //Set all designation names
            context.setAttribute("designations",desgnService.getDesgns());
            //Set Problems
            context.setAttribute("problems", pService.getProbs());
            //Set Departments
            context.setAttribute("depts", dService.getDepartments());
            //Set Sections
            context.setAttribute("sections", sService.getSections());
            context.setAttribute("start_hour", miscUtil.getConfigProperty(Preferences.START_HOUR));
            context.setAttribute("start_minute", miscUtil.getConfigProperty(Preferences.START_MINUTE));
            context.setAttribute("end_hour", miscUtil.getConfigProperty(Preferences.END_HOUR));
            context.setAttribute("end_minute", miscUtil.getConfigProperty(Preferences.END_MINUTE));

            context.setAttribute("time_ack", miscUtil.getConfigProperty(Preferences.TIME_ACK));
            context.setAttribute("time_level1", miscUtil.getConfigProperty(Preferences.TIME_LEVEL1));
            context.setAttribute("time_level2", miscUtil.getConfigProperty(Preferences.TIME_LEVEL2));


        }catch(Exception e){
            e.printStackTrace();
        }
        finally{
            try{
                conn.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("ContextDestroyed()");

        Boolean result = fixIssueScheduler.cancel(true);
        if(result){
            System.out.println("Automatic Fix Thread Stopped Successfully");
        }
        FutureTaskManager.getScheduler().shutdownNow();
        taskManager.shutdown();
        try{
            AbandonedConnectionCleanupThread.shutdown();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * This is Thread used to Schedule Automatic Fix of Issues
     */

    class FixIssuesThread implements Runnable{
        private int endHour;
        private int endMinute;

        public FixIssuesThread(int endHour,int endMinute){
            this.endHour = endHour;
            this.endMinute = endMinute;
        }

        @Override
        public void run() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
            df.setTimeZone(TimeZone.getTimeZone("GMT+05:30"));
            long timeNow = System.currentTimeMillis();
            String date = df.format(new Date(timeNow));

            System.out.println(new Date(timeNow) + ", FixIssuesThread.run()");

            String time = String.format("%02d:%02d:00", endHour,endMinute);
            String datetime = date + time;
            System.out.println("all unfixed issues being fixed with fixAt time = "+datetime);
            Connection conn = null;
            try {
                conn = ConnectionPool.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            IssueService iService = new IssueService(conn);
            try{
                int count = iService.fixIssueAutomatic(datetime);
                System.out.println("" + count + " issues Fixed automatically");
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try{
                    conn.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }

}
