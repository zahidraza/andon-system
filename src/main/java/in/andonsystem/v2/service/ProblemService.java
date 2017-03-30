package in.andonsystem.v2.service;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v1.util.MiscUtil;
import org.springframework.stereotype.Component;

/**
 * Created by razamd on 3/30/2017.
 */
@Component
public class ProblemService {

    public String[] getProblems(){
        return MiscUtil.getInstance().getConfigProperty(Constants.PROBLEMS).split(";");
    }
}
