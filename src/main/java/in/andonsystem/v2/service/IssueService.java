package in.andonsystem.v2.service;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v2.dto.IssueDto;
import in.andonsystem.v2.dto.IssuePatchDto;
import in.andonsystem.v2.entity.Issue;
import in.andonsystem.v2.respository.IssueRepository;
import in.andonsystem.v2.respository.UserRespository;
import in.andonsystem.v2.utils.MiscUtil;
import org.apache.tomcat.util.bcel.Const;
import org.dozer.Mapper;
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

    private final IssueRepository issueRepository;

    private final UserRespository userRespository;

    private final Mapper mapper;

    @Autowired
    public IssueService(IssueRepository issueRepository, UserRespository userRespository, Mapper mapper) {
        this.issueRepository = issueRepository;
        this.userRespository = userRespository;
        this.mapper = mapper;
    }

    public Issue findOne(Long id){
        return issueRepository.findOne(id);
    }

    public List<IssueDto> findAllAfter(Long after){
        Date date = MiscUtil.getTodayMidnight();
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

    @Transactional
    public IssueDto save(IssueDto issueDto){
        Issue issue = mapper.map(issueDto, Issue.class);
        issue.setRaisedAt(new Date());
        issue.setRaisedBy(userRespository.findOne(issueDto.getRaisedBy()));
        return mapper.map(issueRepository.save(issue),IssueDto.class);
    }

    @Transactional
    public IssuePatchDto update(IssuePatchDto issuePatchDto, String operation){
        Issue issue = issueRepository.findOne(issuePatchDto.getId());

        if(operation.equalsIgnoreCase(Constants.OP_ACK)){
            issue.setAckBy(userRespository.findOne(issuePatchDto.getAckBy()));
            issue.setAckAt(new Date());
        }else if(operation.equalsIgnoreCase(Constants.OP_FIX)){
            issue.setFixBy(userRespository.findOne(issuePatchDto.getFixBy()));
            issue.setFixAt(new Date());
        }
        return mapper.map(issue,IssuePatchDto.class);
    }

    public Boolean exists(Long id){
        return issueRepository.exists(id);
    }
}
