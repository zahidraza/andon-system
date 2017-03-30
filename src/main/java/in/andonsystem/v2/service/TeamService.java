package in.andonsystem.v2.service;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v1.util.MiscUtil;
import org.springframework.stereotype.Component;

@Component
public class TeamService {

    public String[] getTeams(){
        return MiscUtil.getInstance().getConfigProperty(Constants.TEAMS).split(";");
    }
}
