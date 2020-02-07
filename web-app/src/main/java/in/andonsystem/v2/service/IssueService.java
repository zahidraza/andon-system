package in.andonsystem.v2.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.v2.dto.IssueDto;
import in.andonsystem.v2.dto.IssuePatchDto;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.Issue2;
import in.andonsystem.v2.entity.User;
import in.andonsystem.Level;
import in.andonsystem.v2.respository.BuyerRepository;
import in.andonsystem.v2.respository.IssueRepository;
import in.andonsystem.v2.respository.UserRespository;
import in.andonsystem.v2.task.AckTask;
import in.andonsystem.v2.task.FixTask;
import in.andonsystem.util.Scheduler;
import org.dozer.Mapper;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by razamd on 3/30/2017.
 */
@Service("issueService2")
@Transactional(readOnly = true)
public class IssueService {
    private final Logger logger = LoggerFactory.getLogger(IssueService.class);

    private final IssueRepository issueRepository;

    private final UserRespository userRespository;

    private final BuyerRepository buyerRepository;

    @Autowired TeamService teamService;

    @Autowired ProblemService problemService;

    private final Mapper mapper;

    @Autowired
    public IssueService(IssueRepository issueRepository, UserRespository userRespository, BuyerRepository
            buyerRepository, Mapper
                        mapper) {
        this.issueRepository = issueRepository;
        this.userRespository = userRespository;
        this.buyerRepository = buyerRepository;
        this.mapper = mapper;
    }

    public Issue2 findOne(Long id, Boolean initUsers){
        logger.debug("findOne: id = {}, initUsers = {}", id, initUsers);
        Issue2 issue = issueRepository.findOne(id);
        if(initUsers){
            Buyer buyer = issue.getBuyer();
            Hibernate.initialize(buyer.getUsers());
        }
        return issue;
    }

    public List<IssueDto> findAllAfter(Long after){
        logger.debug("findAllAfter: after = " + after);
        Date date = MiscUtil.getYesterdayMidnight();
        //If after value is greater than last two day midnight value, then return issues after this value, else return issue after last two day midnight
        if(after > date.getTime()){
            date = new Date(after);
        }
        return issueRepository.findByLastModifiedGreaterThan(date).stream()
                .filter(issue2 -> !issue2.getDeleted())
                .map(issue -> mapper.map(issue, IssueDto.class))
                .collect(Collectors.toList());
    }

    public List<IssueDto> findAllBetween(Long start, Long end){
        logger.debug("findAllBetween: start = {}, end = {}", start, end);
        Date date1 = new Date(start);
        Date date2 = new Date(end);
        return issueRepository.findByLastModifiedBetweenOrderByRaisedAtDesc(date1,date2).stream()
                              .filter(issue2 -> !issue2.getDeleted())
                              .map(issue -> mapper.map(issue, IssueDto.class))
                              .collect(Collectors.toList());
    }

    @Transactional()
    public IssueDto save(IssueDto issueDto){
        logger.debug("save()");
        Issue2 issue = mapper.map(issueDto, Issue2.class);
        issue.setRaisedAt(new Date());
        issue.setRaisedBy(userRespository.findOne(issueDto.getRaisedBy()));
        issue.setProcessingAt(1);
        if (issue.getDeleted() == null) issue.setDeleted(false);
        issue = issueRepository.save(issue);

        //Submit task to scheduler
        Scheduler scheduler = Scheduler.getInstance();

        ConfigUtility configUtility = ConfigUtility.getInstance();
        Long ackTime = Long.parseLong(configUtility.getConfigProperty(Constants.APP_V2_ACK_TIME,"30"));
        Long fixL1Time = Long.parseLong(configUtility.getConfigProperty(Constants.APP_V2_FIX_L1_TIME,"180"));
        Long fixL2Time = Long.parseLong(configUtility.getConfigProperty(Constants.APP_V2_FIX_L2_TIME,"120"));

        Buyer buyer = buyerRepository.findOne(issue.getBuyer().getId());
        String message = generateMessage(issue, buyer);
        sendMessage(buyer, message, issue.getId());

        scheduler.submit(new AckTask(issue.getId(), message), ackTime);
        scheduler.submit(new FixTask(issue.getId(),1, message),fixL1Time);
        scheduler.submit(new FixTask(issue.getId(),2, message),fixL1Time+ fixL2Time);

        return mapper.map(issue,IssueDto.class);
    }

