package in.andonsystem.v2.restcontroller;

import in.andonsystem.v2.service.TeamService;
import in.andonsystem.v2.util.ApiV2Urls;
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
@RequestMapping(ApiV2Urls.URL_USERS_TEAMS)
public class TeamRestController {

    @Autowired
    TeamService teamService;

    @GetMapping
    public ResponseEntity<?> getProblems(){
        String[] teams = teamService.getTeams();
        return new ResponseEntity<>(teams, HttpStatus.OK);
    }
}
