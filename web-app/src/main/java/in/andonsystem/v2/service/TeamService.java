package in.andonsystem.v2.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import org.springframework.stereotype.Component;

@Component
public class TeamService {

    public String[] getTeams(){
        return ConfigUtility.getInstance().getConfigProperty(Constants.TEAMS).split(";");
    }
}
