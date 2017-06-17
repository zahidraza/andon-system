package in.andonsystem.v1.task;

import in.andonsystem.Level;
import in.andonsystem.util.ApplicationContextUtil;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.v1.entity.Issue1;
import in.andonsystem.v1.service.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Checks whether specific issue is fixed by intended level (processingAt)
 * Created by razamd on 4/4/2017.
 */
public class FixTask extends Thread{
    private final Logger logger = LoggerFactory.getLogger(FixTask.class);

    private Long issueId;
    private Integer checkProcessingAt;
    private String message;

    public FixTask(Long issueId, Integer checkProcessingAt, String message){
        this.issueId = issueId;
        this.checkProcessingAt = checkProcessingAt;
        this.message = message;
    }

    @Override
    public void run() {
        logger.debug("Executing FixTask: issueId = {}, checkProcessinAt = {}", issueId, checkProcessingAt);
        ApplicationContext context = ApplicationContextUtil.getApplicationContext();
        IssueService issueService = context.getBean(IssueService.class);

        Issue1 issue = issueService.findOne(issueId,true);

        //If issue is not fixed yet and It is processing at level below or equal to checkProcessingAT
        if(issue.getFixAt() == null && issue.getProcessingAt() <= checkProcessingAt){
            //Send notification to level (checkProcessingAt + 1)
            logger.debug("Sending notification to level " + (checkProcessingAt+1) + " users");

            String mobileNumbers = checkProcessingAt == 1 ? MiscUtil.getUserMobileNumbers(issue.getProblem(), Level.LEVEL2) : MiscUtil.getUserMobileNumbers(issue.getProblem(), Level.LEVEL3);

            if (mobileNumbers != null) {
                boolean result = MiscUtil.sendSMS(mobileNumbers,message);
                if (result) {
                    logger.info("Sent sms to = {}, issueId-1 = {}",mobileNumbers, issueId);
                }
            }else {
                logger.info("No Users found for sending sms");
            }

            //update processing at value = (checkProcessingAt + 1)
            issueService.updateProcessingAt(issueId, (checkProcessingAt+1));
        }else {
            logger.info("Ignoring FixTask as issue is fixed. issueId = {}", issueId);
        }

    }
}
