package in.andonsystem.v1.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.util.Scheduler;
import in.andonsystem.v1.dto.IssueDto;
import in.andonsystem.v1.dto.IssuePatchDto;
import in.andonsystem.v1.entity.Issue1;
import in.andonsystem.v1.entity.Problem;
import in.andonsystem.v1.repository.IssueRepository;
import in.andonsystem.v1.repository.ProblemRepository;
import in.andonsystem.v2.entity.User;
import in.andonsystem.Level;
import in.andonsystem.v2.respository.UserRespository;
import in.andonsystem.v1.task.AckTask;
import in.andonsystem.v1.task.FixTask;
import org.dozer.Mapper;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@Service("issueService1")
@Transactional(readOnly = true)
public class IssueService {
    private final Logger logger = LoggerFactory.getLogger(IssueService.class);

    private final IssueRepository issueRepository;

    private final UserRespository userRespository;

    private final ProblemRepository problemRepository;

    @Autowired private SectionService sectionService;

    @Autowired private DepartmentService departmentService;

    private final Mapper mapper;

    @Autowired
    public IssueService(IssueRepository issueRepository, UserRespository userRespository, ProblemRepository problemRepository, Mapper mapper) {
        this.issueRepository = issueRepository;
        this.userRespository = userRespository;
        this.problemRepository = problemRepository;
        this.mapper = mapper;
    }

    public Issue1 findOne(Long id, Boolean initUsers){
        logger.debug("findOne(): id = {}, initUsers = {}", id, initUsers);
        Issue1 issue = issueRepository.findOne(id);
        if (initUsers){
            Problem problem = issue.getProblem();
            Hibernate.initialize(problem.getDesignations());
            problem.getDesignations().forEach(designation -> Hibernate.initialize(designation.getUsers()));
        }
        return issue;
    }

    public List<IssueDto> findAllAfter(Long after){
        logger.debug("findAllAfter:v1 after = " + after);
        Date date = MiscUtil.getTodayMidnight();
        //If after value is greater than today midnight value, then return issues after this value, else return issue after today midnight
        if(after > date.getTime()){
            date = new Date(after);
        }
        return issueRepository.findByLastModifiedGreaterThan(date).stream()
                .map(issue -> mapper.map(issue, IssueDto.class))
                .collect(Collectors.toList());
    }

    public List<IssueDto> findAllBetween(Long start, Long end){
        logger.debug("findAllBetween:v1 start = {}, end = {}", start, end);
        Date date1 = new Date(start);
        Date date2 = new Date(end);
        return issueRepository.findByLastModifiedBetweenOrderByRaisedAtDesc(date1,date2).stream()
                .map(issue -> mapper.map(issue, IssueDto.class))
                .collect(Collectors.toList());
    }

    @Transactional()
    public IssueDto save(IssueDto issueDto){
        logger.debug("save()");
        Issue1 issue = mapper.map(issueDto, Issue1.class);
        issue.setRaisedAt(new Date());
        issue.setRaisedBy(userRespository.findOne(issueDto.getRaisedBy()));
        issue.setProcessingAt(1);
        issue.setSeekHelp(0) ;
        if (issue.getDeleted() == null) issue.setDeleted(false);
        issue = issueRepository.save(issue);

        //Submit task to scheduler
        Scheduler scheduler = Scheduler.getInstance();

        Properties configProps = ConfigUtility.getInstance().getConfigProps();
        Long ackTime = Long.parseLong(configProps.getProperty(Constants.APP_V1_ACK_TIME,"5"));
        Long fixL1Time = Long.parseLong(configProps.getProperty(Constants.APP_V1_FIX_L1_TIME,"10"));
        Long fixL2Time = Long.parseLong(configProps.getProperty(Constants.APP_V1_FIX_L2_TIME,"10"));

        Problem problem = problemRepository.findOne(issue.getProblem().getId());
        String message = generateMessage(issue, problem);
        sendMessage(problem, message,issue.getId());

        scheduler.submit(new AckTask(issue.getId(), message), ackTime);
        scheduler.submit(new FixTask(issue.getId(),1, message),ackTime + fixL1Time);
        scheduler.submit(new FixTask(issue.getId(),2, message),ackTime + fixL1Time+ fixL2Time);

        return mapper.map(issue,IssueDto.class);
    }

