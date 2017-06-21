package in.andonsystem.v2.task;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.v2.entity.Issue2;
import in.andonsystem.Level;
import in.andonsystem.v2.service.IssueService;
import in.andonsystem.util.ApplicationContextUtil;
import in.andonsystem.util.Scheduler;
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

        Issue2 issue = issueService.findOne(issueId,true);

        if(!issue.getDeleted() && issue.getAckAt() == null){  //If not acknowldged yet
            //Send message to L2 users
            logger.debug("Sending notification to L2 users");

            String mobileNumbers = MiscUtil.getUserMobileNumbers(issue.getBuyer(), Level.LEVEL2);

            if (mobileNumbers != null) {
                boolean result = MiscUtil.sendSMS(mobileNumbers,message);
                if (result) {
                    logger.info("Sent sms to = {}, issueId-2 = {}",mobileNumbers, issueId);
                }
            }else {
                logger.info("No Users found for sending sms");
            }

            //Update processingAt
            issueService.updateProcessingAt(issue.getId(),2);
            //Submit FixTask(2 hrs, 2)
            Long fixL2Time = Long.parseLong(ConfigUtility.getInstance().getConfigProperty(Constants.APP_V2_FIX_L2_TIME, "120"));
            Scheduler.getInstance().submit(new FixTask(issue.getId(),2, message),fixL2Time);
        }else {
            logger.info("Ignoring AckTask as Issue1 is acknowledged. issueId = {}", issueId);
        }


    }
}