    @Transactional
    public IssuePatchDto update(IssuePatchDto issuePatchDto, String operation){
        logger.debug("update: id = {}, operation = {}", issuePatchDto.getId(), operation);
        Issue2 issue = issueRepository.findOne(issuePatchDto.getId());

        if(operation.equalsIgnoreCase(Constants.OP_ACK)){
            issue.setAckBy(userRespository.findOne(issuePatchDto.getAckBy()));
            issue.setAckAt(new Date());

        }
        else if (operation.equalsIgnoreCase(Constants.OP_DEL)) {
            issue.setDeleted(true);
        }
        else if(operation.equalsIgnoreCase(Constants.OP_FIX)){
            User user = userRespository.findOne(issuePatchDto.getFixBy());
            if (issue.getAckAt() == null){
                issue.setAckAt(new Date());
                issue.setAckBy(user);
            }
            issue.setFixBy(user);
            issue.setFixAt(new Date());
            issue.setProcessingAt(4);
            //Send Message to user who raised issue
            String to = issue.getRaisedBy().getMobile();
            String message = generateMessage(issue,buyerRepository.findOne(issue.getBuyer().getId()));
            message = message.replace("raised","fixed");
            in.andonsystem.util.MiscUtil.sendSMS(to,message);
        }
        return mapper.map(issue,IssuePatchDto.class);
    }

    @Transactional
    public void updateProcessingAt(Long issueId, Integer processinAt){
        logger.debug("updateProcessingAt(): issueId = {}, processingAt = {}", issueId, processinAt);
        Issue2 issue = issueRepository.findOne(issueId);
        if(issue != null){
            issue.setProcessingAt(processinAt);
        }else {
            logger.warn("failed to update processingAt value since Issue1 with id = {} does not exist ",issueId);
        }
    }

    public Boolean exists(Long id){
        return issueRepository.exists(id);
    }

    @Transactional
    public void autoFixIssues() {
        logger.info("Auto Fixing non fixed Issues");
        User user = userRespository.findOne(55554L);
        List<Issue2> list = issueRepository.findByProcessingAtLessThanAndRaisedAtLessThanAndDeleted(4,MiscUtil.getTodayMidnight(),false);
        list.forEach(issue -> {
            if (issue.getAckAt() == null) {
                issue.setAckAt(new Date());
                issue.setAckBy(user);
            }
            issue.setFixAt(new Date());
            issue.setFixBy(user);
            issue.setProcessingAt(4);
        });
    }

    public Map<String, Long> getDowntimeProblemwise(Long after) {
        Map<String, Long> downtime = new HashMap<>();
        Date date = new Date(after);
        List<Issue2> issues = issueRepository.findByRaisedAtGreaterThanAndDeleted(date,false);
        String[] problems = problemService.getProblems();

        for (String problem: problems) {
            List<Long> downtimes =
                    issues.stream()
                            .filter(issue2 -> issue2.getProblem().equalsIgnoreCase(problem))
                            .map(issue2 -> getDowntime(issue2.getFixAt(),issue2.getRaisedAt()))
                            .collect(Collectors.toList());
            downtime.put(problem,getSum(downtimes));
        }
        return downtime;
    }

    public Map<String, Long> getDowntimeTeamwise(Long after) {
        Map<String, Long> downtime = new HashMap<>();
        Date date = new Date(after);
        List<Issue2> issues = issueRepository.findByRaisedAtGreaterThanAndDeleted(date,false);
        String[] teams = teamService.getTeams();

        for (String team: teams) {
            List<Long> downtimes =
                    issues.stream()
                        .filter(issue2 -> issue2.getBuyer().getTeam().equalsIgnoreCase(team))
                        .map(issue2 -> getDowntime(issue2.getFixAt(),issue2.getRaisedAt()))
                        .collect(Collectors.toList());
            downtime.put(team,getSum(downtimes));
        }
        return downtime;
    }

