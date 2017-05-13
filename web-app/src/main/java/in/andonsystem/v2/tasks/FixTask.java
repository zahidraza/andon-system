package in.andonsystem.v2.tasks;

import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.Issue2;
import in.andonsystem.v2.entity.User;
import in.andonsystem.v2.enums.Level;
import in.andonsystem.v2.service.IssueService;
import in.andonsystem.v2.util.ApplicationContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by razamd on 4/4/2017.
 */
public class FixTask extends Thread{
    private final Logger logger = LoggerFactory.getLogger(AckTask.class);

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

        Issue2 issue = issueService.findOne(issueId,true);
        System.out.println(issue);

        //If issue is not fixed yet and It is processing at level below or equal to checkProcessingAT
        if(issue.getFixAt() == null && issue.getProcessingAt() <= checkProcessingAt){
            //Send notification to level (checkProcessingAt + 1)
            System.out.println("Sending notification to level " + (checkProcessingAt+1) + " users");
            Buyer buyer = issue.getBuyer();
            List<User> users = buyer.getUsers().stream()
                                    .filter(user -> {
                                        if(checkProcessingAt == 1) return user.getLevel().equalsIgnoreCase(Level.LEVEL2.getValue());
                                        else return user.getLevel().equalsIgnoreCase(Level.LEVEL3.getValue()) ;
                                    })
                                    .collect(Collectors.toList());

            StringBuilder builder = new StringBuilder();
            users.forEach(user -> builder.append(user.getMobile() + ","));
            if (users.size() > 0){
                builder.setLength(builder.length() - 1);
                logger.debug("Sending sms to = {}, message = {}",builder.toString(), message);
                in.andonsystem.v2.util.MiscUtil.sendSMS(builder.toString(),message);
            }

            //update processing at value = (checkProcessingAt + 1)
            issueService.updateProcessingAt(issueId, (checkProcessingAt+1));
        }else {
            logger.debug("Ignoring FixTask as issue is fixed. issueId = {}", issueId);
        }

    }
}
