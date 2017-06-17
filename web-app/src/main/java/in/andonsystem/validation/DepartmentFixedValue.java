package in.andonsystem.validation;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
public class DepartmentFixedValue implements FixedValue {

    @Override
    public String[] getFixedValues() {
        return ConfigUtility.getInstance().getConfigProperty(Constants.DEPARTMENTS).split(";");
    }
}