    @Transactional
    public IssuePatchDto update(IssuePatchDto issuePatchDto, String operation){
        logger.debug("update: issueId = {}, operation = {}", issuePatchDto.getId(), operation);
        Issue1 issue = issueRepository.findOne(issuePatchDto.getId());

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
            String message = generateMessage(issue,problemRepository.findOne(issue.getProblem().getId()));
            message = message.replace("raised","fixed");
            MiscUtil.sendSMS(to,message);
        }
        else if (operation.equalsIgnoreCase(Constants.OP_SEEK_HELP)) {
            // seekHelp = Which level user is seeking help
            // save seekHelp value, update proceesing at to next level and send sms to next level
            // ignore if same level user is seeking help
            if (issue.getSeekHelp() != issuePatchDto.getSeekHelp()){
                issue.setSeekHelp(issuePatchDto.getSeekHelp());
                issue.setProcessingAt(issue.getSeekHelp()+1);

                Level level = issuePatchDto.getSeekHelp() == 1 ? Level.LEVEL2 : Level.LEVEL3;

                //Send Sms to concerned Users
                String message = generateMessage(issue,issue.getProblem());
                String mobNumbers =  MiscUtil.getUserMobileNumbers(issue.getProblem(), level);
                if (mobNumbers != null) {
                    boolean result = MiscUtil.sendSMS(mobNumbers,message);
                    if (result) {
                        logger.info("Sent sms to = {}, issueId-1 = {}",mobNumbers, issue.getId());
                    }
                }else {
                    logger.info("No Users found for sending sms");
                }
                //Schedule Fix task at fix.level2.time interval
                Long fixL2Time = Long.parseLong(ConfigUtility.getInstance().getConfigProperty(Constants.APP_V1_FIX_L2_TIME, "10"));
                Scheduler.getInstance().submit(new FixTask(issue.getId(),2, message),fixL2Time);
            }

        }
        return mapper.map(issue,IssuePatchDto.class);
    }

    @Transactional
    public void updateProcessingAt(Long issueId, Integer processinAt){
        logger.debug("updateProcessingAt(): issueId = {}, processingAt = {}", issueId, processinAt);
        Issue1 issue = issueRepository.findOne(issueId);
        if(issue != null){
            issue.setProcessingAt(processinAt);
        }else {
            logger.warn("failed to update processingAt value since Issue1 with id = {} does not exist ",issueId);
        }
    }

    public Boolean exists(Long id){
        return issueRepository.exists(id);
    }

    private String generateMessage(Issue1 issue, Problem problem){
        StringBuilder builder = new StringBuilder();
        builder.append("Problem raised with details-");
        builder.append("\nLine: " + issue.getLine());
        builder.append("\nSection: " + issue.getSection());
        builder.append("\nDepartment: " + problem.getDepartment());
        builder.append("\nProblem: " + problem.getName());
        builder.append("\nRemarks: " + issue.getDescription());
        return builder.toString();
    }

    private void sendMessage(Problem problem, String message, Long issueId){
        String mobileNumbers = MiscUtil.getUserMobileNumbers(problem, Level.LEVEL1);

        if (mobileNumbers != null) {
            boolean result = MiscUtil.sendSMS(mobileNumbers,message);
            if (result){
                logger.info("Sent sms to = {}, issueId-1 = {}",mobileNumbers, issueId);
            }
        }else {
            logger.info("No mobile numbers found");
        }
    }

    @Transactional
    public void autoFixIssues() {
        logger.info("Auto Fixing non fixed Issues");
        User user = userRespository.findOne(55554L);
        List<Issue1> list = issueRepository.findByProcessingAtLessThanAndRaisedAtGreaterThan(4,MiscUtil.getTodayMidnight());
        list.forEach(issue1 -> {
            if (issue1.getAckAt() == null) {
                issue1.setAckAt(new Date());
                issue1.setAckBy(user);
            }
            issue1.setFixAt(new Date());
            issue1.setFixBy(user);
            issue1.setProcessingAt(4);
        });
    }

    public Map<String, Long> getDowntimeLineWise(Long after) {
        Map<String, Long> downtime = new HashMap<>();
        Date date = new Date(after);
        List<Issue1> issues = issueRepository.findByRaisedAtGreaterThanAndDeleted(date,false);

        for (int i = 1; i <= 8; i++) {
            final int j = i;
            List<Long> downtimes =
                    issues.stream()
                            .filter(issue -> issue.getLine().equals(j))
                            .map(issue2 -> getDowntime(issue2.getFixAt(),issue2.getRaisedAt()))
                            .collect(Collectors.toList());
            downtime.put("Line " + i,getSum(downtimes));
        }
        return downtime;
    }

    public Map<String, Map<String, Long>> getDowntimeDepartmentWiseWithinLine(Long after) {
        Map<String, Map<String, Long>> downtime = new HashMap<>();
        Date date = new Date(after);
        List<Issue1> issues = issueRepository.findByRaisedAtGreaterThanAndDeleted(date,false);
        String[] departments = departmentService.getDepartments();

        for (int i = 1; i <= 8; i++) {
            final int j = i;
            List<Issue1> lineIssues =
                    issues.stream()
                            .filter(issue -> issue.getLine().equals(j))
                            .collect(Collectors.toList());
            downtime.put("Line " + i ,getSumDepartmentWise(lineIssues, departments));
        }
        return downtime;
    }

    public Map<String, Long> getDowntimeDepartmentWise(Long after) {
        Map<String, Long> downtime = new HashMap<>();
        Date date = new Date(after);
        List<Issue1> issues = issueRepository.findByRaisedAtGreaterThanAndDeleted(date,false);
        String[] departments = departmentService.getDepartments();

        for (String department: departments) {
            List<Long> downtimes =
                    issues.stream()
                            .filter(issue -> issue.getProblem().getDepartment().equalsIgnoreCase(department))
                            .map(issue -> getDowntime(issue.getFixAt(),issue.getRaisedAt()))
                            .collect(Collectors.toList());
            downtime.put(department,getSum(downtimes));
        }
        return downtime;
    }

    public Map<String, Map<String, Long>> getDowntimeProblemWiseWithinDepartment(Long after) {
        Map<String, Map<String, Long>> downtime = new HashMap<>();
        Date date = new Date(after);
        List<Issue1> issues = issueRepository.findByRaisedAtGreaterThanAndDeleted(date,false);
        String[] departments = departmentService.getDepartments();

        for (String department: departments) {
            List<Issue1> deptIssues =
                    issues.stream()
                            .filter(issue -> issue.getProblem().getDepartment().equalsIgnoreCase(department))
                            .collect(Collectors.toList());
            List<Problem> problems = problemRepository.findByDepartment(department);
            downtime.put(department,getSumProblemWise(deptIssues,problems));
        }
        return downtime;
    }

    private long getDowntime(Date fixAt, Date raisedAt) {
        if (fixAt == null) return 0L;
        long diff = fixAt.getTime() - raisedAt.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(diff);
    }

    private Map<String, Long> getSumDepartmentWise(List<Issue1> issues, String[] departments) {
        Map<String, Long> map = new HashMap<>();

        for (String department: departments) {
            map.put(department,
                    getSum(issues.stream()
                            .filter(issue -> issue.getProblem().getDepartment().equalsIgnoreCase(department))
                            .map(issue -> getDowntime(issue.getFixAt(),issue.getRaisedAt()))
                            .collect(Collectors.toList())
                    )
            );
        }
        return map;
    }

    private Map<String, Long> getSumProblemWise(List<Issue1> issues, List<Problem> problems) {
        Map<String, Long> map = new HashMap<>();

        problems.forEach(problem -> {
            map.put(problem.getName(),
                    getSum(issues.stream()
                            .filter(issue -> issue.getProblem().getName().equalsIgnoreCase(problem.getName()))
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

