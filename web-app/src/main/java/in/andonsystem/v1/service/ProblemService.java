package in.andonsystem.v1.service;

import in.andonsystem.v1.entity.Problem;
import in.andonsystem.v1.repository.ProblemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
@Service(value = "pService")
public class ProblemService {
    private final Logger logger = LoggerFactory.getLogger(ProblemService.class);

    @Autowired ProblemRepository problemRepository;

    public List<Problem> findAll() {
        logger.debug("findAll()");
        return problemRepository.findAll();
    }

    public boolean exists(Long problemId) {
        return problemRepository.exists(problemId);
    }

}
