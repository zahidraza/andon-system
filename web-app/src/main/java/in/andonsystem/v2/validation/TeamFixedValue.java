package in.andonsystem.v2.validation;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;

/**
 * Created by razamd on 3/30/2017.
 */
public class TeamFixedValue implements FixedValue{

    @Override
    public String[] getFixedValues() {
        return ConfigUtility.getInstance().getConfigProperty(Constants.TEAMS).split(";");
    }
}
