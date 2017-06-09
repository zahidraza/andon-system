package in.andonsystem.v1.service;

import in.andonsystem.Constants;
import in.andonsystem.util.ConfigUtility;
import org.springframework.stereotype.Component;

@Component

/**
 * Created by Md Jawed Akhtar on 08-04-2017.
 */
public class DepartmentService {
    public String[] getDepartments(){

        return ConfigUtility.getInstance().getConfigProperty(Constants.DEPARTMENTS).split(";");
    }
}
