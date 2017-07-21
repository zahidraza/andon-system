package in.andonsystem.v2.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by razamd on 3/30/2017.
 */
@Component
public class ProblemService {
    private final Logger logger = LoggerFactory.getLogger(ProblemService.class);

    public String[] getProblems(){
        logger.debug("getProblems()");
        return ConfigUtility.getInstance().getConfigProperty(Constants.PROBLEMS).split(";");
    }
}
