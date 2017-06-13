package in.andonsystem.v2.restcontroller;

import in.andonsystem.Constants;
import in.andonsystem.v2.dto.FieldError;
import in.andonsystem.v2.dto.IssueDto;
import in.andonsystem.v2.dto.IssuePatchDto;
import in.andonsystem.v2.dto.RestError;
import in.andonsystem.v2.service.BuyerService;
import in.andonsystem.v2.service.IssueService;
import in.andonsystem.v2.service.UserService;
import in.andonsystem.v2.ApiUrls;
import in.andonsystem.util.MiscUtil;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by razamd on 3/30/2017.
 */
@RestController
@RequestMapping(ApiUrls.ROOT_URL_ISSUES)
public class IssueRestController {

    private final Logger logger = LoggerFactory.getLogger(IssueRestController.class);

    @Autowired
    IssueService issueService;

    @Autowired
    BuyerService buyerService;

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
        List<IssueDto > issues = null;
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
        logger.info("saveIssue()");
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
        if(!buyerService.exists(issueDto.getBuyerId())){
            String msg = "Buyer with buyerId = " + issueDto.getBuyerId() + " not found.";
            return new ResponseEntity<Object>(new RestError(404,40401,msg,"",""),HttpStatus.NOT_FOUND);
        }
        if(!userService.exists(issueDto.getRaisedBy())){
            String msg = "User with userId = " + issueDto.getRaisedBy() + " not found.";
            return new ResponseEntity<Object>(new RestError(404,40401,msg,"",""),HttpStatus.NOT_FOUND);
        }
        issueDto = issueService.save(issueDto);
        Link selfLink = linkTo(IssueRestController.class).slash(issueDto.getId()).withSelfRel();
        return ResponseEntity.created(URI.create(selfLink.getHref())).body(issueDto);
    }

    @PatchMapping(ApiUrls.URL_ISSUES_ISSUE)
    public ResponseEntity<?> updateIssue(@PathVariable("issueId") Long issueId, @RequestParam("operation") String operation, @Valid @RequestBody
            IssuePatchDto issueDto){
        logger.info("updateIssue(): id = {}, operation = {}", issueId,operation);
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
        if(operation.equalsIgnoreCase(Constants.OP_ACK) && issueDto.getAckBy() == null){
            fieldErrors.add(new FieldError("ackBy",null,"User acknowledging Issue1 cannot be null."));
            return new ResponseEntity<Object>(fieldErrors, HttpStatus.BAD_REQUEST);
        }
        else if(operation.equalsIgnoreCase(Constants.OP_FIX) && issueDto.getFixBy() == null){
            fieldErrors.add(new FieldError("fixBy",null,"User fixing Issue1 cannot be null."));
            return new ResponseEntity<Object>(fieldErrors, HttpStatus.BAD_REQUEST);
        }
        issueDto.setId(issueId);
        issueDto = issueService.update(issueDto, operation);
        return ResponseEntity.ok(issueDto);
    }


}
