package in.andonsystem.v2.restcontroller;

import in.andonsystem.v2.service.TeamService;
import in.andonsystem.v2.ApiUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by razamd on 3/30/2017.
 */
@RestController
@RequestMapping(ApiUrls.ROOT_URL_TEAMS)
public class TeamRestController {
    private final Logger logger = LoggerFactory.getLogger(TeamRestController.class);

    @Autowired TeamService teamService;

    @GetMapping
    public ResponseEntity<?> getProblems(){
        logger.debug("getproblems");
        String[] teams = teamService.getTeams();
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }
}
