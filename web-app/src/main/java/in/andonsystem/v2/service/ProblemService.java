package in.andonsystem.v2.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import org.springframework.stereotype.Component;

/**
 * Created by razamd on 3/30/2017.
 */
@Component
public class ProblemService {

    public String[] getProblems(){
        return ConfigUtility.getInstance().getConfigProperty(Constants.PROBLEMS).split(";");
    }
}
