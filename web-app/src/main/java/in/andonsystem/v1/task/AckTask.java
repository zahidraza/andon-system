package in.andonsystem.v1.task;

import in.andonsystem.Constants;
import in.andonsystem.util.ApplicationContextUtil;
import in.andonsystem.util.ConfigUtility;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.util.Scheduler;
import in.andonsystem.v1.entity.Issue1;
import in.andonsystem.Level;
import in.andonsystem.v1.service.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by razamd on 4/4/2017.
 */
public class AckTask extends Thread {
    private final Logger logger = LoggerFactory.getLogger(AckTask.class);

    private Long issueId;
    private String message;

    public AckTask(Long issueId, String message){
        this.issueId = issueId;
        this.message = message;
    }

    @Override
    public void run() {
        logger.debug("Executing AckTask: issueId = {}", issueId);
        ApplicationContext context = ApplicationContextUtil.getApplicationContext();
        IssueService issueService = context.getBean(IssueService.class);

        Issue1 issue = issueService.findOne(issueId,true);

        if(!issue.getDeleted() && issue.getAckAt() == null){  //If not acknowldged yet
            //Send message to L2 users
            logger.debug("Sending notification to L2 users");

            String mobileNumbers =  MiscUtil.getUserMobileNumbers(issue.getProblem(), Level.LEVEL2);
            if (mobileNumbers != null) {
                boolean result = MiscUtil.sendSMS(mobileNumbers,message);
                if (result) {
                    logger.info("Sent sms to = {}, issueId-1 = {}",mobileNumbers, issueId);
                }
            }else {
                logger.info("No Users found for sending sms");
            }

            //Update processingAt
            issueService.updateProcessingAt(issue.getId(),2);
            //Submit FixTask(2 hrs, 2)
            Long fixL2Time = Long.parseLong(ConfigUtility.getInstance().getConfigProperty(Constants.APP_V1_FIX_L2_TIME, "10"));
            Scheduler.getInstance().submit(new FixTask(issue.getId(),2, message),fixL2Time);
        }else {
            logger.info("Ignoring AckTask as Issue1 is acknowledged. issueId = {}", issueId);
        }


    }
}
