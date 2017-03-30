package in.andonsystem.v2.validation;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v1.util.MiscUtil;
import in.andonsystem.v2.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by razamd on 3/30/2017.
 */
public class TeamFixedValue implements FixedValue{

    @Override
    public String[] getFixedValues() {
        return MiscUtil.getInstance().getConfigProperty(Constants.TEAMS).split(";");
    }
}
