package in.andonsystem.v2.tasks;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.Issue2;
import in.andonsystem.v2.entity.User;
import in.andonsystem.v2.enums.Level;
import in.andonsystem.v2.service.IssueService;
import in.andonsystem.util.ApplicationContextUtil;
import in.andonsystem.util.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

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
        System.out.println(issue);

        if(issue.getAckAt() == null){  //If not acknowldged yet
            //Send message to L2 users
            System.out.println("Sending notification to L2 users");

            Buyer buyer = issue.getBuyer();
            List<User> users = buyer.getUsers().stream()
                                    .filter(user -> user.getLevel().equalsIgnoreCase(Level.LEVEL2.getValue()))
                                    .collect(Collectors.toList());

            StringBuilder builder = new StringBuilder();
            users.forEach(user -> builder.append(user.getMobile() + ","));
            if (users.size() > 0){
                builder.setLength(builder.length() - 1);
                logger.debug("Sending sms to = {}, message = {}",builder.toString(), message);
                in.andonsystem.util.MiscUtil.sendSMS(builder.toString(),message);
            }

            //Update processingAt
            issueService.updateProcessingAt(issue.getId(),2);
            //Submit FixTask(2 hrs, 2)
            Long fixL2Time = Long.parseLong(ConfigUtility.getInstance().getConfigProperty(Constants.APP_V2_FIX_L2_TIME, "120"));
            Scheduler.getInstance().submit(new FixTask(issue.getId(),2, message),fixL2Time);
        }else {
            logger.debug("Ignoring AckTask as Issue1 is acknowledged. issueId = {}", issueId);
        }


    }
}
