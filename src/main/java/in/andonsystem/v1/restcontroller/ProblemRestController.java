package in.andonsystem.v1.restcontroller;

import in.andonsystem.v1.entity.Problem;
import in.andonsystem.v1.service.ProblemService;
import in.andonsystem.v1.util.ApiV1Urls;
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
@RequestMapping(ApiV1Urls.Root_URL_PROBLEMS)
public class ProblemRestController {

    @Autowired
    ProblemService problemService;

    @GetMapping
    public ResponseEntity<?> getProblems(){
        List<Problem> problems = problemService.findAll();
        return ResponseEntity.ok(problems);
    }


}
