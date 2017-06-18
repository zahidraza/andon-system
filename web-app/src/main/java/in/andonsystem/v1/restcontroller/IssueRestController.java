package in.andonsystem.v1.restcontroller;

import in.andonsystem.Constants;
import in.andonsystem.Level;
import in.andonsystem.util.MiscUtil;
import in.andonsystem.v1.ApiUrls;
import in.andonsystem.v1.dto.IssueDto;
import in.andonsystem.v1.dto.IssuePatchDto;
import in.andonsystem.v1.service.IssueService;
import in.andonsystem.v1.service.ProblemService;
import in.andonsystem.v2.dto.FieldError;
import in.andonsystem.v2.dto.RestError;
import in.andonsystem.v2.service.UserService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;


/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@RestController(value = "issueController")
@RequestMapping(ApiUrls.ROOT_URL_ISSUES)
public class IssueRestController {
    private final Logger logger = LoggerFactory.getLogger(IssueRestController.class);

    @Autowired
    IssueService issueService;

    @Autowired
    ProblemService problemService;

    @Autowired
    UserService userService;

    /**
     * case 1: (start = 0 &  end = 0), Return last two day issues
     * case 2: (start != 0 &  end = 0), Return issues after start if start is greater than last two day
     * case 3: (start != 0 &  end != 0), Return issues in between start and end
     * case 4: (start = 0 &  end != 0), not supported
     * @param start
     * @param end
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getAllIssueAfter(@RequestParam(value = "start", defaultValue = "0") Long start, @RequestParam(value = "end", defaultValue = "0") Long end){
        logger.debug("getAllIssueAfter: start = {} , end = {}", start, end);
        List<IssueDto> issues = null;
        if((start == 0L && end == 0L) || (start != 0L && end == 0L)){
            issues = issueService.findAllAfter(start);
        }else{
            issues = issueService.findAllBetween(start,end);
        }
        Map<String, Object> map = new HashedMap();
        map.put("issueSync", System.currentTimeMillis());
        map.put("issues", issues);
        return new ResponseEntity<>(map, HttpStatus.OK) ;
    }

    @PostMapping
    public ResponseEntity<?> saveIssue(@Valid @RequestBody IssueDto issueDto){
        logger.debug("saveIssue()");
        int value = MiscUtil.checkApp2Closed();
        Map<String,Object> resp = new HashedMap();
        if (value == -1){
            resp.put("status","OFFICE_NOT_OPENED");
            resp.put("message","City Office not opened yet.");
            return ResponseEntity.ok(resp);
        }else if (value == 1){
            resp.put("status","OFFICE_CLOSED");
            resp.put("message","City Office closed.");
            return ResponseEntity.ok(resp);
        }
        ////////////////////////////////////////////////////
        if(!problemService.exists(issueDto.getProblemId())){
            String msg = "Problem with problemId = " + issueDto.getProblemId() + " not found.";
            return new ResponseEntity<Object>(new RestError(404,40401,msg,"",""),HttpStatus.NOT_FOUND);
        }
        if(!userService.exists(issueDto.getRaisedBy())) {
            String msg = "User with userId = " + issueDto.getRaisedBy() + " not found.";
            return new ResponseEntity<Object>(new RestError(404,40401,msg,"",""),HttpStatus.NOT_FOUND);
        }
        else if (! userService.findOne(issueDto.getRaisedBy()).getLevel().equalsIgnoreCase(Level.LEVEL0.getValue())) {
            String msg = "Only Level 0 users can raise the Issue.";
            return new ResponseEntity<Object>(new RestError(409,40901,msg,"",""),HttpStatus.CONFLICT);
        }
        issueDto = issueService.save(issueDto);
        Link selfLink = linkTo(IssueRestController.class).slash(issueDto.getId()).withSelfRel();
        return ResponseEntity.created(URI.create(selfLink.getHref())).body(issueDto);
    }

    @PatchMapping(ApiUrls.URL_ISSUES_ISSUE)
    public ResponseEntity<?> updateIssue(@PathVariable("issueId") Long issueId, @RequestParam("operation") String operation, @Valid @RequestBody
            IssuePatchDto issueDto){
        logger.debug("updateIssue(): id = {}, operation = {}", issueId,operation);
        int value = MiscUtil.checkApp2Closed();
        Map<String,Object> resp = new HashedMap();
        if (value == -1){
            resp.put("status","210");
            resp.put("message","City Office not opened yet.");
            return ResponseEntity.ok(resp);
        }else if (value == 1){
            resp.put("status","211");
            resp.put("message","City Office closed.");
            return ResponseEntity.ok(resp);
        }
        /////////////////////////////////////////
        if(!issueService.exists(issueId)){
            String msg = "Issue1 with issueId = " + issueId + " not found.";
            return new ResponseEntity<Object>(new RestError(404,40401,msg,"",""),HttpStatus.NOT_FOUND);
        }
        List<FieldError> fieldErrors = new ArrayList<>();
        if(operation.equalsIgnoreCase(Constants.OP_ACK)){
            if (issueDto.getAckBy() == null) {
                fieldErrors.add(new FieldError("ackBy",null,"User acknowledging Issue1 cannot be null."));
                return new ResponseEntity<Object>(fieldErrors, HttpStatus.BAD_REQUEST);
            }
        }
        else if(operation.equalsIgnoreCase(Constants.OP_FIX)){
            if (issueDto.getFixBy() == null) {
                fieldErrors.add(new FieldError("fixBy",null,"User fixing Issue1 cannot be null."));
                return new ResponseEntity<Object>(fieldErrors, HttpStatus.BAD_REQUEST);
            }
        }
        else if (operation.equalsIgnoreCase(Constants.OP_SEEK_HELP)) {
            if (issueDto.getSeekHelp() == null) {
                fieldErrors.add(new FieldError("seekHelp",null,"User Level seeking hellp cannot be null"));
                return new ResponseEntity<Object>(fieldErrors, HttpStatus.BAD_REQUEST);
            }else if(!(issueDto.getSeekHelp() == 1 || issueDto.getSeekHelp() == 2)){
                fieldErrors.add(new FieldError("seekHelp",issueDto.getSeekHelp(),"Only level1 or level2 users can seek for help"));
                return new ResponseEntity<Object>(fieldErrors, HttpStatus.BAD_REQUEST);
            }
        }
        issueDto.setId(issueId);
        issueDto = issueService.update(issueDto, operation);
        return ResponseEntity.ok(issueDto);
    }
}
