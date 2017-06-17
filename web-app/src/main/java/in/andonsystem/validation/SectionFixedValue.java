package in.andonsystem.validation;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;

/**
 * Created by mdzahidraza on 17/06/17.
 */
public class SectionFixedValue implements FixedValue{
    @Override
    public String[] getFixedValues() {
        return ConfigUtility.getInstance().getConfigProperty(Constants.SECTIONS).split(";");
    }
}
