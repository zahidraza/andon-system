package in.andonsystem.v2.service;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v1.util.MiscUtil;
import in.andonsystem.v2.dto.IssueDto;
import in.andonsystem.v2.dto.IssuePatchDto;
import in.andonsystem.v2.entity.Buyer;
import in.andonsystem.v2.entity.Issue2;
import in.andonsystem.v2.entity.User;
import in.andonsystem.v2.enums.Level;
import in.andonsystem.v2.respository.BuyerRepository;
import in.andonsystem.v2.respository.IssueRepository;
import in.andonsystem.v2.respository.UserRespository;
import in.andonsystem.v2.tasks.AckTask;
import in.andonsystem.v2.tasks.FixTask;
import in.andonsystem.v2.util.Scheduler;
import org.dozer.Mapper;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by razamd on 3/30/2017.
 */
@Service
@Transactional(readOnly = true)
public class IssueService {

    private final Logger logger = LoggerFactory.getLogger(IssueService.class);

    private final IssueRepository issueRepository;

    private final UserRespository userRespository;

    private final BuyerRepository buyerRepository;

    private final Mapper mapper;

    @Autowired
    public IssueService(IssueRepository issueRepository, UserRespository userRespository,BuyerRepository
            buyerRepository, Mapper
                        mapper) {
        this.issueRepository = issueRepository;
        this.userRespository = userRespository;
        this.buyerRepository = buyerRepository;
        this.mapper = mapper;
    }

    public Issue2 findOne(Long id, Boolean initUsers){
        Issue2 issue = issueRepository.findOne(id);
        if(initUsers){
            Buyer buyer = issue.getBuyer();
            Hibernate.initialize(buyer.getUsers());
        }
        return issue;
    }

    public List<IssueDto> findAllAfter(Long after){
        logger.debug("findAllAfter: after = " + after);
        Date date = in.andonsystem.v2.util.MiscUtil.getTodayMidnight();
        //If after value is greater than today midnight value, then return issues after this value, else return issue after today's midnight
        if(after > date.getTime()){
            date = new Date(after);
        }
        return issueRepository.findByLastModifiedGreaterThan(date).stream()
                .map(issue -> mapper.map(issue, IssueDto.class))
                .collect(Collectors.toList());
    }

    public List<IssueDto> findAllBetween(Long start, Long end){
        Date date1 = new Date(start);
        Date date2 = new Date(end);
        return issueRepository.findByLastModifiedBetween(date1,date2).stream()
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
        issue = issueRepository.save(issue);

        //Submit tasks to scheduler
        Scheduler scheduler = Scheduler.getInstance();

        MiscUtil miscUtil = MiscUtil.getInstance();
        Long ackTime = Long.parseLong(miscUtil.getConfigProperty(Constants.APP_V2_ACK_TIME,"30"));
        Long fixL1Time = Long.parseLong(miscUtil.getConfigProperty(Constants.APP_V2_FIX_L1_TIME,"180"));
        Long fixL2Time = Long.parseLong(miscUtil.getConfigProperty(Constants.APP_V2_FIX_L2_TIME,"120"));

        Buyer buyer = buyerRepository.findOne(issue.getBuyer().getId());
        String message = generateMessage(issue, buyer);
        sendMessage(buyer, message);

        scheduler.submit(new AckTask(issue.getId(), message), ackTime);
        scheduler.submit(new FixTask(issue.getId(),1, message),fixL1Time);
        scheduler.submit(new FixTask(issue.getId(),2, message),fixL1Time+ fixL2Time);

        return mapper.map(issue,IssueDto.class);
    }

    @Transactional
    public IssuePatchDto update(IssuePatchDto issuePatchDto, String operation){
        Issue2 issue = issueRepository.findOne(issuePatchDto.getId());

        if(operation.equalsIgnoreCase(Constants.OP_ACK)){
            issue.setAckBy(userRespository.findOne(issuePatchDto.getAckBy()));
            issue.setAckAt(new Date());
        }else if(operation.equalsIgnoreCase(Constants.OP_FIX)){
            issue.setFixBy(userRespository.findOne(issuePatchDto.getFixBy()));
            issue.setFixAt(new Date());
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

    private String generateMessage(Issue2 issue, Buyer buyer){
        StringBuilder builder = new StringBuilder();
        builder.append("Problem raised with details-");
        builder.append("\nTeam: " + buyer.getTeam());
        builder.append("\nBuyer: " + buyer.getName());
        builder.append("\nProblem: " + issue.getProblem());
        builder.append("\nRemarks: " + issue.getDescription());
        return builder.toString();
    }

    private void sendMessage(Buyer buyer, String message){


        List<User> users = buyer.getUsers().stream()
                .filter(user -> user.getLevel().equalsIgnoreCase(Level.LEVEL1.getValue()))
                .collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        users.forEach(user -> builder.append(user.getMobile() + ","));
        if (users.size() > 0){
            builder.setLength(builder.length() - 1);
            logger.info("Sending sms to = {}, message = {}",builder.toString(), message);
            in.andonsystem.v2.util.MiscUtil.sendSMS(builder.toString(),message);
        }
    }
}
