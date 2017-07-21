package in.andonsystem.v2.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TeamService {
    private final Logger logger = LoggerFactory.getLogger(TeamService.class);

    public String[] getTeams(){
        logger.debug("getTeams()");
        return ConfigUtility.getInstance().getConfigProperty(Constants.TEAMS).split(";");
    }
}
