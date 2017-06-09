package in.andonsystem.v2.restcontroller;

import in.andonsystem.v2.service.ProblemService;
import in.andonsystem.v2.ApiUrls;
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
@RequestMapping(ApiUrls.ROOT_URL_PROBLEMS)
public class ProblemRestController {

    @Autowired
    ProblemService problemService;

    @GetMapping
    public ResponseEntity<?> getProblems(){
        String[] problems = problemService.getProblems();
        return new ResponseEntity<>(problems, HttpStatus.OK);
    }
}
