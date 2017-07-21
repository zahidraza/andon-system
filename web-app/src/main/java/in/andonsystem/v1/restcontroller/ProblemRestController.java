package in.andonsystem.v1.restcontroller;

import in.andonsystem.v1.entity.Problem;
import in.andonsystem.v1.service.ProblemService;
import in.andonsystem.v1.ApiUrls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@RestController(value = "problemController")
@RequestMapping(ApiUrls.Root_URL_PROBLEMS)
public class ProblemRestController {
    private final Logger logger = LoggerFactory.getLogger(ProblemRestController.class);

    @Autowired ProblemService problemService;

    @GetMapping
    public ResponseEntity<?> getProblems(){
        logger.debug("getProblems()");
        List<Problem> problems = problemService.findAll();
        return ResponseEntity.ok(problems);
    }


}
