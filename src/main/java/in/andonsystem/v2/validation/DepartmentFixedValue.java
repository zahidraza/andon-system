package in.andonsystem.v2.validation;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v1.util.MiscUtil;

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
public class DepartmentFixedValue implements FixedValue {

    @Override
    public String[] getFixedValues() {
        return MiscUtil.getInstance().getConfigProperty(Constants.DEPARTMENTS).split(";");
    }
}
