package in.andonsystem.v2.restcontroller;

import in.andonsystem.v2.dto.IssueDto;
import in.andonsystem.v2.dto.RestError;
import in.andonsystem.v2.entity.Issue;
import in.andonsystem.v2.service.BuyerService;
import in.andonsystem.v2.service.IssueService;
import in.andonsystem.v2.service.UserService;
import in.andonsystem.v2.utils.ApiV2Urls;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import javax.validation.Valid;
import java.net.URI;
import java.util.Date;

/**
 * Created by razamd on 3/30/2017.
 */
@RestController
@RequestMapping(ApiV2Urls.ROOT_URL_ISSUES)
public class IssueRestController {

    private final Logger logger = LoggerFactory.getLogger(IssueRestController.class);

    @Autowired
    IssueService issueService;

    @Autowired
    BuyerService buyerService;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllIssueAfter(@RequestParam(value = "after", defaultValue = "0") Long after){
        return new ResponseEntity<>(issueService.findAllAfter(after), HttpStatus.OK) ;
    }

    @PostMapping
    public ResponseEntity<?> saveIssue(@Valid @RequestBody IssueDto issueDto){
        logger.info("saveIssue()");
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

}