    public Map<String, Map<String, Long>> getDowntimeBuyerwise(Long after) {
        Map<String, Map<String, Long>> downtime = new HashMap<>();
        Date date = new Date(after);
        List<Issue2> issues = issueRepository.findByRaisedAtGreaterThanAndDeleted(date,false);
        String[] teams = teamService.getTeams();

        for (String team: teams) {
            List<Issue2> teamIssues =
                    issues.stream()
                            .filter(issue2 -> issue2.getBuyer().getTeam().equalsIgnoreCase(team))
                            .collect(Collectors.toList());
            List<Buyer> buyers = buyerRepository.findByTeam(team);
            downtime.put(team,getSumBuyerwise(teamIssues, buyers));
        }
        return downtime;
    }

    public Map<String, Long> getDowntimeTop5Buyers(Long after) {
        Map<String, Long> downtime, result = new HashMap<>();
        Date date = new Date(after);
        List<Issue2> issues = issueRepository.findByRaisedAtGreaterThanAndDeleted(date,false);

        List<Buyer> buyers = buyerRepository.findAll();
        downtime = getSumBuyerwise(issues, buyers);
        downtime = MiscUtil.sortByValueDesc(downtime);
        int i = 0;
        long sum = 0;
        for (Map.Entry<String,Long> entry: downtime.entrySet()) {
            if (i < 5) {
                result.put(entry.getKey(), entry.getValue());
            }else {
                sum += entry.getValue();
            }
            i++;
        }
        result.put("Others",sum);
        return result;
    }

    //////////////////////////////////////////////////////////////////////////
    private String generateMessage(Issue2 issue, Buyer buyer){
        StringBuilder builder = new StringBuilder();
        builder.append("Problem raised with details-");
        builder.append("\nTeam: " + buyer.getTeam());
        builder.append("\nBuyer: " + buyer.getName());
        builder.append("\nProblem: " + issue.getProblem());
        builder.append("\nRaised By: " + issue.getRaisedBy().getName());
        builder.append("\nRemarks: " + issue.getDescription());
        return builder.toString();
    }

    private void sendMessage(Buyer buyer, String message, Long issueId){
        String mobileNumbers = MiscUtil.getUserMobileNumbers(buyer,Level.LEVEL1);
        if (mobileNumbers != null) {
            boolean result = MiscUtil.sendSMS(mobileNumbers,message);
            if (result) {
                logger.info("Sent sms to = {}, issueId-2 = {}",mobileNumbers, issueId);
            }
        }else {
            logger.info("No Users found for sending sms");
        }
    }

    private long getDowntime(Date fixAt, Date raisedAt) {
        if (fixAt == null) return 0L;
        long rDays = TimeUnit.MILLISECONDS.toDays(raisedAt.getTime());
        long fDays = TimeUnit.MILLISECONDS.toDays(fixAt.getTime());
        long diff = fixAt.getTime() - raisedAt.getTime() - (fDays-rDays)*((14*60 + 30)*60*1000); //14 hours 30 minutes
        return TimeUnit.MILLISECONDS.toMinutes(diff);
    }

    private Map<String, Long> getSumBuyerwise(List<Issue2> issues, List<Buyer> buyers) {
        Map<String, Long> map = new HashMap<>();

        buyers.forEach(buyer -> {
            map.put(buyer.getName(),
                        getSum(issues.stream()
                            .filter(issue -> issue.getBuyer().getName().equalsIgnoreCase(buyer.getName()))
                            .map(issue -> getDowntime(issue.getFixAt(),issue.getRaisedAt()))
                            .collect(Collectors.toList())
                        )
            );
        });

        return map;
    }

    private Long getSum(List<Long> downtimes) {
        Long sum = 0L;
        for (Long d: downtimes) {
            sum += d;
        }
        return sum;
    }
}
