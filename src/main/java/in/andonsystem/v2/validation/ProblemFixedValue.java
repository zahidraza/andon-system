package in.andonsystem.v2.validation;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v1.util.MiscUtil;

/**
 * Created by razamd on 3/30/2017.
 */
public class ProblemFixedValue implements FixedValue {
    @Override
    public String[] getFixedValues() {
        return MiscUtil.getInstance().getConfigProperty(Constants.PROBLEMS).split(";");
    }
}
