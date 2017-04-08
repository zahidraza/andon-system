package in.andonsystem.v1.service;

import in.andonsystem.v1.util.Constants;
import in.andonsystem.v1.util.MiscUtil;
import org.springframework.stereotype.Component;

@Component

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
public class DepartmentService {
    public String[] getDepartments(){

        return MiscUtil.getInstance().getConfigProperty(Constants.DEPARTMENTS).split(";");
    }
}
